package com.wannabe.app.main.annotation;

import com.wannabe.app.main.data.state.ArticleType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ArticleTypeValidator implements ConstraintValidator<ArticleTypeValid, ArticleType> {

    private ArticleTypeValid articleTypeValid;

    @Override
    public void initialize(ArticleTypeValid constraintAnnotation) {
        this.articleTypeValid = constraintAnnotation;
    }

    //equalsIgnoreCase -> 대소문자를 구분하지 않고 비교
    @Override
    public boolean isValid(ArticleType value, ConstraintValidatorContext context) {
        Object[] enumValues = this.articleTypeValid.enumClass().getEnumConstants();
        if (enumValues != null) {
            for (Object enumValue : enumValues) {
                //equals 메서드는 대소문자를 구분하며 비교하고
                //equalsIgnoreCase 메서드는 대소문자를 구분하지 않고 비교합니다.
                if (value.equals(enumValue.toString())
                    || (this.articleTypeValid.ignoreCase() && value.getArticleTypeValue().equalsIgnoreCase(enumValue.toString()))) {
                    return true;
                }
            }
        }
        return false;
    }
}