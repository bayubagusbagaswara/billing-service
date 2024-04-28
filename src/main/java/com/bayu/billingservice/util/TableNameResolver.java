package com.bayu.billingservice.util;

import jakarta.persistence.Table;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;

@UtilityClass
public class TableNameResolver {

    public static String getTableName(Class<?> entityClass) {
        Annotation[] annotations = entityClass.getAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation instanceof Table table) {
                return table.name();
            }
        }
        // default to entity class name if @Table annotation is not present
        return entityClass.getSimpleName();
    }
}
