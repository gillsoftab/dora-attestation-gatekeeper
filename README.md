# EBA Independent HSM Attestation Verification Tool

Independent verification tool enabling the European Banking Authority (EBA) to cryptographically verify whether financial entities comply with DORA requirements for HSM-based key protection — without requiring the entity's cooperation.

**Academic foundation:** Gillström, N., *Verifieringsansvar för kryptografiska nycklar i betalinfrastruktur: En rättsdogmatisk fallstudie av kontraktuell riskallokering och DORA-förordningens krav på IKT-riskhantering*, Uppsala University, Department of Law, Spring 2026. Supervised by a Docent (Associate Professor) and former Senior Adviser (*ämnesråd*) at the Financial Markets Division of the Swedish Ministry of Finance, whose assessment stated that the conclusions are *befogade* — a Swedish legal term denoting that the conclusions are justified on the merits of the legal analysis. The thesis demonstrates through systematic legal analysis that contractual HSM requirements without verification mechanisms do not satisfy DORA. The legal definition below follows the thesis's argument structure.

---

## Legal Definition

### 1. The Verification Gap in DORA's Contractual Framework

DORA and its delegated regulation (EU) 2024/1773, Article 3(6)(b), require that contractual arrangements for ICT services supporting critical functions include provisions on cryptographic controls. The deficiency is not the absence of contractual requirements — which DORA mandates — but the absence of verification that those requirements are met. This distinction is not specific to the case study below: any financial entity in the EU that includes contractual cryptographic security requirements, as DORA requires, but does not verify compliance faces the same structural deficiency.

**Case study: Swish Utbetalning.** Six Swedish major banks collectively own GetSwish AB, which provides infrastructure for digitally signed payment instructions. The banks' contractual terms require that signing keys are managed in Hardware Security Modules (HSM), whether handled by the corporate customer directly or by their technical supplier, satisfying the formal contractual requirement under DORA. No mechanism exists to verify that any party in the chain complies. However:

- **No verification has ever occurred.** Neither the banks nor GetSwish AB have verified HSM usage for any customer or technical supplier in the years the service has been operational. The system does not distinguish between signing and transport certificates at issuance — a customer can create multiple signing certificates and switch between them without restriction. Verification at each certificate issuance, which is what DORA requires, is not implemented and cannot be performed within the current system architecture. GetSwish AB has confirmed in writing that it does not use the open source reference implementation for HSM attestation verification — and since no alternative verification mechanism exists, no verification of any kind occurs.
- **The digital signature is immediately binding.** A signed payment order is irrevocable — there is no secondary verification, no manual approval process, and no possibility of intervention after signing.
- **The signing key is the sole security barrier.** A compromised key gives immediate, irrevocable access to the customer's funds up to the daily transaction limit.
- **At least one actor complies — but is not verified.** At least one technical supplier in the Swish ecosystem uses HSM in accordance with DORA's requirements and can provide independently verifiable cryptographic attestation proof. This attestation can be verified by EBA without the actor's cooperation. Yet neither the banks, GetSwish AB, nor the NCA (Finansinspektionen) have ever requested or verified this proof. This establishes that compliance is achievable, that the verification mechanism functions, and that the supervisory failure is complete — even compliant actors are not verified.


### 2. The Supervisory Method Gap

A principles-based supervisory method that examines whether contractual requirements exist — but not whether they are met — cannot detect non-compliance in cases where the only independently verifiable proof of compliance is cryptographic. DORA requires financial entities to ensure authenticity and integrity, not merely to contractually require it.

### 3. Regulatory Basis — Systematic Interpretation of DORA

The thesis applies EU legal methodology — examining wording, context, and objectives — to construct the applicable law. The following provisions form a coherent system that presupposes actual control mechanisms, not merely formal contractual requirements:

#### 3.1 Governance and Ultimate Responsibility

| DORA Provision | Requirement | Significance for Verification |
|---|---|---|
| **Article 5(2)(a)** | The management body shall bear the **ultimate responsibility** for managing the financial entity's ICT risk. | The management body bears ultimate responsibility for ICT risk management, which includes verifying that security requirements the bank itself has imposed — such as HSM usage — are actually met. This responsibility cannot be discharged by placing the requirement in a contract and relying on the customer's compliance without verification. |
| **Article 5(2)(b)** | The management body shall put in place policies that aim to ensure the maintenance of **high standards of availability, authenticity, integrity and confidentiality, of data**. | DORA requires that contractual arrangements include provisions on cryptographic controls — the banks' HSM requirement satisfies this obligation. However, maintaining "high standards" of authenticity and integrity requires both the contractual requirement and verification of its implementation. Without attestation verification, the infrastructure cannot distinguish between a signing key protected inside an HSM — which by design cannot be copied — and a software-generated key that may have been compromised. |
| **Article 5(3)** | Financial entities, other than microenterprises, shall establish a role in order to monitor the arrangements concluded with ICT third-party service providers on the use of ICT services, or shall designate a member of senior management as responsible for overseeing the related risk exposure and relevant documentation. | Monitoring presupposes that there is something to monitor — an actual control mechanism. A contractual requirement without follow-up does not satisfy this. Article 9(4)(a) confirms that this obligation extends to customers where applicable — and where the bank's own contractual terms require HSM usage, it is applicable. |

#### 3.2 The ICT Risk Management Framework — Verification Obligation

| DORA Provision | Requirement | Significance for Verification |
|---|---|---|
| **Article 6(1)** | Financial entities shall have a sound, comprehensive and well-documented ICT risk management framework as part of their overall risk management system, which enables them to address ICT risk quickly, efficiently and comprehensively and to ensure a high level of digital operational resilience. | The framework must be substantive, not formal. Recital 21 confirms this: "comprehensive capacity enabling a **robust and efficient** ICT risk management." |
| **Article 6(2)** | The ICT risk management framework shall include the strategies, policies, procedures, ICT protocols and tools that are necessary to duly and adequately protect all information assets and ICT assets [...] in order to ensure that all information assets and ICT assets are adequately protected from risks including damage and **unauthorised access or use**. | A compromised signing key enables precisely the unauthorised access or use this provision targets. |
| **Article 6(10)** | Financial entities may, in accordance with Union and national sectoral law, outsource the tasks of verifying compliance with ICT risk management requirements to intra-group or external undertakings. In case of such outsourcing, **the financial entity remains fully responsible for the verification of compliance with the ICT risk management requirements**. | The grammatical construction — "the verification" in definite form, not "any verification" — presupposes linguistically that verification takes place. The provision regulates **who** bears responsibility for verification that is presupposed to occur. This has been confirmed across three language versions: English ("the verification of compliance"), French ("la vérification du respect"), Swedish ("verifieringen av efterlevnad"). The convergence excludes that the definite form is a translation artefact. |

