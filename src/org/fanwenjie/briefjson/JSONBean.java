package org.fanwenjie.briefjson;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 *
 * @author Fan Wen Jie
 * @version 2015-03-05
 */
public class JSONBean {
    public static Object serialize(Object bean) throws NullPointerException {
        if (bean == null)
            return null;
        if (bean instanceof String || bean instanceof Boolean || bean instanceof Number)
            return bean;
        if (bean instanceof Collection)
            return serialize(((Collection) bean).toArray());
        if (bean.getClass().isArray()) {
            int length = Array.getLength(bean);
            ArrayList<Object> array = new ArrayList<Object>(length);
            for (int i = 0; i < length; ++i)
                array.add(serialize(Array.get(bean, i)));
            return array;
        }
        if (bean instanceof Map) {
            Map map = (Map) bean;
            for (Object key : map.keySet())
                map.put(key, serialize(map.get(key)));
            return map;
        }

        ArrayList<Integer> indexs = new ArrayList<Integer>();
        ArrayList<Object> values = new ArrayList<Object>();
        ArrayList<String> keys = new ArrayList<String>();
        for (Field field : bean.getClass().getDeclaredFields()) {
            boolean isRequired = false;
            Object value = null;
            try {
                field.setAccessible(true);
                JSONSeriable seriable = field.getAnnotation(JSONSeriable.class);
                if (seriable != null) {
                    String key = field.getName();
                    value = serialize(field.get(bean));
                    isRequired = seriable.isRequired();
                    if (!seriable.name().isEmpty())
                        key = seriable.name();
                    int order = seriable.order();
                    int positon = indexs.size();
                    if (order < Integer.MAX_VALUE)
                        for (int i = 0; i < indexs.size(); ++i)
                            if (order < indexs.get(i))
                                positon = i;
                    indexs.add(positon, order);
                    values.add(positon, value);
                    keys.add(positon, key);
                }
            } catch (Exception ignore) {
            } finally {
                if (isRequired && null == value)
                    throw new NullPointerException("Field " + field.getName() + " can't be null");
            }
        }
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(indexs.size());
        for (int i = 0; i < indexs.size(); ++i)
            map.put(keys.get(i), values.get(i));
        return map;
    }

    public static<T> Collection deserialize(Collection template,Class<T> genericType,Collection collection) throws Exception{
        return deserialize(template,genericType,collection.toArray());
    }

    public static<T,A> Collection deserialize(Collection template,Class<T> genericType,A[] array) throws Exception{
        Collection collection = template.getClass().newInstance();
        Object[] list = template.toArray();
        for(int i=0;i<array.length;++i)
            if(i<list.length)
                collection.add(deserialize(list[i],array[i]));
            else
                collection.add(deserialize(genericType,array[i]));
        return collection;
    }

    public static <T> T deserialize(T template,Object object) throws Exception {
        if (object instanceof Number || object instanceof String || object instanceof Boolean)
            return (T) object;
        if (object instanceof Collection)
            return deserialize(template, ((Collection) object).toArray());
        if (template instanceof Collection) {
            if (!object.getClass().isArray())
                return null;
            return (T) deserialize((Collection) template, Object.class, (Object[]) object);
        }
        if (template.getClass().isArray()) {
            if (!object.getClass().isArray())
                return null;
            Class componentType = template.getClass().getComponentType();
            int length = Array.getLength(object);
            Object array = Array.newInstance(template.getClass(), length);
            for (int i = 0; i < length; ++i)
                if (i < Array.getLength(template))
                    Array.set(array, i, deserialize(Array.get(template, i), Array.get(object, i)));
                else
                    Array.set(array,i,deserialize(componentType,Array.get(object,i)));
            return (T)array;
        }

        if (object instanceof Map) {
            Map map = (Map) object;
            for (Field field : template.getClass().getDeclaredFields()) {
                Object value = null;
                boolean isRequired = false;
                try {
                    field.setAccessible(true);
                    JSONSeriable seriable = field.getAnnotation(JSONSeriable.class);
                    if (seriable != null) {
                        String name = seriable.name();
                        if (name.isEmpty())
                            name = field.getName();
                        isRequired = seriable.isRequired();
                        value = map.get(name);
                        Object tmp = field.get(template);
                        Class clazz = field.getType();

                        if(Collection.class.isAssignableFrom(clazz)){
                            Class genericType = Object.class;
                            try {
                                genericType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                            } catch (Exception ignore) {
                            }
                            if(tmp==null||((Collection)tmp).size()==0) {
                                if (value instanceof Collection)
                                    value = deserialize(clazz, genericType, (Collection) value);
                                else if (value.getClass().isArray())
                                    value = deserialize(clazz, genericType, (Object[]) value);
                                else return null;
                            }else{
                                if (value instanceof Collection)
                                    value = deserialize((Collection)tmp, genericType, (Collection) value);
                                else if (value.getClass().isArray())
                                    value = deserialize((Collection)tmp, genericType, (Object[]) value);
                                else return null;
                            }
                        }else{
                            if(tmp==null||(tmp.getClass().isArray()&&Array.getLength(tmp)==0))
                                value = deserialize(clazz,value);
                            else
                                value = deserialize(tmp, value);
                        }
                        field.set(template, value);
                    }
                } catch (Exception ignore) {
                } finally {
                    if (isRequired && value == null)
                        throw new NullPointerException();
                }
            }
            return template;
        }
        return null;
    }

