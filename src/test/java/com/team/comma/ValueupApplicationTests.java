package com.team.comma;

import static org.assertj.core.api.Assertions.assertThatIOException;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ValueupApplicationTests {

	@Test
	void contextLoads() {
		assertThatIOException().isThrownBy(() -> { throw new IOException("boom!"); })
        .withMessage("%s!", "boom")
        .withMessageContaining("boom")
        .withNoCause();
	}

}
