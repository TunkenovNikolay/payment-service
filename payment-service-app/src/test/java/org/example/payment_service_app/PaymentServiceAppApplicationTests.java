package org.example.payment_service_app;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Интеграционный тест требует БД. Для unit-тестов используйте PaymentServiceTest")
class PaymentServiceAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