#### 3.3 Protection and Prevention — Strong Authentication Mechanisms

| DORA Provision | Requirement | Significance for Verification |
|---|---|---|
| **Article 9(2)** | Financial entities shall design, procure and implement ICT security policies, procedures, protocols and tools that aim to ensure the resilience, continuity and availability of ICT systems, in particular for those supporting critical or important functions, and to maintain high standards of availability, authenticity, integrity and confidentiality of data, whether at rest, in use or in transit. | Sets the standard against which Articles 9(3) and 9(4) must be assessed. |
| **Article 9(3)** | In order to achieve the objectives referred to in paragraph 2, financial entities shall use ICT solutions and processes that are appropriate **in accordance with Article 4**. | The proportionality assessment under Article 4 is incorporated directly into Article 9(3). This means that the choice of ICT solution must satisfy proportionality **before** being assessed against 9(3)(a)-(d). |
| **Article 9(3)(b)** | Minimise the risk of corruption or loss of data, unauthorised access and technical flaws that may hinder business activity. | A manual process does the opposite — it introduces the risks it should minimise. |
| **Article 9(3)(c)** | **Prevent** the lack of availability, the impairment of the authenticity and integrity, the breaches of confidentiality and the loss of data. | The verb is "prevent" — not "minimise," not "manage." An entity that chooses a manual process over an automated solution does not *prevent* the impairment of authenticity and integrity — it *permits* it by deliberately choosing a method with inherent weaknesses. |
| **Article 9(3)(d)** | Ensure that data is protected from risks arising from data management, **including poor administration, processing-related risks and human error**. | An entity that identifies a risk, formulates a countermeasure (HSM requirement), but fails to verify compliance exhibits poor administration. Choosing a manual process over an automated one introduces all three listed risk categories simultaneously. |
| **Article 9(4)(a)** | Develop and document an information security policy defining rules to protect the availability, authenticity, integrity and confidentiality of data, information assets and ICT assets, **including those of their customers, where applicable**. | The legislature explicitly anticipated situations where the bank's obligations extend to the customer's technical environment. This excludes the interpretation that the bank's obligations under Article 9 are limited to internal matters. |
| **Article 9(4)(d)** | Implement **policies and protocols for strong authentication mechanisms**, based on relevant standards and **dedicated control systems**, and protection measures of cryptographic keys whereby data is encrypted in line with results of approved data classification and ICT risk assessment processes. | Digital signing is an authentication mechanism. The requirement that it be "strong" means it must actually provide authenticity, not merely give the appearance of it. The requirement for "dedicated control systems" means there must be mechanisms to verify that the authenticity guarantee is maintained. The explicit reference to "protection measures of cryptographic keys" directly addresses the HSM verification question. |

#### 3.4 The Reversed Proportionality Argument

The proportionality principle (Article 4, incorporated via Article 9(3)) can be applied in reverse: since Article 9(3) expressly prescribes that the ICT solutions applied shall be appropriate in accordance with Article 4, a proportionality assessment means that an entity that chooses a manual process with inherent error risk over an available automated solution neither prevents the impairment of authenticity and integrity within the meaning of Article 9(3)(c), nor ensures the protection from poor administration, processing-related risks and human error required by Article 9(3)(d).

The proportionality assessment for HSM attestation verification:

- **Appropriateness:** HSM attestation directly verifies the security requirement — it is suited to achieve the goal.
- **Necessity:** No less intrusive alternative provides equivalent independently verifiable assurance (physical inspection is disproportionate, self-declaration lacks independence, third-party audit provides only point-in-time assurance, software attestation can be modified by the entity being verified and lacks the independently verifiable guarantees of a certified HSM).
- **Proportionality in the strict sense:** The service is critical, the risk at compromise is high (immediate irrevocable access to funds), and the verification cost is negligible (attestation is built into the HSMs the contractual terms already presuppose). Attestation capability is specified as a requirement in FIPS 140-2 Level 3, FIPS 140-3 Level 3 and Common Criteria EAL 4+, independently verifiable via NIST CMVP and certified evaluation reports.

An entity cannot claim that verification is disproportionate when the attestation functionality is built into the hardware the entity's own contractual terms require, the verification is a single check at certificate issuance, and the alternative (no verification) means the entity does not know whether its own security mechanism functions. Furthermore, without verification, the entity cannot fulfil its obligation under Article 19 to detect and report ICT-related incidents — a compromised signing key that was never HSM-protected is an incident the entity has no means of discovering. With HSM attestation verification at certificate issuance, a single check provides a cryptographic proof that remains valid for the lifetime of the certificate — the mathematical guarantee requires no ongoing monitoring.

#### 3.5 Full Responsibility in Contractual Arrangements

| DORA Provision | Requirement | Significance for Verification |
|---|---|---|
| **Article 28(1)(a)** | Financial entities that have in place contractual arrangements for the use of ICT services to run their business operations shall, **at all times**, remain **fully responsible** for compliance with, and the discharge of, all obligations under this Regulation and applicable financial services law. | The formulations are absolute. "At all times" and "fully responsible" leave limited semantic space for the qualifications that a permissive interpretation would require. A contractual risk allocation cannot relieve the bank of its regulatory obligations. |
| **Article 28(1)(b)** | Financial entities' management of ICT third-party risk shall be implemented in light of the **principle of proportionality**, taking into account the nature, scale, complexity and importance of ICT-related dependencies, and the risks arising from contractual arrangements, taking into account the **criticality or importance** of the respective service and **the potential impact on the continuity and availability** of financial services. | See Section 3.4 above — proportionality favours stricter control when the service is critical, risk is high, and verification cost is negligible. |
| **Article 28(5)** | Financial entities shall only enter into contractual arrangements with providers that comply with appropriate information security standards. | Presupposes that the financial entity **can assess** whether the provider complies — which in turn presupposes some form of control. |
| **Article 29(1)-(2)** | Financial entities shall take into account whether the envisaged contractual arrangement would lead to (a) contracting an ICT third-party service provider that is **not easily substitutable**, or (b) having in place **multiple contractual arrangements** with the same ICT third-party service provider. Financial entities shall assess whether arrangements may impact their ability to **fully monitor the contracted functions**. | Both criteria are met: GetSwish AB is the sole provider (not substitutable) and all six banks have contractual arrangements with the same provider. A bank without a mechanism to verify HSM management does not monitor the contracted function at all. |
| **Article 30(2)(c)** | Provisions on availability, authenticity, integrity and confidentiality in relation to the **protection of data**, including personal data. | A contractual requirement for HSM management without verification is not a provision on protection — it is a provision on what the customer shall do, without any mechanism that actually protects the signing key. |

