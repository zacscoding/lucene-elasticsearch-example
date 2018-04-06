package org.esdemo.util;

import org.esdemo.entity.AbstractEntity;
import org.esdemo.entity.NullTestEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class ReflectionUtil {

    public static <T> String getSpringDataId(T inst) {
        if (inst == null) {
            return null;
        }

        try {
            Field[] fields = inst.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    if (field.getType() == String.class) {
                        return (String) field.get(inst);
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    public static boolean modifyAnnotationValue(Class<?> target, Class<? extends Annotation> targetAnnotation, Annotation targetValue) {
        try {
            Method method = Class.class.getDeclaredMethod("annotationData");
            method.setAccessible(true);

            Object annotationData = method.invoke(target);

            Field annotations = annotationData.getClass().getDeclaredField("annotations");
            annotations.setAccessible(true);

            Map<Class<? extends Annotation>, Annotation> map = (Map<Class<? extends Annotation>, Annotation>) annotations.get(annotationData);
            map.put(targetAnnotation, targetValue);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean modifyDocumentAnnotation(Class<?> target, Document modified) {
        return modifyAnnotationValue(target, Document.class, modified);
    }

    public static boolean modifyDocumentAnnotation(Class<?> target, String newIndexName) {
        try {
            Document origin = AbstractEntity.class.getAnnotation(Document.class);
            Document modified = new Document() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return origin.annotationType();
                }

                @Override
                public String indexName() {
                    // modified
                    return newIndexName;
                }

                @Override
                public String type() {
                    return origin.type();
                }

                @Override
                public boolean useServerConfiguration() {
                    return origin.useServerConfiguration();
                }

                @Override
                public short shards() {
                    return origin.shards();
                }

                @Override
                public short replicas() {
                    return origin.replicas();
                }

                @Override
                public String refreshInterval() {
                    return origin.refreshInterval();
                }

                @Override
                public String indexStoreType() {
                    return origin.indexStoreType();
                }

                @Override
                public boolean createIndex() {
                    return origin.createIndex();
                }
            };
            return modifyAnnotationValue(target, Document.class, modified);
        } catch (Exception e) {
            return false;
        }
    }

}
