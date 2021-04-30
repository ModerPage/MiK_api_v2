package me.modernpage.aspect;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Component
@Aspect
public class LoggingAspect {
	
	@Before("allResource()") 
	public void userBeforeAdvice(JoinPoint joinPoint) {
		System.out.println(joinPoint.toShortString());
	}
	
	@AfterReturning(pointcut = "allResource()")
	public void userAfterAdvice(JoinPoint joinPoint) {
		System.out.println(joinPoint.toShortString());
	}
	
	/*
	 * @Around("allUserResource()") public Object
	 * userAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
	 * 
	 * try { return proceedingJoinPoint.proceed(); } catch (NoSuchElementException
	 * e) { return new ErrorMessage(404, "so such data found",
	 * "ask developer: my.genteel@list.ru"); } catch (Throwable e) { return new
	 * ErrorMessage(500, "Internal error occured",
	 * "ask developer: my.genteel@list.ru"); } }
	 */
	
	@Pointcut("within(me.modernpage.controller.*)")
	public void allResource() {}
}