#### 3.6 ICT Concentration Risk — EBA's Own Oversight Mandate

DORA establishes ICT concentration risk as a systemic concern with dedicated institutional mechanisms. The case study presents the precise scenario the legislature intended these mechanisms to address.

**Definition.** Article 3(29) defines ICT concentration risk as "an exposure to individual or multiple related critical ICT third-party service providers creating a degree of dependency on such providers so that the unavailability, failure or other type of shortfall of such provider may potentially endanger the ability of a financial entity to deliver critical or important functions, or cause it to suffer other types of adverse effects, including large losses, or endanger the financial stability of the Union as a whole." In the present case, six major Swedish banks depend on a single collectively owned provider (GetSwish AB) for payment signing infrastructure. The provider is not substitutable. The dependency is total.

**Obligation to assess.** Article 28(4)(c) requires financial entities, before entering into contractual arrangements, to "identify and assess all relevant risks in relation to the contractual arrangement, including the possibility that such contractual arrangement may contribute to reinforcing ICT concentration risk as referred to in Article 29." Article 29(1) specifies two criteria: (a) contracting a provider that is not easily substitutable, or (b) having multiple contractual arrangements with the same provider. Both criteria are met.

**Oversight Forum.** Article 32(2) requires the Oversight Forum to "undertake a collective assessment of the results and findings of the oversight activities conducted for all critical ICT third-party service providers and promote coordination measures to increase the digital operational resilience of financial entities, foster best practices on addressing ICT concentration risk and **explore mitigants** for cross-sector risk transfers." The open source reference implementation for HSM attestation verification constitutes precisely such a mitigant — it reduces concentration risk by providing an independently verifiable control mechanism that is not dependent on the concentrated provider's own systems or cooperation.

**Lead Overseer powers.** Article 35(1)(d)(ii) grants the Lead Overseer authority to examine "the use of conditions and terms, including their technical implementation, under which the critical ICT third-party service providers provide ICT services to financial entities, which the Lead Overseer deems relevant for preventing the generation of single points of failure, the amplification thereof, or for minimising the possible systemic impact across the Union's financial sector in the event of ICT concentration risk." A payment infrastructure where signing key integrity is neither verified nor verifiable constitutes a single point of failure. HSM attestation verification eliminates this single point of failure.

**Legislative intent.** The recitals confirm that the concentration risk framework was established because existing national mechanisms were insufficient. Recital 30 states that "the broader issue of counteracting systemic risk which may be triggered by the financial sector's exposure to a limited number of critical ICT third-party service providers is not sufficiently addressed by Union law." Recital 31 adds that intra-group provision of ICT services "should not be automatically considered less risky" — directly applicable to GetSwish AB's ownership structure. Recital 88 states that the Lead Overseer's powers should "enable the Lead Overseer to acquire real insight into the type, dimension and impact of the ICT third-party risk posed to financial entities and ultimately to the Union's financial system."

The concentration risk framework provides EBA with an additional, independent legal basis — beyond the Article 17 breach-of-Union-law procedure — to address the verification gap. The tools already exist in the regulation. The mitigant already exists as open source, supporting multiple HSM vendors, with independently auditable code. Any alternative implementation must meet the same functional standard — multi-vendor attestation verification, key origin and exportability checks, and DORA article compliance mapping — or fail the proportionality assessment under Article 9(3), since a less capable solution cannot be justified when a more complete one is freely available. Compliance of any alternative implementation is independently testable: cryptographic chain validation is mathematically deterministic, meaning the same attestation input must produce the same verification result — EBA can run both implementations against the same signing certificate attestation evidence and compare, leaving no room for interpretive divergence. The critical requirement for independent verification is that EBA or the national competent authority obtains the HSM manufacturers' root certificates directly from the manufacturers (Securosys, Yubico, Microsoft, Google) — not from any party in the chain being verified. This ensures that the verification is independent of all parties: the bank, GetSwish AB, the technical supplier, and the reference implementation provider alike.

#### 3.7 The Contractual Requirement as De Facto Liability Limitation

The contractual structure can be summarised in three steps: the bank imposes a security requirement, establishes that the signature is binding, but implements no mechanism to verify that the security requirement is met. The contractual term functions in practice as a **liability limitation** vis-à-vis the customer rather than the **security measure** the regulation requires. The absence of verification is not the result of a lack of bargaining position but an active choice not to use a control mechanism the banks, through their ownership of GetSwish AB, have full legal and factual ability to implement (cf. Recital 31: intra-group providers shall not automatically be considered less risky).

#### 3.8 Effet Utile

If formal contractual requirements without verification were accepted as sufficient, every DORA obligation could be fulfilled through documentation alone, which would reduce Articles 5, 6, 9 and 28-30 to requirements without independent normative content — in conflict with the principle that every provision shall be given effective application. DORA's purpose is preventive digital operational resilience — Article 9(3)(c) requires financial entities to *prevent* the impairment of authenticity and integrity, not to detect it after the fact. This preventive requirement can only be satisfied by verification at the point of certificate issuance: either valid HSM attestation evidence is presented and the signing certificate is issued, or it is not and the certificate is refused. Any verification that occurs after issuance — periodic audits, supervisory reviews, incident investigations — is by definition reactive, not preventive, and cannot satisfy this standard.

### 4. The Supervisory Authority's Responsibility

| Source | Obligation | Significance |
|---|---|---|
| **DORA Article 46** | Competent authorities shall monitor financial entities' compliance. | The national competent authority (NCA) — in the Swedish case, Finansinspektionen — has an obligation, not merely a power, to supervise compliance. |
| **DORA Article 50(2)** | Competent authorities shall have the power to require that measures be taken. | The NCA has the legal tools to order banks to implement verification mechanisms. That such tools exist and have not been used underscores the question of supervisory passivity. |
| **Article 4(3) TEU** | The loyalty principle: Member States shall take all appropriate measures to ensure fulfilment of obligations arising from Union law. | A supervisory authority that fails to act despite knowledge of systematic breaches of a directly applicable regulation raises the question of compatibility with the loyalty principle. |
| **Article 258 TFEU** | The Commission may bring infringement proceedings against a Member State. | A systematic failure to supervise constitutes a potential Treaty infringement. |

