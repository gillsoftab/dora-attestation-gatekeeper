package se.gillsoft.dora.gatekeeper.service;

import org.bouncycastle.openssl.PEMParser;
import org.springframework.stereotype.Service;
import se.gillsoft.dora.gatekeeper.model.*;
import se.gillsoft.dora.gatekeeper.model.EbaVerificationResponse.DoraCompliance;
import se.gillsoft.dora.gatekeeper.model.EbaVerificationResponse.KeyProperties;
import se.gillsoft.dora.gatekeeper.verification.AzureHsmVerifier;
import se.gillsoft.dora.gatekeeper.verification.GoogleCloudHsmVerifier;
import se.gillsoft.dora.gatekeeper.verification.SecurosysVerifier;
import se.gillsoft.dora.gatekeeper.verification.YubicoVerifier;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

/**
 * EBA Independent Verification Service.
 * 
 * Performs cryptographic verification of HSM attestation evidence
 * without requiring the entity's cooperation. The verification is
 * purely mathematical — the attestation chain either validates
 * against the HSM manufacturer's root CA or it does not.
 * 
 * This is the practical manifestation of the distinction between
 * contractual and cryptographic compliance: contractual compliance
 * requires trust in the counterparty's statements, while cryptographic
 * compliance through HSM attestation is independently verifiable.
 * 
 * This service is designed to be used by EBA under:
 * - Article 17(6) of Regulation 1093/2010 (breach of Union law investigation)
 * - Article 29 of Regulation 1093/2010 (supervisory convergence)
 */
@Service
public class EbaVerificationService {

    private final SecurosysVerifier securosysVerifier;
    private final YubicoVerifier yubicoVerifier;
    private final AzureHsmVerifier azureVerifier;
    private final GoogleCloudHsmVerifier googleVerifier;
    private final ApprovalRegistry approvalRegistry;

    public EbaVerificationService(
            SecurosysVerifier securosysVerifier,
            YubicoVerifier yubicoVerifier,
            AzureHsmVerifier azureVerifier,
            GoogleCloudHsmVerifier googleVerifier,
            ApprovalRegistry approvalRegistry) {
        this.securosysVerifier = securosysVerifier;
        this.yubicoVerifier = yubicoVerifier;
        this.azureVerifier = azureVerifier;
        this.googleVerifier = googleVerifier;
        this.approvalRegistry = approvalRegistry;
    }

    /**
     * Independently verify HSM attestation evidence.
     * 
     * @param request The attestation evidence to verify
     * @return Binary compliance determination with DORA article mapping
     */
    public EbaVerificationResponse verify(EbaVerificationRequest request) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        Instant timestamp = Instant.now();

        // Parse public key
        PublicKey publicKey;
        String keyAlgorithm;
        try {
            publicKey = parsePublicKey(request.getPublicKey());
            keyAlgorithm = publicKey.getAlgorithm();
        } catch (Exception e) {
            errors.add("Invalid public key: " + e.getMessage());
            return buildNonCompliantResponse(errors, warnings, timestamp, request);
        }

        String publicKeyFingerprint = fingerprint(publicKey);

        // Determine vendor
        HsmVendor vendor;
        try {
            vendor = HsmVendor.valueOf(request.getHsmVendor().toUpperCase());
        } catch (Exception e) {
            errors.add("Unsupported or invalid HSM vendor: " + request.getHsmVendor()
                    + ". Supported: YUBICO, SECUROSYS, AZURE, GOOGLE");
            return buildNonCompliantResponse(errors, warnings, timestamp, request);
        }

        // Perform vendor-specific attestation verification
        boolean publicKeyMatch = false;
        boolean attestationChainValid = false;
        boolean generatedOnDevice = false;
        boolean exportable = true;
        String hsmModel = null;
        String hsmSerial = null;

