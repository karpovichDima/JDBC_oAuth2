package com.dazito.oauthexample.utils.converter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class ConversionServiceBean {

    private static final String STRING_ARRAY_SEPARATOR = ",";
    private final Logger logger = LoggerFactory.getLogger(ConversionServiceBean.class);

    @Bean
    public ConversionServiceFactoryBean conversionService(@Value("${dazito.converter-locations}") String locations) {

        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        Set<Converter> converters = findAndInstantiateConverters(locations);
        converters.add(new SerializingConverter());
        converters.add(new DeserializingConverter());
        bean.setConverters(converters);
        return bean;
    }

    private Set<Converter> findAndInstantiateConverters(String locations) {
        logger.info("Trying to find converters");
        Collection<Class> converterClasses = findConverterClasses(locations);
        logger.info(String.format("Found %s classes of converters.", converterClasses.size()));
        return converterClasses.stream().map(clazz ->
        {
            try {
                return (Converter) clazz.newInstance();
            } catch (InstantiationException e) {
                logger.error("Could not instantiate config " + clazz.getName(), e);
            } catch (IllegalAccessException e) {
                logger.error("Could not get access to config " + clazz.getName(), e);
            }
            return null;
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    // find converters
    private Collection<Class> findConverterClasses(String locations) {
        String[] converterLocations = parseConverterLocations(locations);
        List<Class> result = new ArrayList<>();
        Arrays.stream(converterLocations)
                .parallel()
                .map(location -> FindClassUtil.findAllClassesImplementsInterface(location, Converter.class))
                .forEach(result::addAll);
        result = result.stream().filter(clazz -> !Modifier.isAbstract(clazz.getModifiers())).collect(Collectors.toList());
        return result;
    }

    // split address locations
    private String[] parseConverterLocations(String converterLocations) {
        if (Objects.isNull(converterLocations)) {
            logger.error("Could not find property tms.config-locations. Please, check the configuration");
            return new String[]{};
        }
        return converterLocations.split(STRING_ARRAY_SEPARATOR);
    }
}
