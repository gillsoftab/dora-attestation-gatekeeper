package se.gillsoft.dora.gatekeeper.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import se.gillsoft.dora.gatekeeper.model.IssuanceConfirmationResponse.RegistryStatus;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Approval Registry — Step 4 of the verification flow.
 * 
 * Maintains an authoritative record of all attestation verifications
 * performed by the gatekeeper. Every verification — compliant and
 * non-compliant — is registered. This registry serves three functions:
 * 
 * 1. Links each verification to its outcome (issued/not issued)
 *    after the Step 7 confirmation loop.
 * 2. Enables secondary reconciliation: certificates that exist in
 *    GetSwish AB's system but lack a registry entry were issued
 *    outside the approved process.
 * 3. Provides the data basis for Article 17 investigations and
 *    Article 29 supervisory convergence assessments.
 * 
 * In production, this would be backed by a persistent, tamper-evident
 * data store (e.g. append-only database with cryptographic hash chain).
 * This reference implementation uses an in-memory ConcurrentHashMap.
 */
@Component
public class ApprovalRegistry {

    private final ConcurrentHashMap<String, RegistryEntry> entries = new ConcurrentHashMap<>();

    /**
     * Register a verification result (Step 4).
     * Called immediately after attestation verification completes.
     */
    public RegistryEntry register(String verificationId,
                                   boolean compliant,
                                   String publicKeyFingerprint,
                                   String supplierIdentifier,
                                   String supplierName,
                                   String hsmVendor,
                                   String hsmModel,
                                   String countryCode) {
        RegistryEntry entry = RegistryEntry.builder()
                .verificationId(verificationId)
                .compliant(compliant)
                .publicKeyFingerprint(publicKeyFingerprint)
                .supplierIdentifier(supplierIdentifier)
                .supplierName(supplierName)
                .hsmVendor(hsmVendor)
                .hsmModel(hsmModel)
                .countryCode(countryCode)
                .verificationTimestamp(Instant.now().toString())
                .status(compliant ? RegistryStatus.VERIFIED_AND_ISSUED : RegistryStatus.REJECTED_NOT_ISSUED)
                .certificateReceived(false)
                .build();

        // For compliant entries, status is provisional until Step 7 confirms issuance
        if (compliant) {
            entry.setStatus(null); // Awaiting confirmation
        }

        entries.put(verificationId, entry);
        return entry;
    }

    /**
     * Update a registry entry with the Step 7 confirmation result.
     */
    public Optional<RegistryEntry> confirm(String verificationId,
                                            boolean issued,
                                            String actualPublicKeyFingerprint,
                                            boolean publicKeyMatch) {
        RegistryEntry entry = entries.get(verificationId);
        if (entry == null) {
            return Optional.empty();
        }

        entry.setConfirmationTimestamp(Instant.now().toString());
        entry.setCertificateReceived(issued);

        if (entry.isCompliant() && issued && publicKeyMatch) {
            entry.setStatus(RegistryStatus.VERIFIED_AND_ISSUED);
            entry.setActualPublicKeyFingerprint(actualPublicKeyFingerprint);
        } else if (entry.isCompliant() && issued && !publicKeyMatch) {
            entry.setStatus(RegistryStatus.ANOMALY_PUBLIC_KEY_MISMATCH);
            entry.setActualPublicKeyFingerprint(actualPublicKeyFingerprint);
        } else if (entry.isCompliant() && !issued) {
            entry.setStatus(RegistryStatus.VERIFIED_NOT_ISSUED);
        } else if (!entry.isCompliant() && issued) {
            entry.setStatus(RegistryStatus.ANOMALY_ISSUED_DESPITE_REJECTION);
            entry.setActualPublicKeyFingerprint(actualPublicKeyFingerprint);
        } else {
            entry.setStatus(RegistryStatus.REJECTED_NOT_ISSUED);
        }

        return Optional.of(entry);
    }

    /**
     * Look up a registry entry by verification ID.
     */
    public Optional<RegistryEntry> lookup(String verificationId) {
        return Optional.ofNullable(entries.get(verificationId));
    }

    /**
     * Find all entries for a given country code (for Article 17 investigations).
     */
    public List<RegistryEntry> findByCountry(String countryCode) {
        return entries.values().stream()
                .filter(e -> countryCode.equals(e.getCountryCode()))
                .collect(Collectors.toList());
    }

    /**
     * Find all anomalies (for supervisory review).
     */
    public List<RegistryEntry> findAnomalies() {
        return entries.values().stream()
                .filter(e -> e.getStatus() != null &&
                        e.getStatus().name().startsWith("ANOMALY"))
                .collect(Collectors.toList());
    }

    /**
     * Find all entries awaiting Step 7 confirmation.
     */
    public List<RegistryEntry> findAwaitingConfirmation() {
        return entries.values().stream()
                .filter(e -> e.getStatus() == null)
                .collect(Collectors.toList());
    }

    /**
     * Compliance statistics for a given country code.
     */
    public ComplianceStats getStats(String countryCode) {
        List<RegistryEntry> countryEntries = findByCountry(countryCode);
        long total = countryEntries.size();
        long compliant = countryEntries.stream()
                .filter(e -> e.getStatus() == RegistryStatus.VERIFIED_AND_ISSUED)
                .count();
        long anomalies = countryEntries.stream()
                .filter(e -> e.getStatus() != null && e.getStatus().name().startsWith("ANOMALY"))
                .count();
        return new ComplianceStats(total, compliant, anomalies,
                total > 0 ? (double) compliant / total * 100.0 : 0.0);
    }

    public record ComplianceStats(long total, long compliant, long anomalies, double complianceRate) {}

    /**
     * A single entry in the approval registry.
     * 
     * The unique key is verificationId, NOT publicKeyFingerprint.
     * The same HSM-protected signing key (same fingerprint) may appear
     * in multiple registry entries — this is expected and correct:
     * - Pre-existing keys verified for the first time when the gatekeeper
     *   flow is introduced (the key existed before the registry did)
     * - Certificate renewal: same key, new certificate, new verification
     * - Re-verification after registry transition from EBA to NCA
     * 
     * Blocking duplicate fingerprints would prevent legitimate operations
     * and force unnecessary key regeneration with no security benefit —
     * the key never left the HSM, which is exactly what the attestation proves.
     */
    @Data
    @Builder
    public static class RegistryEntry {
        private String verificationId;
        private boolean compliant;
        private String publicKeyFingerprint;
        private String actualPublicKeyFingerprint;
        private String supplierIdentifier;
        private String supplierName;
        private String hsmVendor;
        private String hsmModel;
        private String countryCode;
        private String verificationTimestamp;
        private String confirmationTimestamp;
        private RegistryStatus status;
        private boolean certificateReceived;
    }
}
