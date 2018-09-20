package com.dazito.oauthexample.utils.converter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AutoConverter<S, T> implements Converter<S, T> {

    private final Logger logger = LoggerFactory.getLogger(AutoConverter.class);

    private static final int SOURCE_CLASS_POSITION = 0;
    private static final int TARGET_CLASS_POSITION = 1;

    @Override
    public final T convert(S source) {
        T target;
        try {
            target = instantiateTargetObject();
        } catch (IllegalAccessException | InstantiationException e) {
            logger.error("Could not instantiate target object in AutoConverter", e);
            return null;
        }

        // getting set all fields of the source and target
        Set<Field> sourceFields = getSourceClassFields();
        Set<Field> targetFields = getTargetClassFields();

        Map<String, Field> targetFieldsMap = new HashMap<>();

        // Checks target set fields on the duplicates
        for (Field f : targetFields) {
            if (targetFieldsMap.put(f.getName(), f) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        // copies fields from source to target object
        for (Field sourceField : sourceFields) {
            findFieldAndSetValue(source, target, targetFieldsMap, sourceField);
        }

        // additionally logic
        manualConvert(source, target);

        return target;
    }

    /**
     * Additional logic for config. This method should be override if the logic is wanted.
     *
     * @param source source object
     * @param target target object
     */
    public void manualConvert(S source, T target) {

    }

    private void findFieldAndSetValue(S source, T targetObject, Map<String, Field> targetFieldsMap, Field sourceField) {
        String currentFieldName = sourceField.getName();

        // if field is final, then return
        if (Modifier.isFinal(sourceField.getModifiers())) {
            return;
        }

        // getting name and type field from source object
        if (targetFieldsMap.containsKey(currentFieldName)) {
            Field targetField = targetFieldsMap.get(currentFieldName);
            Class<?> sourceFieldType = sourceField.getType();

            if (!sourceFieldType.equals(List.class) && sourceFieldType.equals(targetField.getType())) {
                copyFieldValue(source, targetObject, sourceField, targetField);
            }
        }
    }

    // copies fields from source to target object
    private void copyFieldValue(S source, T targetObject, Field sourceField, Field targetField) {
        Object value = null;
        boolean isSourceFieldAccessible = sourceField.isAccessible();
        boolean isTargetFieldAccessible = targetField.isAccessible();
        sourceField.setAccessible(true);
        targetField.setAccessible(true);
        try {
            value = sourceField.get(source);
        } catch (IllegalAccessException e) {
            logger.error(String.format("Can not get %s field value", sourceField.getName()), e);
        }
        if (value != null) {
            try {
                targetField.set(targetObject, value);
            } catch (IllegalAccessException e) {
                logger.error(String.format("Can not set %s field value", sourceField.getName()), e);
            }
        }
        sourceField.setAccessible(isSourceFieldAccessible);
        targetField.setAccessible(isTargetFieldAccessible);
    }

    // getting set all fields of the source
    private Set<Field> getSourceClassFields() {
        Class<S> sourceClass = getSourceType();
        return getFieldSetForClass(sourceClass);
    }

    // Returns fields of the target class
    private Set<Field> getTargetClassFields() {
        Class<T> targetClass = getTargetType();
        return getFieldSetForClass(targetClass);
    }

    private Set<Field> getFieldSetForClass(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();
        Class currentClass = clazz;
        while (!currentClass.equals(Object.class)) {
            Field[] foundFields = currentClass.getDeclaredFields();
            fields.addAll(Arrays.stream(foundFields).collect(Collectors.toList()));
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }

    // Creates new class, of the target
    private T instantiateTargetObject() throws IllegalAccessException, InstantiationException {
        Class<T> type = getTargetType();
        return type.newInstance();
    }

    // Returns arg for TARGET_CLASS_POSITION(type-target)
    @SuppressWarnings("unchecked")
    private Class<T> getTargetType() {
        return (Class<T>) getPositionalGenericArgument(TARGET_CLASS_POSITION);
    }

    // Returns arg for SOURCE_CLASS_POSITION(type-source)
    @SuppressWarnings("unchecked")
    private Class<S> getSourceType() {
        return (Class<S>) getPositionalGenericArgument(SOURCE_CLASS_POSITION);
    }

    // Returns arg of the superclass, by index.
    private Class getPositionalGenericArgument(int position) {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class) superClass.getActualTypeArguments()[position];
    }
}
