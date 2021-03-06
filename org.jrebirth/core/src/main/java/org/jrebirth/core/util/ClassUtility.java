/**
 * Get more info at : www.jrebirth.org .
 * Copyright JRebirth.org © 2011-2013
 * Contact : sebastien.bordes@jrebirth.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jrebirth.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.application.Application;

import org.jrebirth.core.exception.CoreException;
import org.jrebirth.core.log.JRLogger;
import org.jrebirth.core.log.JRLoggerFactory;

/**
 * The class <strong>ClassUtility</strong>.
 * 
 * Some Useful class utilities to perform introspection.
 * 
 * @author Sébastien Bordes
 */
public final class ClassUtility implements UtilMessages {

    /** The separator used for serialization. */
    public static final String SEPARATOR = "|";

    /** The separator use in upper case to simulate a acamleCase change. */
    private static final String CASE_SEPARATOR = "_";

    /** The class logger. */
    private static final JRLogger LOGGER = JRLoggerFactory.getLogger(ClassUtility.class);

    /**
     * Private Constructor.
     */
    private ClassUtility() {
        // Nothing to do
    }

    // public static boolean isGenericType(final Class<?> mainClass, final int index, final Class<?> genericTypeClass) {
    //
    // // Retrieve the generic super class Parameterized type
    // final ParameterizedType paramType = (ParameterizedType) mainClass.getGenericInterfaces();
    //
    // // Retrieve the right generic type we want to instantiate
    // final Class<?> genericClass = (Class<?>) paramType.getActualTypeArguments()[index];
    //
    // return genericTypeClass.equals(genericClass);
    // }

    /**
     * Build the nth generic type of a class.
     * 
     * @param mainClass The main class used (that contain at least one generic type)
     * @param assignableClass the parent type of the generic to build
     * @param parameters used by the constructor of the generic type
     * 
     * @return a new instance of the generic type
     * 
     * @throws CoreException if the instantiation fails
     */
    public static Object buildGenericType(final Class<?> mainClass, final Class<?> assignableClass, final Object... parameters) throws CoreException {
        return buildGenericType(mainClass, new Class<?>[] { assignableClass }, parameters);
    }

    /**
     * Build the nth generic type of a class.
     * 
     * @param mainClass The main class used (that contain at least one generic type)
     * @param assignableClasses if the array contains only one class it define the type of the generic to build, otherwise it defines the types to skip to find the obejct to build
     * @param parameters used by the constructor of the generic type
     * 
     * @return a new instance of the generic type
     * 
     * @throws CoreException if the instantiation fails
     */
    public static Object buildGenericType(final Class<?> mainClass, final Class<?>[] assignableClasses, final Object... parameters) throws CoreException {
        Class<?> genericClass = null;
        // Copy parameters type to find the right constructor
        final Class<?>[] parameterTypes = new Class<?>[parameters.length];

        try {

            int i = 0;
            for (final Object obj : parameters) {
                parameterTypes[i] = obj.getClass();
                i++;
            }

            genericClass = findGenericClass(mainClass, assignableClasses);

            // Find the right constructor and use arguments to create a new
            // instance
            final Constructor<?> constructor = getConstructor(genericClass, parameterTypes);

            return constructor.newInstance(parameters);

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | SecurityException e) {

            final StringBuilder sb = new StringBuilder("[");
            for (final Class<?> assignableClass : assignableClasses) {
                sb.append(assignableClass.getName()).append(", ");
            }
            sb.append("]");

            if (genericClass == null) {
                LOGGER.log(GENERIC_TYPE_ERROR_1, e, sb.toString(), mainClass.getName());
            } else {
                LOGGER.log(GENERIC_TYPE_ERROR_2, e, genericClass.getName(), mainClass.getName());
            }

            if (e instanceof IllegalArgumentException) {
                LOGGER.log(ARGUMENT_LIST);
                for (int i = 0; i < parameterTypes.length; i++) {
                    LOGGER.log(ARGUMENT_DETAIL, parameterTypes[i].toString(), parameters[i].toString());
                }
            }
            if (genericClass == null) {
                throw new CoreException(GENERIC_TYPE_ERROR_1.get(sb.toString(), mainClass.getName()), e);
            } else {
                throw new CoreException(GENERIC_TYPE_ERROR_2.get(genericClass.getName(), mainClass.getName()), e);
            }
        }
    }

