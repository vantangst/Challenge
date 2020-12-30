package android.support.core.utils;

import androidx.annotation.VisibleForTesting;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClassUtils {

    private static final int MAX_DEEP = 8;
    private static final ClassCache sClassCache = new ClassCache();

    /**
     * Get parameter type of object at position
     *
     * @param obj Any object
     * @return List types of parameter on object
     */
    public static <T> Class<T> getFirstGenericParameter(Object obj) {
        Class clazz = obj.getClass();
        if (!sClassCache.hasGenericType(clazz, 0))
            throw new RuntimeException(clazz.getSimpleName() + " has not first position");
        return sClassCache.getGenericType(clazz, 0);
    }

    /**
     * Get parameter type of object at position
     *
     * @param obj Any object
     * @return List types of parameter on object
     */
    public static <T> Class<T> getGenericParameter(Object obj, int index) {
        Class clazz = obj.getClass();
        if (!sClassCache.hasGenericType(clazz, index))
            throw new RuntimeException(clazz.getSimpleName() + " has not first position");
        return sClassCache.getGenericType(clazz, index);
    }

    /**
     * Get annotation of object
     *
     * @param obj             Object has annotation
     * @param annotationClass annotation to get instance
     * @return Annotation of object
     */
    public static <A extends Annotation> A getAnnotation(Object obj, Class<A> annotationClass) {
        Class clazz = obj.getClass();
        if (sClassCache.hasAnnotation(clazz, annotationClass))
            return sClassCache.getAnnotation(clazz, annotationClass);
        return null;
    }

    @VisibleForTesting
    public static class ClassCache {
        private final Map<String, Class> mGenericTypeCache = new HashMap<>();
        private final Map<String, HasAnnotationMap> mHasAnnotation = new HashMap<>();
        private final Map<String, AnnotationValueMap> mAnnotationCache = new HashMap<>();

        boolean hasGenericType(Class clazz, int index) {
            String genType = formatGenericType(clazz, index);
            if (mGenericTypeCache.containsKey(genType)) {
                return mGenericTypeCache.get(genType) != null;
            }
            Type[] types = findParameterTypes(clazz, 0);
            if (types == null || types.length == 0) {
                mGenericTypeCache.put(genType, null);
                return false;
            }
            Type type = types[index];
            if (!(type instanceof Class))
                throw new RuntimeException("First parameter type need to be class");
            mGenericTypeCache.put(genType, (Class) type);
            return true;
        }

        private String formatGenericType(Class clazz, int index) {
            return String.format(Locale.getDefault(), "%s[%d]", clazz.getName(), index);
        }

        Class getGenericType(Class clazz, int index) {
            return mGenericTypeCache.get(formatGenericType(clazz, index));
        }

        private Type[] findParameterTypes(Class clazz, int deep) {
            Type superType = clazz.getGenericSuperclass();
            if (superType instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) superType).getActualTypeArguments();
                if (types.length != 0) return types;
            }
            if (deep > MAX_DEEP) return null;
            Class superClass = clazz.getSuperclass();
            if (superClass == null) return null;
            return findParameterTypes(superClass, deep + 1);
        }

        private <A extends Annotation> A findAnnotation(Class clazz, Class<A> annotationClass, int deep) {
            Annotation annotation = clazz.getAnnotation(annotationClass);
            if (annotation != null) return (A) annotation;
            if (deep > MAX_DEEP) return null;
            clazz = clazz.getSuperclass();
            if (clazz == null) return null;
            return findAnnotation(clazz, annotationClass, deep + 1);
        }

        <A extends Annotation> boolean hasAnnotation(Class clazz, Class<A> annotationClass) {
            HasAnnotationMap hasAnnotationMap;
            AnnotationValueMap annotationValueMap;
            if (!mHasAnnotation.containsKey(clazz.getName())) {
                hasAnnotationMap = new HasAnnotationMap();
                annotationValueMap = new AnnotationValueMap();
                mHasAnnotation.put(clazz.getName(), hasAnnotationMap);
                mAnnotationCache.put(clazz.getName(), annotationValueMap);
            } else {
                hasAnnotationMap = mHasAnnotation.get(clazz.getName());
                annotationValueMap = mAnnotationCache.get(clazz.getName());
            }
            assert hasAnnotationMap != null;
            assert annotationValueMap != null;
            if (!hasAnnotationMap.containsKey(annotationClass)) {
                Annotation annotation = findAnnotation(clazz, annotationClass, 0);
                hasAnnotationMap.put(annotationClass, annotation != null);
                annotationValueMap.put(annotationClass, annotation);
                return annotation != null;
            }
            return hasAnnotationMap.get(annotationClass);
        }

        <A extends Annotation> A getAnnotation(Class clazz, Class<A> annotationClass) {
            return (A) mAnnotationCache.get(clazz.getName()).get(annotationClass);
        }
    }

    @VisibleForTesting
    public static class HasAnnotationMap extends HashMap<Class<? extends Annotation>, Boolean> {
    }

    @VisibleForTesting
    public static class AnnotationValueMap extends HashMap<Class<? extends Annotation>, Annotation> {
    }
}


