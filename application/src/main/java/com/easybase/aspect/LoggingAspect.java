/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.aspect;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Pointcut(
		"within(@org.springframework.web.bind.annotation.RestController *)"
	)
	public void controller() {
	}

	@AfterThrowing(
		pointcut = "controller() || service()", throwing = "exception"
	)
	public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
		Signature signature = joinPoint.getSignature();

		String className = signature.getDeclaringTypeName();
		String methodName = signature.getName();

		Throwable cause;

		if (exception.getCause() != null) {
			cause = exception.getCause();
		}
		else {
			cause = new Throwable("NULL");
		}

		log.error(
			"Exception in {}.{} with cause = {}", className, methodName, cause);
	}

	@Around("controller()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();

		try {
			Signature signature = joinPoint.getSignature();

			String className = signature.getDeclaringTypeName();
			String methodName = signature.getName();

			Object result = joinPoint.proceed();

			long elapsedTime = System.currentTimeMillis() - start;

			log.info(
				"Controller executed: {}.{} in {} ms", className, methodName,
				elapsedTime);

			return result;
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Signature signature = joinPoint.getSignature();

			String className = signature.getDeclaringTypeName();
			String methodName = signature.getName();

			log.error(
				"Illegal argument: {} in {}.{}",
				Arrays.toString(joinPoint.getArgs()), className, methodName);

			throw illegalArgumentException;
		}
	}

	@Pointcut("within(@org.springframework.stereotype.Service *)")
	public void service() {
	}

}