    /**
     * Retrieve the constructor of a Type.
     * 
     * @param genericClass the type of the object
     * @param parameterTypes an array of parameters' type
     * 
     * @return the right constructor that matchers parameters
     */
    private static Constructor<?> getConstructor(final Class<?> genericClass, final Class<?>[] parameterTypes) {
        Constructor<?> constructor = null;
        try {
            constructor = genericClass.getConstructor(parameterTypes);
        } catch (final NoSuchMethodException e) {
            // Return the first constructor as a workaround
            constructor = genericClass.getConstructors()[0];
        } catch (final SecurityException e) {
            LOGGER.log(NO_CONSTRUCTOR, e);
            throw e; // Pop up the exception to let it managed by the caller method
        }
        return constructor;
    }

    /**
     * Return the generic class for the given parent class and index.
     * 
     * @param mainClass the parent class
     * @param assignableClass the parent type of the generic to build
     * 
     * @return the class of the generic type according to the index provided or null if not found
     */
    public static Class<?> findGenericClass(final Class<?> mainClass, final Class<?> assignableClass) {
        return findGenericClass(mainClass, new Class<?>[] { assignableClass });
    }

    /**
     * Return the generic class for the given parent class and index.
     * 
     * @param mainClass the parent class
     * @param assignableClasses if the array contains only one class it define the type of the generic to build, otherwise it defines the types to skip to find the obejct to build
     * 
     * @return the class of the generic type according to the index provided or null if not found
     */
    public static Class<?> findGenericClass(final Class<?> mainClass, final Class<?>[] assignableClasses) {

        final boolean excludeMode = assignableClasses.length > 1;

        // Retrieve the generic super class Parameterized type
        final ParameterizedType paramType = (ParameterizedType) mainClass.getGenericSuperclass();

        Class<?> genericClass = null;
        Class<?> tempClass = null;
        for (int i = 0; genericClass == null && i < paramType.getActualTypeArguments().length; i++) {
            tempClass = getClassFromType(paramType.getActualTypeArguments()[i]);
            if (!excludeMode && assignableClasses[0].isAssignableFrom(tempClass)) {
                genericClass = tempClass;
            }
            if (excludeMode) {
                boolean excludeIt = false;
                for (final Class<?> excludeClass : assignableClasses) {
                    if (excludeClass.isAssignableFrom(tempClass)) {
                        excludeIt = true;
                    }
                }
                if (!excludeIt) {
                    genericClass = tempClass;
                }
            }
        }

        return genericClass;
    }

    /**
     * Convert A_STRING_UNDESCORED into aStringUnderscored.
     * 
     * @param undescoredString the string to convert
     * 
     * @return the string with camelCase
     */
    public static String underscoreToCamelCase(final String undescoredString) {

        // Split the string for each underscore
        final String[] parts = undescoredString.split(CASE_SEPARATOR);
        final StringBuilder camelCaseString = new StringBuilder(undescoredString.length());
        camelCaseString.append(parts[0].toLowerCase(Locale.getDefault()));

        // Skip the first part already added
        for (int i = 1; i < parts.length; i++) {
            // First letter to upper case
            camelCaseString.append(parts[i].substring(0, 1).toUpperCase(Locale.getDefault()));
            // Others to lower case
            camelCaseString.append(parts[i].substring(1).toLowerCase(Locale.getDefault()));
        }
        return camelCaseString.toString();

    }

