package se.gillsoft.dora.gatekeeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DORA Attestation Gatekeeper.
 * 
 * EBA/NCA gatekeeper API for HSM attestation verification at certificate
 * issuance under DORA (EU 2022/2554). Implements the 7-step verification
 * flow described in README.md.
 * 
 * Run with: --spring.profiles.active=eba
 * Swagger UI: http://localhost:8080/swagger-ui.html
 * 
 * © Gillsoft AB — MIT Licence
 */
@SpringBootApplication
public class DoraAttestationGatekeeperApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoraAttestationGatekeeperApplication.class, args);
    }
}