        switch (vendor) {
            case SECUROSYS -> {
                if (request.getAttestationData() == null) {
                    errors.add("attestationData (XML) is required for Securosys verification");
                    break;
                }
                if (request.getAttestationSignature() == null) {
                    errors.add("attestationSignature is required for Securosys verification");
                    break;
                }
                if (request.getAttestationCertChain() == null || request.getAttestationCertChain().isEmpty()) {
                    errors.add("attestationCertChain is required for Securosys verification");
                    break;
                }
                var result = securosysVerifier.verifySecurosysAttestation(
                        request.getAttestationData(),
                        request.getAttestationSignature(),
                        request.getAttestationCertChain(),
                        publicKey);
                publicKeyMatch = result.isPublicKeyMatch();
                attestationChainValid = result.isChainValid();
                generatedOnDevice = true; // Securosys: never_extractable=true means generated on device
                exportable = result.isExtractable();
                hsmModel = "Primus HSM";
                hsmSerial = result.getHsmSerialNumber();
                if (!result.isValid()) {
                    errors.addAll(result.getErrors());
                }
            }
            case YUBICO -> {
                if (request.getAttestationCertChain() == null || request.getAttestationCertChain().isEmpty()) {
                    errors.add("attestationCertChain is required for Yubico verification");
                    break;
                }
                var result = yubicoVerifier.verifyYubicoAttestation(
                        request.getAttestationCertChain(),
                        publicKey);
                publicKeyMatch = result.isPublicKeyMatch();
                attestationChainValid = result.isChainValid();
                generatedOnDevice = "generated".equals(result.getKeyOrigin());
                exportable = result.isKeyExportable();
                hsmModel = "YubiHSM 2";
                hsmSerial = result.getDeviceSerial();
                if (!result.isValid()) {
                    errors.addAll(result.getErrors());
                }
            }
            case AZURE -> {
                if (request.getAttestationData() == null || request.getAttestationData().isBlank()) {
                    errors.add("attestationData (JSON) is required for Azure verification");
                    break;
                }
                var result = azureVerifier.verifyAzureAttestation(
                        request.getAttestationData(),
                        publicKey);
                publicKeyMatch = result.isPublicKeyMatch();
                attestationChainValid = result.isChainValid();
                generatedOnDevice = "generated".equals(result.getKeyOrigin());
                exportable = result.isExportable();
                hsmModel = "Azure Managed HSM";
                hsmSerial = result.getHsmPool();
                if (!result.isValid()) {
                    errors.addAll(result.getErrors());
                }
            }
            case GOOGLE -> {
                if (request.getAttestationData() == null || request.getAttestationData().isBlank()) {
                    errors.add("attestationData is required for Google Cloud HSM verification");
                    break;
                }
                var result = googleVerifier.verifyGoogleAttestation(
                        request.getAttestationData(),
                        request.getAttestationCertChain(),
                        publicKey);
                publicKeyMatch = result.isPublicKeyMatch();
                attestationChainValid = result.isChainValid();
                generatedOnDevice = "generated".equals(result.getKeyOrigin());
                exportable = result.isExtractable();
                hsmModel = "Google Cloud HSM";
                hsmSerial = result.getKeyId();
                if (!result.isValid()) {
                    errors.addAll(result.getErrors());
                }
            }
        }

        // Determine compliance
        boolean compliant = errors.isEmpty() && publicKeyMatch && attestationChainValid
                && generatedOnDevice && !exportable;

        // Build DORA compliance mapping
        DoraCompliance doraCompliance = buildDoraCompliance(
                compliant, publicKeyMatch, attestationChainValid, generatedOnDevice, exportable);

        // Key properties
        KeyProperties keyProperties = KeyProperties.builder()
                .generatedOnDevice(generatedOnDevice)
                .exportable(exportable)
                .attestationChainValid(attestationChainValid)
                .publicKeyMatchesAttestation(publicKeyMatch)
                .build();

        // Warnings for edge cases
        if (exportable && attestationChainValid) {
            warnings.add("CRITICAL: Key is marked as exportable. Even though HSM attestation is valid, "
                    + "an exportable key provides no security guarantee as it may have been copied outside the HSM boundary.");
        }
        if (!generatedOnDevice && attestationChainValid) {
            warnings.add("Key was imported into HSM, not generated on-device. "
                    + "Key may have existed in software before import, compromising security guarantees.");
        }

        // Generate unique verification ID (Step 4)
        String verificationId = UUID.randomUUID().toString();

        // Register in approval registry (Step 4)
        approvalRegistry.register(
                verificationId, compliant, publicKeyFingerprint,
                request.getSupplierIdentifier(), request.getSupplierName(),
                compliant ? vendor.getVendorName() : null,
                compliant ? hsmModel : null,
                request.getCountryCode());

        // Build signed verification receipt (Step 5)
        EbaVerificationResponse receipt = EbaVerificationResponse.builder()
                .verificationId(verificationId)
                .compliant(compliant)
                .verificationTimestamp(timestamp)
                .publicKeyFingerprint(publicKeyFingerprint)
                .publicKeyAlgorithm(keyAlgorithm)
                .hsmVendor(compliant ? vendor.getVendorName() : null)
                .hsmModel(compliant ? hsmModel : null)
                .hsmSerialNumber(compliant ? hsmSerial : null)
                .keyProperties(keyProperties)
                .doraCompliance(doraCompliance)
                .supplierIdentifier(request.getSupplierIdentifier())
                .supplierName(request.getSupplierName())
                .keyPurpose(request.getKeyPurpose())
                .countryCode(request.getCountryCode())
                .errors(errors)
                .warnings(warnings)
                .build();

