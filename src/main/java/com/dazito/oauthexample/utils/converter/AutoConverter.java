package com.dazito.oauthexample.utils.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        Set<Field> sourceFields = getSourceClassFields();
        Set<Field> targetFields = getTargetClassFields();
        Map<String, Field> targetFieldsMap = targetFields.stream().collect(Collectors.toMap(Field::getName, f -> f));
        for (Field sourceField : sourceFields) {
            findFieldAndSetValue(source, target, targetFieldsMap, sourceField);
        }
        manualConvert(source, target);
        return target;
    }

    private T instantiateTargetObject() throws IllegalAccessException, InstantiationException {
        Class<T> type = getTargetType();
        return type.newInstance();
    }

    @SuppressWarnings("unchecked")
    private Class<T> getTargetType() {
        return (Class<T>) getPositionalGenericArgument(TARGET_CLASS_POSITION);
    }

    /**
     * Additional logic for converter. This method should be override if the logic is wanted.
     *
     * @param source source object
     * @param target target object
     */
    public void manualConvert(S source, T target) {

    }

    private void findFieldAndSetValue(S source, T targetObject, Map<String, Field> targetFieldsMap, Field sourceField) {
        String currentFieldName = sourceField.getName();
        if (Modifier.isFinal(sourceField.getModifiers())) {
            return;
        }
        if (targetFieldsMap.containsKey(currentFieldName)) {
            Field targetField = targetFieldsMap.get(currentFieldName);
            Class<?> sourceFieldType = sourceField.getType();
            if (!sourceFieldType.equals(List.class) && sourceFieldType.equals(targetField.getType())) {
                copyFieldValue(source, targetObject, sourceField, targetField);
            }
        }
    }

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

    private Set<Field> getSourceClassFields() {
        Class<S> sourceClass = getSourceType();
        return getFieldSetForClass(sourceClass);
    }

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





    @SuppressWarnings("unchecked")
    private Class<S> getSourceType() {
        return (Class<S>) getPositionalGenericArgument(SOURCE_CLASS_POSITION);
    }

    private Class getPositionalGenericArgument(int position) {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class) superClass.getActualTypeArguments()[position];
    }
}
