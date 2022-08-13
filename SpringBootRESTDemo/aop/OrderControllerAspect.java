package com.mercury.SpringBootRESTDemo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


//1. what is AOP
//2. AOP vs IoC vs OOP
//3. Pointcut
//4. Different aspect : before, after, after throwing, around
//5. ProceedJoinPoint
//6. different waive : compile vs runtime (Spring AOP use runtime)

@Aspect
@Component
public class OrderControllerAspect {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	// when				where: Pointcut
//	@Before("execution(* com.mercury.SpringBootRESTDemo.controllers.OrderController.*(..))")
//	public void logBeforeEachFunction() {
//		LOGGER.info("AOP log is printing.....");	// what need to be done
//	}
	
	@Pointcut("execution(* com.mercury.SpringBootRESTDemo.controllers.OrderController.*(..))")
	public void orderControllerPointCut() {
		
	}
	
	@Pointcut("execution(* com.mercury.SpringBootRESTDemo.controllers.ProductController.*(..))")
	public void productControllerPointCut() {
		
	}
	
	@After("orderControllerPointCut()")
	public void logIfCorrectExit() {
		LOGGER.info("AFTER LOG>>>>>>>>");
	}
	
	// if any throws
	@AfterThrowing("orderControllerPointCut()")
	public void emailIfException() {
		// send email
	}
	
	@Before("orderControllerPointCut()")
	public void logBeforeExecution() {
		LOGGER.info("AOP log is printing.....");	// what need to be done
	}
	
	// Around : Before + After
	@Around("productControllerPointCut()")
	public Object calcuateTime(ProceedingJoinPoint pjp) {
		Long startTime = System.currentTimeMillis();
		
		Object o = null;
		try {
			o = pjp.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		Long endTime = System.currentTimeMillis();
		LOGGER.info("execution time: " + (endTime - startTime));
		
		return o;
	}
}
