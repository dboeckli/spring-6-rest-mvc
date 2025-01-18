package ch.springframeworkguru.springrestmvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@ActiveProfiles("test")
class SpringRestMvcApplicationTests {

    @Test
    void contextLoads() {
    }

}
