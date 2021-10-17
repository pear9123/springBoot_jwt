package com.jwt;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtApplicationTests {

	@Test
	void contextLoads() {
		
		List<String> names = Arrays.asList("김갑순", "김갑돌");
        // x를 건네고 받는 과정에서 x를 두 번 적게 된다.
        names.forEach(x -> System.out.println(x));
        // 아예 x들을 빼버리고 아래와 같이 작성할 수 있다.
        names.forEach(System.out::println);
	}

}
