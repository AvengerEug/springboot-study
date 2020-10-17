package com.eugene.sumarry.springbootstudy.poflow.anno;

import com.eugene.sumarry.springbootstudy.poflow.spring.POFlowImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(POFlowImportSelector.class)
public @interface EnablePOFlow {
}
