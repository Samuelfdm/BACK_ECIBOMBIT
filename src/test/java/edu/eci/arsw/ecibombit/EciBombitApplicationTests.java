package edu.eci.arsw.ecibombit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.cloud.azure.keyvault.secret.property-sources[0].enabled=false"})
class EciBombitApplicationTests {
	@Test
	void contextLoads() {
	}
}