package com.concurrency_issue_practice.stock.aop;

import com.concurrency_issue_practice.stock.annotation.DistributionLock;
import com.concurrency_issue_practice.stock.utils.DistributionLockKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class DistributionLockAop {
    private static final String LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    
    @Around("@annotation(com.concurrency_issue_practice.stock.annotation.DistributionLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        DistributionLock distributionLock = method.getAnnotation(DistributionLock.class);

        String key = LOCK_PREFIX + DistributionLockKeyGenerator.generate(methodSignature.getName(),
                methodSignature.getParameterNames(), joinPoint.getArgs(), distributionLock.key());

        RLock lock = redissonClient.getLock(key);
        try {
            boolean available = lock.tryLock(distributionLock.waitTime(), distributionLock.leaseTime(),
                    distributionLock.timeUnit());

            if (!available) {
                System.out.println("Lock 획득 실패");
                throw new IllegalStateException("Lock 실패~");
            }

            return joinPoint.proceed();
        } catch (InterruptedException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            System.out.println("unlock - " + key);
            lock.unlock();
        }

    }
}
