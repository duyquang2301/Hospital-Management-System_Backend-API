package com.wannabe.app.main.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ArticleTypeValidator.class)
@Documented
public @interface ArticleTypeValid {

    Class<? extends Enum<?>> enumClass();

    //message : 오류 발생 시 생성할 메세지 입니다.
    String message() default "Invalid ArticleType";

    // groups() : 상황별 validation 제어를 위해 사용됩니다.
    Class<?>[] groups() default {};

    // payloads() : 심각도를 나타냅니다.
    Class<? extends Payload>[] payload() default {};

    // ignoreCase() : 대소문자를 구별할 것인지 정하는 boolean 값입니다.
    boolean ignoreCase() default false;
}