A supervisory authority's failure to intervene does not constitute a legal source. The regulation is directly applicable under Article 288 TFEU. A supervisory authority's inaction does not change the legal position — it only means that the breach has not been sanctioned. An authority's failure to express a view on a question it has not examined cannot be interpreted as a position on that question.

In a system with automated reporting at certificate issuance, the absence of reported data would itself constitute verifiable evidence of non-compliance, transforming supervision from reactive review to proactive anomaly detection.

#### 4.1 Verification Procedure — Triangulation From Independent Sources

To prevent selective reporting and ensure complete coverage, the verification procedure should use three independent data sources: the banks, GetSwish AB, and the HSM manufacturers' root certificates. No single party can manipulate the result without another party's data revealing the discrepancy.

**Phase 1 — Banks report first.** EBA or the NCA requests each owning bank to provide a complete list of all active customer agreements for Swish Utbetalning, including the associated Swish numbers and signing certificate identifiers. Since a representative from each owning bank sits on GetSwish AB's board, requesting the banks first prevents coordination of responses. During this phase, all banks are prohibited from onboarding new customers to Swish Utbetalning — ensuring the dataset is frozen at the point of inquiry. This freeze should be achievable within one business day; a period of up to one week is reasonable.

**Phase 2 — GetSwish AB reports.** Once all banks have submitted their data, EBA or the NCA requests GetSwish AB to provide all active Swish numbers with their associated certificates and any available attestation evidence. Critically, GetSwish AB's current system does not distinguish between signing and transport use — the certificates are identical duplicates, and any of them can be used for payment signing. This means that for every Swish number with a Swish Utbetalning agreement (identified via bank data in Phase 1), either all associated certificates must have valid HSM attestation — since any certificate may be used to authorise payments — or GetSwish AB must implement a separation between signing and transport certificates so that only HSM-attested certificates can be used for payment signing. In the absence of such separation, the only way to ensure that signing operations are HSM-protected is through a technical supplier that can distinguish certificate purposes via HSM attestation evidence — a capability that neither the banks nor GetSwish AB currently possess. Either path leads to the same requirement: HSM attestation verification must be implemented. In the Swish architecture, technical suppliers operate under a separate TL-number (987 prefix) linked to the customer's Swish number (123 prefix). A technical supplier has one transport certificate (for mTLS to the Swish API) and separate signing certificates for each customer's Swish number — each with its own private key, and often multiple signing certificates per Swish number for redundancy. Transport and signing certificates serve different purposes — transport certificates secure the mTLS channel to the Swish API, while signing certificates authorise payments. All signing certificate private keys must be generated and stored inside an HSM. The same HSM-protected signing key may be used across multiple customers, which further simplifies the verification: a single HSM attestation evidence for one private key can cover all signing certificates using that key. A technical supplier that uses HSM can produce valid attestation evidence for each individual signing certificate issued under its TL-number, proving that every signing key was generated and is stored inside a certified HSM. This makes the verification operationally scalable: one technical supplier's HSM infrastructure covers all of its customers' signing certificates, and the attestation evidence is produced per certificate at the point of issuance. This architectural deficiency — the inability to enforce different security levels for different certificate purposes — is itself a failure of ICT risk management. The cross-reference between bank data (Phase 1) and GetSwish AB data (Phase 2) must produce a matching set. Any discrepancy — a Swish number reported by a bank but absent from GetSwish AB's list, or vice versa — is immediately identifiable and requires explanation.

**Phase 3 — Independent attestation verification.** For each signing certificate reported, EBA or the NCA verifies the attestation evidence against the HSM manufacturers' root certificates obtained directly from the manufacturers. The result is binary for each certificate: valid HSM attestation exists, or it does not. This step requires no cooperation from any party in the chain — the mathematical proof is independently verifiable.

**Phase 4 — Remediation.** Signing certificates that lack valid HSM attestation evidence must be revoked. The affected entities must demonstrate that a verification mechanism — capable of validating HSM attestation at certificate issuance — is operational before new signing certificates may be issued. The freeze on new certificate issuance for non-compliant entities remains in effect until this capability is verified by the competent authority.

The procedure is designed so that no party can conceal non-compliance. The banks cannot underreport because GetSwish AB's operational data will reveal the gap. GetSwish AB cannot underreport because the banks' data will reveal the gap. Neither can falsify attestation evidence because the verification is performed against HSM manufacturers' root certificates held by the supervisory authority itself. The only way to conceal non-compliance would require the independent HSM manufacturer to participate — which is structurally excluded.

#### 4.2 Two Distinct Structural Problems

The Swish Utbetalning case presents two separate problems that must not be conflated, as they have different causes and different solutions.

**Problem 1 — Certificate type separation (architectural).** GetSwish AB's current system does not distinguish between transport certificates and signing certificates — they are identical duplicates, and any certificate can be used for payment signing. This is an architectural deficiency in GetSwish AB's infrastructure. It could be resolved by GetSwish AB introducing separate number ranges (e.g. 456-prefix) or other mechanisms to enforce the distinction between certificate types. This is an internal design decision for GetSwish AB and does not in itself raise DORA independence concerns. However, solving Problem 1 alone does not resolve Problem 2.

**Problem 2 — Independence of the signing key provider (regulatory).** Regardless of how certificate type separation is implemented, DORA's independence requirements determine who may provide the HSM-protected signing key and attestation evidence. This is the core DORA compliance problem.

Article 6(4) requires "appropriate segregation and independence of ICT risk management functions, control functions, and internal audit functions, according to the three lines of defence model." Article 5(4) requires members of the management body to actively maintain sufficient knowledge and skills, and the governance framework must ensure "effective and prudent management of ICT risk."

A bank that simultaneously acts as the contractual party imposing the HSM requirement on the customer, an owner of GetSwish AB which issues the certificates, and a technical supplier providing the HSM solution to the customer, occupies three roles that cannot be reconciled with the independence and segregation requirements in Articles 5 and 6. The entity setting the security requirement cannot simultaneously be the entity selling the solution to meet that requirement and the entity verifying compliance — this collapses all three lines of defence into one.

