package com.easy.base.annotation;

import com.easy.base.model.enums.FieldTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidatesFieldType {
    FieldTypes value();
}