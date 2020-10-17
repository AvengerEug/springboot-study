package com.eugene.sumarry.springbootstudy.poflow.spring;

import com.eugene.sumarry.springbootstudy.poflow.anno.EnablePOFlow;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class POFlowImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        if (importingClassMetadata.hasAnnotation(EnablePOFlow.class.getName())) {
            return new String[] {
                    POFlowConfigured.class.getName()
            };
        }

        return new String[0];
    }
}