    public static <T> T deserialize(Class<T> klass,Map map) throws Exception {
        Object bean = klass.newInstance();
        for (Field field : klass.getDeclaredFields()) {
            Object value = null;
            boolean isRequired = false;
            try {
                field.setAccessible(true);
                JSONSeriable seriable = field.getAnnotation(JSONSeriable.class);
                if (seriable != null) {
                    String name = seriable.name();
                    if (name.isEmpty())
                        name = field.getName();
                    isRequired = seriable.isRequired();
                    value = map.get(name);

                    Class clazz = field.getType();
                    if (Collection.class.isAssignableFrom(clazz)) {
                        Class genericType = Object.class;
                        try {
                            genericType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        } catch (Exception ignore) {
                        }
                        if (value instanceof Collection)
                            value = deserialize(clazz, genericType, (Collection) value);
                        else if (value.getClass().isArray())
                            value = deserialize(clazz, genericType, (Object[]) value);
                        else return null;
                    } else
                        value = deserialize(clazz, value);
                    field.set(bean, value);
                }
            } catch (Exception ignore) {
            } finally {
                if (isRequired && value == null)
                    throw new NullPointerException();
            }
        }
        return (T) bean;
    }

    public static <T,A> Collection<T> deserialize(Class<? extends Collection> klass,Class<T> genericType,A[] array) throws Exception {
        Collection collection = klass.newInstance();
        for (int i = 0; i < array.length; ++i)
            collection.add(deserialize(genericType, array[i]));
        return collection;
    }

    public static <T> Collection<T> deserialize(Class<? extends Collection> klass,Class<T> genericType,Collection array) throws Exception {
        return deserialize(klass, genericType, array.toArray());
    }

    public static <T> T[] deserialize(Class<T> componentType,Collection array) throws Exception {
        return deserialize(componentType, array.toArray());
    }

    public static <T,A> T[] deserialize(Class<T> componentType,A[] array) throws Exception {
        T[] collection = (T[]) (Array.newInstance(componentType, array.length));
        for (int i = 0; i < array.length; ++i)
            collection[i] = deserialize(componentType, array[i]);
        return collection;
    }

    public static<T> T deserialize(Class<T> klass,Object object) throws Exception {
        if (object instanceof Number || object instanceof String || object instanceof Boolean)
            return (T) object;
        else if (object instanceof Map)
            return (T) deserialize(klass, (Map) object);
        else if (Collection.class.isAssignableFrom(klass)) {
            if (object instanceof Collection)
                return (T) deserialize((Class<? extends Collection>) klass, Object.class, (Collection) object);
            else if (object.getClass().isArray())
                return (T) deserialize((Class<? extends Collection>) klass, Object.class, (Object[]) object);
            else return null;
        } else if (klass.isArray()) {
            if (object instanceof Collection)
                return (T) deserialize(klass.getComponentType(), (Collection) object);
            else if (object.getClass().isArray())
                return (T) deserialize(klass.getComponentType(), (Object[]) object);
            else return null;
        } else
            return null;
    }
}