    /**
     * Convert aStringUnderscored into A_STRING_UNDESCORED.
     * 
     * @param camelCaseString the string to convert
     * 
     * @return the underscored string
     */
    public static String camelCaseToUnderscore(final String camelCaseString) {

        final StringBuilder sb = new StringBuilder();
        for (final String camelPart : camelCaseString.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            if (sb.length() > 0) {
                sb.append(CASE_SEPARATOR);
            }
            sb.append(camelPart.toUpperCase(Locale.getDefault()));
        }
        return sb.toString();
    }

    /**
     * Return the method that exactly match the action name. The name must be unique into the class.
     * 
     * @param cls the class which contain the searched method
     * @param action the name of the method to find
     * @return the method
     * @throws NoSuchMethodException if no method was method
     */
    public static Method getMethodByName(final Class<?> cls, final String action) throws NoSuchMethodException {
        for (final Method m : cls.getMethods()) {
            if (m.getName().equals(action)) {
                return m;
            }
        }
        throw new NoSuchMethodException(action);
    }

    /**
     * List all properties for the given class.
     * 
     * @param cls the class to inspect by reflection
     * 
     * @return the field list
     */
    public static List<Field> retrievePropertyList(final Class<?> cls) {
        final List<Field> propertyList = new ArrayList<>();
        for (final Field f : cls.getFields()) {
            propertyList.add(f);
        }
        return propertyList;
    }

    /**
     * Check if the given method exists in the given class.
     * 
     * @param cls the class to search into
     * @param methodName the name of the method to check (camelCased or in upper case with underscore separator)
     * 
     * @return true if the method exists
     */
    public static List<Method> retrieveMethodList(final Class<?> cls, final String methodName) {
        final List<Method> methodList = new ArrayList<>();
        final String camelCasedMethodName = underscoreToCamelCase(methodName);
        for (final Method m : cls.getMethods()) {
            if (m.getName().equals(camelCasedMethodName) || m.getName().equals(methodName)) {
                methodList.add(m);
            }
        }
        return methodList;
    }

    /**
     * Return the Class object for the given type.
     * 
     * @param type the given type to cast into Class
     * 
     * @return the Class casted object
     */
    public static Class<?> getClassFromType(final Type type) {
        Class<?> returnClass = null;
        if (type instanceof Class<?>) {
            returnClass = (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            returnClass = getClassFromType(((ParameterizedType) type).getRawType());
        }
        return returnClass;
    }

    /**
     * Extract the first annotation requested found into the class hierarchy.<br />
     * Interfaces are not yet supported.
     * 
     * @param sourceClass the class (wit its parent classes) to inspect
     * @param annotationClass the annotation to find
     * 
     * @param <A> the type of the requested annotation
     * 
     * @return the request annotation or null if none have been found into the class hierarchy
     */
    public static <A extends Annotation> A extractAnnotation(final Class<?> sourceClass, final Class<A> annotationClass) {
        A annotation = null;
        Class<?> currentClass = sourceClass;
        while (annotation == null && currentClass != null) {
            annotation = currentClass.getAnnotation(annotationClass);
            currentClass = currentClass.getSuperclass();
        }
        return annotation;
    }

    /**
     * Retrieve an annotation property dynamically by reflection.
     * 
     * @param annotation the annotation to explore
     * @param attributeName the name of the method to call
     * 
     * @return the property value
     */
    public static Object getAnnotationAttribute(final Annotation annotation, final String attributeName) {
        Object object = null;
        try {
            // Get the annotation method for the given name
            final Method attributeMethod = annotation.annotationType().getDeclaredMethod(attributeName);

            // Call the method to gets the value
            object = attributeMethod.invoke(annotation);

        } catch (NoSuchMethodException | SecurityException e) {
            LOGGER.log(NO_ANNOTATION_PROPERTY, e, attributeName);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOGGER.log(NO_ANNOTATION_PROPERTY_VALUE, e, attributeName);
        }
        return object;
    }

    public static Class<? extends Application> getClassFromStaticMethod(final int classDeepLevel) {
        try {
            return (Class<? extends Application>) Class.forName(Thread.currentThread().getStackTrace()[classDeepLevel].getClassName());
        } catch (final ClassNotFoundException e) {
            return null;
        }
    }

}
