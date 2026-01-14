package com.easymeeting.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CopyTools {
    public static <T, S> List<T> copyList(List<S> sList, Class<T> classz) {
        List<T> list = new ArrayList<T>();
        if (sList == null) {
            return list;
        }
        for (S s : sList) {
            if (s == null) {
                continue;
            }
            T t = null;
            try {
                t = classz.newInstance();
            } catch (Exception e) {
            }
            copyPropertiesIgnoreNull(s, t);
            list.add(t);
        }
        return list;
    }

    public static <T, S> T copy(S s, Class<T> classz) {
        if (s == null) {
            return null;
        }
        T t = null;
        try {
            t = classz.newInstance();
        } catch (Exception e) {
        }
        copyPropertiesIgnoreNull(s, t);
        return t;
    }
    
    private static <S, T> void copyPropertiesIgnoreNull(S source, T target) {
        try {
            BeanUtils.copyProperties(source, target);
        } catch (FatalBeanException ex) {
            // 如果标准复制失败，尝试手动复制属性，忽略无法复制的属性
            PropertyDescriptor[] sourcePds = BeanUtils.getPropertyDescriptors(source.getClass());
            for (PropertyDescriptor sourcePd : sourcePds) {
                Method readMethod = sourcePd.getReadMethod();
                if (readMethod != null) {
                    try {
                        PropertyDescriptor targetPd = BeanUtils.getPropertyDescriptor(target.getClass(), sourcePd.getName());
                        if (targetPd != null) {
                            Method writeMethod = targetPd.getWriteMethod();
                            if (writeMethod != null) {
                                Object value = readMethod.invoke(source);
                                // 处理包装类型与基本类型的转换
                                if (value != null) {
                                    writeMethod.invoke(target, value);
                                } else if (isPrimitiveType(targetPd.getPropertyType())) {
                                    // 如果目标是基本类型且源值为null，则使用默认值
                                    writeMethod.invoke(target, getDefaultValue(targetPd.getPropertyType()));
                                } else {
                                    writeMethod.invoke(target, (Object) null);
                                }
                            }
                        }
                    } catch (Exception ignored) {
                        // 忽略无法复制的属性
                    }
                }
            }
        }
    }
    
    private static boolean isPrimitiveType(Class<?> clazz) {
        return clazz.isPrimitive() ||
               clazz == Long.class || clazz == Integer.class || clazz == Short.class || clazz == Byte.class ||
               clazz == Double.class || clazz == Float.class || clazz == Character.class || clazz == Boolean.class;
    }
    
    private static Object getDefaultValue(Class<?> clazz) {
        if (clazz == long.class || clazz == Long.class) return 0L;
        if (clazz == int.class || clazz == Integer.class) return 0;
        if (clazz == short.class || clazz == Short.class) return (short) 0;
        if (clazz == byte.class || clazz == Byte.class) return (byte) 0;
        if (clazz == double.class || clazz == Double.class) return 0.0;
        if (clazz == float.class || clazz == Float.class) return 0.0f;
        if (clazz == char.class || clazz == Character.class) return '\0';
        if (clazz == boolean.class || clazz == Boolean.class) return false;
        return null;
    }
    
    /**
     * 安全的属性复制方法，处理包装类型与基本类型之间的转换
     * @param source 源对象
     * @param target 目标对象
     */
    public static <S, T> void copyPropertiesSafe(S source, T target) {
        if (source == null || target == null) {
            return;
        }
        copyPropertiesIgnoreNull(source, target);
    }
}