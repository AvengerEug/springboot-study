package com.eugene.sumarry.autoconfigure.byannotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EugeneByAnnotationProperties.class)
public @interface EnableEugeneByAnnotationProperties {
}
