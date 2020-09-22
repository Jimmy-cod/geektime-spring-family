package geektime.spring.web.foo;

import geektime.spring.web.context.TestBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class FooConfig {
    @Bean
    public TestBean testBeanX() {
        return new TestBean("parentX");
    }

    @Bean
    public TestBean testBeanY() {
        return new TestBean("parentY");
    }

    @Bean
    public FooAspect fooAspect() {
        return new FooAspect();
    }
}
