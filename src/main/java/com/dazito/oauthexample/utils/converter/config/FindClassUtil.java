package com.dazito.oauthexample.utils.converter.config;

import org.reflections.Reflections;

import java.util.Collection;

@SuppressWarnings("unchecked")
public class FindClassUtil {

    /**
     * Method for find all classes which implements the interface
     *
     * @param packageName       name of package where you need to find classes
     * @param requiredInterface interface which should implements the required class
     * @return list of classes which implements interface
     * @implNote Reflections library is used for find the classes
     */
    public static Collection<Class> findAllClassesImplementsInterface(String packageName, Class requiredInterface) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(requiredInterface);
    }
}
