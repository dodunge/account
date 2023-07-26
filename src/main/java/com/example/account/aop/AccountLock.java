package com.example.account.aop;

import java.lang.annotation.*;

// 이 어노테이션으로 붙일수 있는 타겟의 종류(여기서는 메소드)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 상속가능한 구조로 사용하겠다
@Inherited
public @interface AccountLock {
    long tryLockTime() default 5000L;
}