The consequence is that DORA's own independence requirements effectively prohibit the following parties from acting as technical suppliers for Swish Utbetalning signing services: the owning banks (direct conflict of interest across all three lines of defence), any indirect participant acting under a bank's mandate (the mandate creates the same structural dependency as direct participation — the entity acts as an extension of the bank and therefore inherits the same independence disqualification), and GetSwish AB itself (as the certificate issuer, it cannot simultaneously be the party providing the keys to be certified — the verifier and the verified cannot be the same entity). This prohibition follows directly from Articles 5 and 6 of DORA, not from competition law. It should be noted that both banks and GetSwish AB may have the capability to issue certificates, and could argue that decentralised certificate issuance reduces concentration risk under Article 29. However, concentration risk and structural independence are separate requirements that must both be satisfied simultaneously. Decentralising certificate issuance may address concentration risk but does not resolve the independence conflict: regardless of where the certificate is issued, the party providing the HSM-protected signing key and attestation evidence must be independent of the party setting the requirement and the party verifying compliance. The certificate issuer and the signing key provider serve different functions — and it is the signing key provider that must be an independent technical supplier.

A further variant of this argument must be addressed. An owning bank might claim to resolve the independence conflict by issuing signing certificates itself rather than through GetSwish AB, while continuing to use GetSwish AB's infrastructure to process the signed payment instructions. This does not resolve the conflict. The signing key's validity is ultimately relied upon by GetSwish AB's system to authorise payments — and the bank is a co-owner of that system. The bank would simultaneously impose the HSM requirement (contractual party), issue the certificate (certificate authority), and co-own the infrastructure that relies on the certificate's integrity (GetSwish AB shareholder). The independence conflict is not reduced by moving the certificate issuance step; it is reproduced. The same analysis applies to all six owning banks, since each bank's board representation in GetSwish AB creates the same structural dependency. A bank cannot claim independence from an infrastructure it co-owns and co-governs. Recital 63 explicitly provides that entities "collectively owned by financial entities" shall be considered third-party providers, and Recital 31 confirms that such arrangements "should not be automatically considered less risky than the provision of ICT services by providers outside of a financial group." GetSwish AB is not a subsidiary of any single bank — it is collectively owned by all six owning banks, which is precisely the ownership structure these recitals address. A potential counterargument arises from the second sentence of Recital 31, which states that "when ICT services are provided from within the same financial group, financial entities might have a higher level of control over intra-group providers, which ought to be taken into account in the overall risk assessment." However, this provision strengthens rather than weakens the case for a verification obligation. If the owning banks have a higher level of control over GetSwish AB — which they demonstrably do through board representation and ownership — they have correspondingly less justification for not having implemented HSM attestation verification. The banks could have mandated verification through an ownership directive at any time. The "higher level of control" has not been exercised to manage the risk; it has been left unused. Having the control but not using it is precisely the poor administration that Article 9(3)(d) requires protection against.

**The relationship between the two problems.** Problem 1 (certificate type separation) is an architectural prerequisite that GetSwish AB must resolve regardless. Problem 2 (independence of the signing key provider) is a DORA compliance requirement that persists regardless of how Problem 1 is solved. Even if GetSwish AB implements perfect certificate type separation, the signing keys must still be provided by an independent party with HSM attestation evidence, and verification must still pass through EBA's gatekeeper. Solving Problem 1 without solving Problem 2 achieves nothing — the signing certificates would be correctly categorised but still unverified.

All customers must therefore use an independent technical supplier operating under a separate TL-number (987 prefix) with its own HSM infrastructure and attestation capability, or register as a technical supplier themselves with their own TL-number and HSM infrastructure. For enterprise customers with sufficient technical capacity, becoming a TL is a viable path that preserves full control over their signing keys while satisfying the independence requirement — their attestation evidence is verified through EBA's gatekeeper identically to any other TL.

The TL model also provides a significant risk reduction advantage: requiring more than 22,000 corporate customers to individually maintain HSM competence — including correct configuration with non-exportable keys, firmware management, and the ability to produce valid attestation evidence — constitutes an unnecessary risk exposure under Article 9(3)(d) when specialised technical suppliers can handle it on their behalf. The automated verification at EBA's gatekeeper handles any volume of attestation checks equally; the issue is not the number of verifications but the number of entities that must independently maintain cryptographic competence.

The proportionality argument is further reinforced: since the verification mechanism must be implemented at the GetSwish AB infrastructure level regardless — because that is where certificates are issued — the marginal cost of verification for each additional customer or technical supplier approaches zero.

A further obligation follows from the combination of Articles 5(2)(b), 9(3)(d) and 9(4)(a). Banks that impose a contractual HSM requirement on customers and know — through the verification procedure described in Section 4.1 — which technical suppliers have valid HSM attestation, are obligated under DORA to make that information available to their customers. To impose a requirement without informing the customer how it can be met constitutes poor administration within the meaning of Article 9(3)(d). The bank's obligation under Article 9(4)(a) to protect data "including those of their customers, where applicable" extends to ensuring that customers have the information necessary to comply with the security requirements the bank itself has imposed. In practice, this means banks must provide customers seeking Swish Utbetalning with a list of technical suppliers whose HSM attestation has been verified. This is not a commercial recommendation — it is a factual record of compliance status, comparable to publishing which auditing firms hold a valid licence from the supervisory authority.

Should the initial list of verified technical suppliers contain only a single provider, this does not constitute ICT concentration risk within the meaning of Article 3(29). The reference implementation is published as open source, supporting multiple HSM vendors (Securosys, Yubico, Azure, Google Cloud), with no vendor lock-in. The barrier to becoming a compliant technical supplier is low: acquire a certified HSM, implement attestation verification, and register a TL-number. A market with a single initial provider due to first-mover advantage — where the technology is open, the standards are published, and the entry barrier is minimal — is structurally different from the concentration risk DORA addresses, which concerns dependency on providers that are not easily substitutable. Any technical supplier with a certified HSM can replicate the capability.

A separate consideration arises from the fact that representatives of all six owning banks sit on GetSwish AB's board. Any coordination between banks regarding which technical suppliers to use, avoid, or favour would constitute a potentially anti-competitive agreement under Article 101 TFEU and applicable national competition law. The obligation to publish a factual list of verified technical suppliers resolves this: banks share compliance status, not commercial preferences. Each customer selects independently from the list of compliant providers.