        // Sign the receipt with EBA's key (Step 5)
        // In production: qualified electronic seal under eIDAS (EU) No 910/2014
        // In reference implementation: self-signed demonstration signature
        receipt.setEbaSignature(signReceipt(receipt));
        receipt.setEbaSigningCertificate("--- EBA signing certificate (reference implementation) ---");

        return receipt;
    }

    /**
     * Batch verification for multiple entities.
     * Returns individual results plus aggregate statistics.
     * A compliance rate significantly below 100% indicates a systemic
     * supervisory failure — precisely the type of finding that triggers
     * EBA's obligations under Article 17 of Regulation 1093/2010.
     */
    public EbaBatchVerificationResponse verifyBatch(List<EbaVerificationRequest> requests) {
        List<EbaVerificationResponse> results = new ArrayList<>();
        int compliantCount = 0;
        int nonCompliantCount = 0;

        for (EbaVerificationRequest request : requests) {
            EbaVerificationResponse result = verify(request);
            results.add(result);
            if (result.isCompliant()) {
                compliantCount++;
            } else {
                nonCompliantCount++;
            }
        }

        return EbaBatchVerificationResponse.builder()
                .verificationTimestamp(Instant.now())
                .totalEntities(requests.size())
                .compliantCount(compliantCount)
                .nonCompliantCount(nonCompliantCount)
                .complianceRate(requests.isEmpty() ? 0.0
                        : (double) compliantCount / requests.size() * 100)
                .results(results)
                .build();
    }

    private DoraCompliance buildDoraCompliance(boolean compliant, boolean publicKeyMatch,
            boolean chainValid, boolean generatedOnDevice, boolean exportable) {

        // Article 5(2)(b): High standards for authenticity and integrity
        // Cannot be maintained without verified HSM protection
        boolean art5_2b = chainValid && publicKeyMatch && !exportable;

        // Article 6(10): Full responsibility for verification of compliance
        // "The verification" in definite form presupposes verification occurs
        boolean art6_10 = chainValid && publicKeyMatch && generatedOnDevice && !exportable;

        // Article 9(3)(c): PREVENT impairment of authenticity and integrity
        // Verb is "prevent" — requires active measure, not passive contractual term
        boolean art9_3c = chainValid && publicKeyMatch && !exportable;

        // Article 9(3)(d): Protection against poor administration,
        // processing-related risks and the human factor
        boolean art9_3d = chainValid && generatedOnDevice && !exportable;

        // Article 9(4)(d): Strong authentication mechanisms with dedicated control systems
        boolean art9_4d = chainValid && publicKeyMatch && generatedOnDevice && !exportable;

        // Article 28(1)(a): Full responsibility at all times regardless of
        // contractual arrangements
        boolean art28_1a = compliant;

        String summary;
        if (compliant) {
            summary = "Signing key is cryptographically proven to be generated and stored in a certified HSM "
                    + "with non-exportable attribute. All DORA requirements for cryptographic key management "
                    + "are independently verifiable.";
        } else {
            List<String> failures = new ArrayList<>();
            if (!chainValid)
                failures.add("attestation chain invalid");
            if (!publicKeyMatch)
                failures.add("public key does not match attestation");
            if (!generatedOnDevice)
                failures.add("key not generated on device");
            if (exportable)
                failures.add("key is exportable");

            summary = "Non-compliant: " + String.join(", ", failures) + ". "
                    + "The absence of valid attestation means the financial entity cannot demonstrate compliance "
                    + "with DORA Articles 5(2)(b), 6(10), 9(3)(c)-(d), 9(4)(d), or 28(1)(a). "
                    + "The entity must provide cryptographic attestation evidence or be considered non-compliant.";
        }

        return DoraCompliance.builder()
                .article5_2b(art5_2b)
                .article6_10(art6_10)
                .article9_3c(art9_3c)
                .article9_3d(art9_3d)
                .article9_4d(art9_4d)
                .article28_1a(art28_1a)
                .summary(summary)
                .build();
    }

    private PublicKey parsePublicKey(String pemInput) throws Exception {
        String pem = pemInput.trim();
        if (pem.startsWith("-----BEGIN PUBLIC KEY-----")) {
            String base64 = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(base64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            try {
                return KeyFactory.getInstance("RSA").generatePublic(spec);
            } catch (Exception e) {
                return KeyFactory.getInstance("EC").generatePublic(spec);
            }
        } else if (pem.startsWith("-----BEGIN CERTIFICATE REQUEST-----")) {
            try (PEMParser parser = new PEMParser(new StringReader(pem))) {
                var csr = (org.bouncycastle.pkcs.PKCS10CertificationRequest) parser.readObject();
                var pkInfo = csr.getSubjectPublicKeyInfo();
                var keySpec = new X509EncodedKeySpec(pkInfo.getEncoded());
                String algorithm = pkInfo.getAlgorithm().getAlgorithm().getId();
                String keyAlg = algorithm.startsWith("1.2.840.10045") ? "EC" : "RSA";
                return KeyFactory.getInstance(keyAlg).generatePublic(keySpec);
            }
        }
        throw new IllegalArgumentException(
                "Input must be PEM-encoded public key or CSR");
    }

    private String fingerprint(PublicKey key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(key.getEncoded());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x:", b & 0xff));
            }
            return sb.substring(0, sb.length() - 1);
        } catch (Exception e) {
            return "error";
        }
    }

    private EbaVerificationResponse buildNonCompliantResponse(List<String> errors,
            List<String> warnings, Instant timestamp, EbaVerificationRequest request) {

        String verificationId = UUID.randomUUID().toString();

        // Register non-compliant result in approval registry
        approvalRegistry.register(
                verificationId, false, null,
                request.getSupplierIdentifier(), request.getSupplierName(),
                null, null, request.getCountryCode());

        EbaVerificationResponse receipt = EbaVerificationResponse.builder()
                .verificationId(verificationId)
                .compliant(false)
                .verificationTimestamp(timestamp)
                .keyProperties(KeyProperties.builder()
                        .generatedOnDevice(false)
                        .exportable(true)
                        .attestationChainValid(false)
                        .publicKeyMatchesAttestation(false)
                        .build())
                .doraCompliance(DoraCompliance.builder()
                        .article5_2b(false)
                        .article6_10(false)
                        .article9_3c(false)
                        .article9_3d(false)
                        .article9_4d(false)
                        .article28_1a(false)
                        .summary("Verification could not be completed. " + String.join("; ", errors))
                        .build())
                .supplierIdentifier(request.getSupplierIdentifier())
                .supplierName(request.getSupplierName())
                .keyPurpose(request.getKeyPurpose())
                .countryCode(request.getCountryCode())
                .errors(errors)
                .warnings(warnings)
                .build();

        receipt.setEbaSignature(signReceipt(receipt));
        receipt.setEbaSigningCertificate("--- EBA signing certificate (reference implementation) ---");

        return receipt;
    }

    // =========================================================================
    // Step 7: Issuance Confirmation — close the verification loop
    // =========================================================================

    /**
     * Process an issuance confirmation from the certificate issuer (Step 7).
     * 
     * If the certificate was issued, extracts the public key from the
     * submitted signing certificate and verifies that it matches the
     * attestation evidence approved in Steps 2-5. The verification is
     * cryptographic: EBA does not rely on the issuer's assertion.
     * 
     * Anomalies are detected and flagged:
     * - Certificate issued despite NON-COMPLIANT attestation
     * - Public key in certificate does not match approved attestation
     * - Confirmation for unknown verification ID
     */
    public IssuanceConfirmationResponse confirmIssuance(IssuanceConfirmation confirmation) {
        List<String> anomalies = new ArrayList<>();
        Instant processedTimestamp = Instant.now();

        // Look up the original verification in the registry
        Optional<ApprovalRegistry.RegistryEntry> entryOpt =
                approvalRegistry.lookup(confirmation.getVerificationId());

        if (entryOpt.isEmpty()) {
            anomalies.add("ANOMALY: Confirmation received for unknown verification ID: "
                    + confirmation.getVerificationId());
            return IssuanceConfirmationResponse.builder()
                    .verificationId(confirmation.getVerificationId())
                    .loopClosed(false)
                    .registryStatus(IssuanceConfirmationResponse.RegistryStatus.ANOMALY_UNKNOWN_VERIFICATION)
                    .processedTimestamp(processedTimestamp.toString())
                    .anomalies(anomalies)
                    .build();
        }

        ApprovalRegistry.RegistryEntry entry = entryOpt.get();
        String expectedFingerprint = entry.getPublicKeyFingerprint();
        String actualFingerprint = null;
        boolean publicKeyMatch = false;

        if (confirmation.isIssued() && confirmation.getSigningCertificatePem() != null) {
            // Extract public key from the submitted certificate and compare
            try {
                PublicKey certPublicKey = extractPublicKeyFromCertificate(
                        confirmation.getSigningCertificatePem());
                actualFingerprint = fingerprint(certPublicKey);
                publicKeyMatch = actualFingerprint.equals(expectedFingerprint);

                if (!publicKeyMatch) {
                    anomalies.add("ANOMALY: Public key in issued certificate does not match "
                            + "the attestation evidence approved in verification "
                            + confirmation.getVerificationId());
                }
            } catch (Exception e) {
                anomalies.add("Failed to extract public key from submitted certificate: "
                        + e.getMessage());
            }
        }

        if (confirmation.isIssued() && !entry.isCompliant()) {
            anomalies.add("CRITICAL ANOMALY: Certificate issued despite NON-COMPLIANT "
                    + "attestation verification. This constitutes active circumvention "
                    + "of the supervisory mechanism.");
        }

        // Update the registry entry with confirmation result
        approvalRegistry.confirm(
                confirmation.getVerificationId(),
                confirmation.isIssued(),
                actualFingerprint,
                publicKeyMatch);

        // Determine final status
        IssuanceConfirmationResponse.RegistryStatus finalStatus;
        if (entry.isCompliant() && confirmation.isIssued() && publicKeyMatch) {
            finalStatus = IssuanceConfirmationResponse.RegistryStatus.VERIFIED_AND_ISSUED;
        } else if (entry.isCompliant() && confirmation.isIssued() && !publicKeyMatch) {
            finalStatus = IssuanceConfirmationResponse.RegistryStatus.ANOMALY_PUBLIC_KEY_MISMATCH;
        } else if (entry.isCompliant() && !confirmation.isIssued()) {
            finalStatus = IssuanceConfirmationResponse.RegistryStatus.VERIFIED_NOT_ISSUED;
        } else if (!entry.isCompliant() && confirmation.isIssued()) {
            finalStatus = IssuanceConfirmationResponse.RegistryStatus.ANOMALY_ISSUED_DESPITE_REJECTION;
        } else {
            finalStatus = IssuanceConfirmationResponse.RegistryStatus.REJECTED_NOT_ISSUED;
        }

        return IssuanceConfirmationResponse.builder()
                .verificationId(confirmation.getVerificationId())
                .loopClosed(anomalies.isEmpty())
                .publicKeyMatch(confirmation.isIssued() ? publicKeyMatch : null)
                .expectedPublicKeyFingerprint(expectedFingerprint)
                .actualPublicKeyFingerprint(actualFingerprint)
                .registryStatus(finalStatus)
                .processedTimestamp(processedTimestamp.toString())
                .anomalies(anomalies)
                .build();
    }

    // =========================================================================
    // Receipt signing (Step 5)
    // =========================================================================

    /**
     * Sign a verification receipt with EBA's key.
     * 
     * In production, this would use EBA's qualified electronic seal
     * under eIDAS Regulation (EU) No 910/2014. The reference
     * implementation uses a SHA-256 hash as a placeholder.
     */
    private String signReceipt(EbaVerificationResponse receipt) {
        try {
            String canonical = receipt.getVerificationId() + "|"
                    + receipt.isCompliant() + "|"
                    + receipt.getVerificationTimestamp() + "|"
                    + receipt.getPublicKeyFingerprint();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonical.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return "SIGNING_ERROR: " + e.getMessage();
        }
    }

    /**
     * Extract a public key from a PEM-encoded X.509 certificate.
     */
    private PublicKey extractPublicKeyFromCertificate(String certificatePem) throws Exception {
        try (PEMParser parser = new PEMParser(new StringReader(certificatePem))) {
            Object parsed = parser.readObject();
            if (parsed instanceof org.bouncycastle.cert.X509CertificateHolder holder) {
                byte[] encoded = holder.getSubjectPublicKeyInfo().getEncoded();
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
                // Try RSA first, fall back to EC
                try {
                    return KeyFactory.getInstance("RSA").generatePublic(keySpec);
                } catch (Exception e) {
                    return KeyFactory.getInstance("EC").generatePublic(keySpec);
                }
            }
            throw new IllegalArgumentException("Could not parse X.509 certificate from PEM");
        }
    }
}
