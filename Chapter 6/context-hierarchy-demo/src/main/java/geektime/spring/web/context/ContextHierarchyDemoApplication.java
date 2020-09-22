package geektime.spring.web.context;

import geektime.spring.web.foo.FooConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
@Slf4j
public class ContextHierarchyDemoApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(ContextHierarchyDemoApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(FooConfig.class);

		ClassPathXmlApplicationContext xmlContext = new ClassPathXmlApplicationContext(
				new String[] {"applicationContext.xml"}, applicationContext); //这里定义了parent context

		//委托机制：在自己的 context 中找不到 bean，会委托父 context 查找该 bean
//		场景一：
//		父上下文开启 @EnableAspectJAutoProxy 的支持
//		子上下文未开启 <aop: aspectj-autoproxy />
//		切面 fooAspect 在 FooConfig.java 定义（父上下文增强）
/*
* <bean id="fooAspect" class="geektime.spring.web.foo.FooAspect" /> 可以不要
* 因为FooConfig.java 已经建立了fooAspect bean. 它可以在子childContext看到。
* 反之不行，只定义xml bean 不能在parentContext看到
* */
		TestBean bean = applicationContext.getBean("testBeanX", TestBean.class);
		bean.hello();

		log.info("=============");

		bean = xmlContext.getBean("testBeanX", TestBean.class);
		bean.hello();

		bean = xmlContext.getBean("testBeanY", TestBean.class);
		bean.hello();
	}
}