### 5. EBA's Supervisory Mechanisms — The Escalation Ladder

| Step | Mechanism | Character |
|---|---|---|
| **Article 16, Regulation 1093/2010** | EBA issues guidelines with "comply or explain." | Voluntary. Insufficient for a documented systemic deficiency. |
| **Article 17(1)-(3)** | EBA investigates alleged breach of Union law by an NCA and issues a recommendation. | Mandatory procedure when conditions are met. |
| **Article 17(4)** | If the NCA does not comply, EBA may adopt an individual decision addressed directly to the financial entity. | EBA can bypass the NCA entirely. |
| **Article 17(6)** | If the NCA does not comply with EBA's recommendation, EBA **shall** inform the Commission. | An obligation, not a power. "Shall" — not "may." |
| **Article 258 TFEU** | The Commission brings infringement proceedings. | Treaty infringement procedure. |

Where a documented systemic deficiency exists — confirmed in writing by the actor itself, with a reference implementation proving feasibility and quantifiable non-compliance — the question arises whether EBA has an obligation to use Article 17 rather than Article 16. Voluntary guidelines with a "comply or explain" mechanism cannot remedy a deficiency that EBA knows to be systemic. Deliberately choosing a tool one knows to be insufficient, when the obligations required by the regulation presuppose binding measures, itself raises the question of whether EBA fulfils its mandate under Regulation (EU) No 1093/2010.

In addition to the Article 17 procedure, EBA has a parallel and independent legal basis through the Oversight Framework established in DORA Chapter V, Section II. As set out in Section 3.6 above, Article 32(2) requires the Oversight Forum to annually assess concentration risk and "explore mitigants," while Article 35(1)(d)(ii) grants the Lead Overseer authority to examine the technical implementation of conditions under which critical ICT third-party service providers provide services — including conditions relevant to "preventing the generation of single points of failure." These two paths — the Article 17 breach-of-Union-law procedure directed at Finansinspektionen's supervisory failure, and the Oversight Framework directed at the systemic risk in the infrastructure itself — reinforce each other. A finding under either path strengthens the case under the other, and EBA cannot argue that one path renders the other unnecessary since they address different aspects of the same deficiency: Article 17 addresses the supervisory failure, while the Oversight Framework addresses the operational risk.

The practical consequence is that EBA must be prepared to act directly. As established in Section 6 below, Finansinspektionen's supervisory model — built on principles-based assessment — is structurally incapable of performing the material verification DORA requires. This is not a correctable error in Finansinspektionen's application of its methodology; it is a fundamental incompatibility between the methodology itself and the regulation's requirements. A recommendation to Finansinspektionen under Article 17(3) to "perform material verification" cannot be implemented within a supervisory framework that is not constructed for material verification. Article 17(4) — which empowers EBA to adopt individual decisions addressed directly to the financial entity — therefore becomes not merely available but necessary. EBA must effectively assume the supervisory function for this specific requirement, either directly or by mandating that GetSwish AB implement the verification mechanism at certificate issuance as a condition of its continued operation as a financial entity under DORA. Articles 17(4) and 17(6) operate simultaneously: EBA adopts an individual decision addressed directly to the financial entity requiring immediate remediation, while at the same time informing the Commission of the national competent authority's failure to act — triggering the Treaty infringement procedure under Article 258 TFEU in parallel with the direct operational remedy.

A further consequence follows from the preventive requirement established in Section 3.8. If DORA requires verification at the point of certificate issuance — because Article 9(3)(c) mandates prevention, not post-incident detection — then the supervisory authority receiving verification reports must be capable of receiving them in real time. EBA's current reporting infrastructure, built on file-based XBRL-CSV packages submitted periodically through national portals, is designed for reactive reporting and cannot fulfil this function. The obligation to perform material supervision under Article 17(4), combined with the preventive standard under Article 9(3)(c), requires EBA to establish or mandate reporting infrastructure capable of automated, real-time receipt of verification data at the moment of certificate issuance. This is not a technical upgrade — it is a precondition for fulfilling the supervisory function DORA assigns. A supervisory authority that lacks the infrastructure to receive preventive compliance data cannot perform preventive supervision, and cannot therefore fulfil the mandate the regulation presupposes.

### 6. Regulatory Theory: The Structural Paradox

Applying Julia Black's paradox analysis of principles-based regulation (*Capital Markets Law Journal*, vol. 3, no. 4, 2008), the verification gap documented in this case is not an anomaly but a structurally expected outcome:

- **The interpretation paradox:** The concept of "adequate" ICT risk management leaves interpretive space that enables minimalist compliance.
- **The compliance paradox:** In the absence of clear verification requirements, regulated actors fall back on formal compliance — formulating the requirement without controlling its implementation.
- **The trust paradox:** Principles-based regulation presupposes the institutional trust between supervisor and regulated entities that it simultaneously aims to create.

DORA presupposes a supervisory apparatus that verifies materially. Finansinspektionen's supervisory model is not constructed for that. If national competent authorities whose methodology rests on principles-based assessment are confronted with rule-based requirements that presuppose material verification, the question arises whether the supervisory model in its current form is compatible with the mandate DORA presupposes. If EBA does not act, the appearance of compliance becomes recursive through every institutional level: banks appear compliant because they have contractual HSM requirements, Finansinspektionen appears to supervise because it reviews documentation and frameworks, and EBA appears to oversee because it receives reports from national competent authorities — yet at no point in the chain does actual verification of the underlying cryptographic reality occur. Each institution's compliance is predicated on the assumption that another institution has verified — and none has. At this point, EBA's decision to act is no longer discretionary. Under the Treaties establishing the European Union, EBA was created to ensure the consistent application of Union law in the financial sector (Article 1(2), Regulation 1093/2010). Where the recursive failure of verification is documented, quantified, and independently provable, issuing voluntary guidelines under Article 16 — rather than performing material supervision through the binding mechanisms of Article 17(4) and 17(6) — is itself a failure to fulfil EBA's Treaty-based mandate, since guidelines addressed to a supervisory authority that is structurally incapable of material verification cannot produce the outcome the regulation requires.

---

## Technical Definition

### Requirement for Real-Time Reporting Infrastructure

The legal analysis in Sections 1-6 establishes that DORA requires preventive verification at the point of certificate issuance (Section 3.8). This has a direct technical consequence for supervisory reporting infrastructure. EBA's current reporting framework — file-based XBRL-CSV packages submitted through national portals on periodic schedules — was designed for reactive reporting: registers of information submitted quarterly, incident reports filed after events. This infrastructure is structurally incompatible with the preventive requirement in Article 9(3)(c), which requires that impairment of authenticity and integrity is *prevented*, not reported after the fact.

If DORA's material verification requirements are to be fulfilled, the supervisory authority must be capable of receiving automated reports at the moment of certificate issuance — not in a batch file weeks or months later. This requires a real-time API endpoint capable of receiving structured verification reports as they are generated. The reference implementation provides this capability as a REST API, which is the industry standard for real-time machine-to-machine communication. This is not a technical preference — it is a necessary consequence of the regulation's preventive requirements. A supervisory infrastructure that cannot receive real-time data cannot perform real-time supervision, and supervision that is not real-time cannot be preventive.

### Verification Principle

HSM attestation relies on a **hardware root of trust**. Every certified HSM device contains a unique attestation key, signed by the manufacturer's root certificate authority (CA). When a signing key is generated inside the HSM, the device produces an **attestation certificate** that cryptographically binds:

- The public key of the generated signing key
- The identity of the HSM device (serial number, model, firmware)
- Key attributes (generated on-device, non-exportable)

This chain can be verified against the HSM manufacturer's publicly available root CA certificate.

### Verification Flow — EBA as Gatekeeper

The architecture places EBA's verification *before* certificate issuance, making EBA's approval a precondition — not a post-issuance report.

```
                         CERTIFICATE ISSUANCE FLOW
                         
  ┌──────────────┐    ┌──────────────────┐    ┌──────────────────────────┐
  │  Technical   │    │  GetSwish AB     │    │  EBA / NCA               │
  │  Supplier    │    │  (or Bank)       │    │  Gatekeeper API          │
  │              │    │                  │    │  dora-api.eba.europa.eu  │
  └──────┬───────┘    └────────┬─────────┘    └────────────┬─────────────┘
         │                     │                           │
         │  1. CSR +           │                           │
         │  attestation ──────▶│                           │
         │  evidence           │                           │
         │                     │  2. Forward               │
         │                     │  attestation ────────────▶│
         │                     │  evidence                 │
         │                     │                           │  3. Verify chain
         │                     │                           │  against HSM mfr
         │                     │                           │  root CA
         │                     │                           │
         │                     │                           │  4. Register in
         │                     │                           │  approval
         │                     │                           │  registry
         │                     │                           │
         │                     │       5. Signed             │
         │                     │◀──── verification ────────│
         │                     │       receipt              │
         │                     │                           │
         │                     │                           │
         │  IF COMPLIANT:      │                           │
         │◀─── 6. Issue  ─────│                           │
         │  signing certificate│                           │
         │                     │  7. Confirm: send full    │
         │                     │  signing certificate ────▶│
         │                     │  (or non-issuance notice) │
         │  IF NON-COMPLIANT:  │                           │
         │◀─── 6. Refuse ─────│                           │
         │  certificate denied │  7. Confirm: send         │
         │                     │  non-issuance notice ────▶│
         │                     │                           │
```

**Step 1.** The technical supplier submits CSR and HSM attestation evidence to GetSwish AB (or the issuing bank). The attestation evidence includes the attestation certificate chain and, depending on the HSM vendor, additional vendor-specific data.

**Step 2.** GetSwish AB forwards the attestation evidence — together with the public key extracted from the CSR — to EBA's gatekeeper API for independent verification. This occurs *before* any certificate is issued.

**Step 3.** EBA verifies the attestation certificate chain against the HSM manufacturer's root CA, obtained directly from the manufacturer. The verification confirms that the signing key was generated inside a genuine HSM and is non-exportable. The result is binary: COMPLIANT or NON-COMPLIANT.

**Step 4.** EBA registers every verification result — both compliant and non-compliant — in its approval registry. This registry serves as the authoritative record of all attestation verifications and enables the secondary reconciliation control described below.

**Step 5.** EBA returns a signed verification receipt to GetSwish AB. The receipt is not a simple boolean but a traceable, cryptographically signed document containing: a unique verification ID, the verification timestamp, the public key fingerprint, HSM vendor and model, the DORA article compliance determination, and EBA's own digital signature over the receipt. The signature allows GetSwish AB — and any subsequent auditor — to independently verify that the approval originated from EBA. The receipt ID is the key that links the certificate to EBA's approval registry.

**Step 6.** GetSwish AB issues the signing certificate *only* if EBA's receipt confirms COMPLIANT status, and embeds the EBA verification ID in its own records alongside the issued certificate. If NON-COMPLIANT, the certificate request is refused and no signing certificate is created. The technical supplier must resolve the attestation deficiency before resubmitting.

**Step 7 — Closing the loop.** GetSwish AB sends a confirmation back to EBA. If the certificate was issued, the confirmation includes the full signing certificate, allowing EBA to independently extract the public key and verify that it matches the attestation evidence approved in Step 3. The circle is closed cryptographically, not contractually — EBA does not rely on GetSwish AB's assertion that the correct certificate was issued; it verifies the match itself. If the certificate was not issued (whether due to NON-COMPLIANT status or any other reason), GetSwish AB sends a non-issuance notice with timestamp and the EBA verification ID. This ensures that EBA's registry reflects the actual outcome for every verification request — an approved attestation without a corresponding issued certificate, or vice versa, is immediately visible.

**Secondary control — registry reconciliation.** EBA maintains a registry of all approved attestation verifications. This registry serves as a safeguard against any alternative certificate issuance path that might bypass the EBA verification step. Through the triangulation procedure described in Section 4.1, any certificate that exists in GetSwish AB's system but does not have a corresponding entry in EBA's registry is immediately identifiable as issued outside the approved process — a qualitatively more serious offence than mere non-compliance, as it constitutes active circumvention of the supervisory mechanism.

**Transitional architecture — EBA to NCA.** EBA's gatekeeper role under Article 17(4) is not permanent. It is activated to remedy a specific supervisory failure and remains in effect until the national competent authority demonstrates the capability to perform the same function. The API and verification infrastructure is therefore designed to be operated by either authority. In the initial phase, EBA operates the endpoint directly (`dora-api.eba.europa.eu`). Once the NCA has established equivalent real-time verification capability — the same API infrastructure, the same independent verification against HSM manufacturers' root certificates, the same registry — the gatekeeper function transitions to the NCA (e.g. `dora-api.fi.se` for Sweden, or the equivalent domain for any other Member State), and EBA's role reverts to supervisory convergence oversight under Article 29 of Regulation 1093/2010. The transition condition is not a policy decision but a technical verification: the NCA must demonstrate that its infrastructure produces the same deterministic verification results as EBA's, confirmed by running both implementations against identical attestation evidence.

### Attestation Verification Logic

```
┌────────────────────┐
│  Attestation Input │
│                    │
│  • Public key      │  ──→  Extract public key fingerprint
│  • Attestation     │  ──→  Verify attestation certificate chain
│    certificate     │       against HSM manufacturer root CA
│  • HSM vendor      │  ──→  Verify key attributes:
│                    │       - keyOrigin = "generated"
│                    │       - keyExportable = false
└────────────────────┘
           │
           ▼
┌────────────────────┐
│   Binary Result    │
│                    │
│  ✅ COMPLIANT:     │  Public key proven to be generated and
│     HSM-attested   │  stored in genuine HSM, non-exportable
│                    │
│  ❌ NON-COMPLIANT: │  Attestation chain invalid, key not
│     Not attested   │  generated on-device, or key exportable
└────────────────────┘
```

### Key Property: Independent Verifiability

The critical property is that verification is **independent of the entity being verified**:

1. The entity submits attestation evidence (or EBA requests it under its supervisory powers).
2. This tool verifies the evidence against the HSM manufacturer's root CA.
3. The HSM manufacturer is an independent third party (Securosys SA, Yubico AB, Microsoft, Google).
4. The result is deterministic — the same input always produces the same output.
5. No cooperation from the entity is required beyond providing the attestation data.

This is the practical manifestation of the distinction between contractual and cryptographic compliance: the mathematical proof validates or it does not.

### API Endpoint

#### POST dora-api.eba.europa.eu/v1/attestation/se/verify

Accepts attestation evidence and returns an independent verification result with DORA article compliance mapping.

**Request:**

```json
{
  "publicKey": "-----BEGIN PUBLIC KEY-----\nMIIB...",
  "hsmVendor": "SECUROSYS",
  "attestationData": "PD94bWwgdmVyc2lvbj0iMS4wI...",
  "attestationSignature": "eywPlJWUEiLDnaq+NEAs4zB3...",
  "attestationCertChain": [
    "-----BEGIN CERTIFICATE-----\n...",
    "-----BEGIN CERTIFICATE-----\n..."
  ],
  "supplierIdentifier": "5569741234",
  "supplierName": "Example Teknisk Leverantör AB",
  "keyPurpose": "Swish payment signing"
}
```

**Compliant response:**

```json
{
  "compliant": true,
  "verificationTimestamp": "2026-03-20T14:30:00Z",
  "publicKeyFingerprint": "c2:e7:bc:ce:c8:ae:e1:ed:...",
  "publicKeyAlgorithm": "RSA",
  "hsmVendor": "Securosys",
  "hsmModel": "Primus HSM",
  "hsmSerialNumber": "18000000",
  "keyProperties": {
    "generatedOnDevice": true,
    "exportable": false,
    "attestationChainValid": true,
    "publicKeyMatchesAttestation": true
  },
  "doraCompliance": {
    "article5_2b": true,
    "article6_10": true,
    "article9_3c": true,
    "article9_3d": true,
    "article9_4d": true,
    "article28_1a": true,
    "summary": "Signing key is cryptographically proven to be generated and stored in a certified HSM with non-exportable attribute. All DORA requirements for cryptographic key management are independently verifiable."
  },
  "errors": [],
  "warnings": []
}
```

**Non-compliant response:**

```json
{
  "compliant": false,
  "verificationTimestamp": "2026-03-20T14:30:00Z",
  "publicKeyFingerprint": "a1:b2:c3:...",
  "publicKeyAlgorithm": "RSA",
  "hsmVendor": null,
  "hsmModel": null,
  "hsmSerialNumber": null,
  "keyProperties": {
    "generatedOnDevice": false,
    "exportable": true,
    "attestationChainValid": false,
    "publicKeyMatchesAttestation": false
  },
  "doraCompliance": {
    "article5_2b": false,
    "article6_10": false,
    "article9_3c": false,
    "article9_3d": false,
    "article9_4d": false,
    "article28_1a": false,
    "summary": "No valid HSM attestation provided. Cannot verify that signing key is hardware-protected. The absence of attestation means the financial entity cannot demonstrate compliance with DORA Articles 5(2)(b), 6(10), 9(3)(c)-(d), 9(4)(d), or 28(1)(a). The entity must provide cryptographic attestation evidence or be considered non-compliant."
  },
  "errors": [
    "Attestation certificate chain verification failed against manufacturer root CA"
  ],
  "warnings": []
}
```

#### POST dora-api.eba.europa.eu/v1/attestation/se/verify/batch

Batch verification for multiple entities. Returns individual results plus aggregate compliance statistics. A compliance rate significantly below 100% indicates a systemic supervisory failure by the national competent authority — precisely the type of finding that triggers EBA's obligations under Article 17 of Regulation 1093/2010.

### Supported HSM Vendors

| Vendor | Attestation Method | Root CA Verification |
|---|---|---|
| Securosys Primus | XML attestation + signature + cert chain | Securosys root CA |
| Yubico YubiHSM 2 | Attestation certificate chain | Yubico root CA |
| Azure Managed HSM | `az keyvault key get-attestation` JSON | Microsoft MAA |
| Google Cloud HSM | Attestation bundle + cert chain | Google root CA |
| AWS CloudHSM | ❌ Lacks per-key attestation | Not supported |

---

## Prerequisites

```bash
brew install openjdk@21
sudo ln -sfn $(brew --prefix openjdk@21)/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home
java -version
```

```bash
mvn dependency:resolve
```

## Build and Run

```bash
mvn clean package
java -jar target/dora-attestation-gatekeeper-1.0.0.jar --spring.profiles.active=eba
```

**Swagger UI:** http://localhost:8080/swagger-ui.html

## Licence

MIT — Gillsoft AB

## Reference

Gillström, N., *Verifieringsansvar för kryptografiska nycklar i betalinfrastruktur*, Uppsala University, Spring 2026. Available at DiVA: [link to be added after publication]

Open source reference implementation: https://github.com/gillsoftab/hsm-attestation-reference
