package com.jpr.myapplication;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 类描述:
 * 创建日期:2018/2/9 on 14:54
 * 作者:JiaoPeiRong
 */

public class FastJson {
    public static String toJson(Object object) {
        //Json载体
        StringBuffer jsonBuffer = new StringBuffer();
        //判断是否是集合
        if (object instanceof List<?>) {
            //开始的"{"
            jsonBuffer.append("{");
            List<?> list = (List<?>) object;
            //循环取集合类型
            for (int i = 0; i < jsonBuffer.length(); i++) {
                addObjectToJson(jsonBuffer, list.get(i));
                //jsonArray添加 逗号分隔
                if (i < list.size() - 1) {
                    jsonBuffer.append(",");
                }

            }
        } else {
            addObjectToJson(jsonBuffer, object);
        }

        return jsonBuffer.toString();
    }

    /**
     * 解析单独的JsonObject类型
     * 递归
     *
     * @param jsonBuffer
     * @param o
     */
    private static void addObjectToJson(StringBuffer jsonBuffer, Object o) {
        jsonBuffer.append("{");
        List<Field> fields = new ArrayList<>();
        getAllFields(o.getClass(), fields);
        for (int i = 0; i < fields.size(); i++) {
            //代表getMethod方法
            Method method = null;
            Field field = fields.get(i);
            //代表成员变量的值
            Object fieldValue = null;
            //成员变量名称
            String fieldName = field.getName();
            //方法名,(char)(fieldName.charAt(0)-32)首字母大写,a 97  A 65
            String methodName = "get" + ((char) (fieldName.charAt(0) - 32) + fieldName.substring(1));
            try {
                //拿到方法
                method = o.getClass().getMethod(methodName);
            } catch (NoSuchMethodException e) {
                if (!fieldName.startsWith("is")) {
                    methodName = "is" + ((char) (fieldName.charAt(0) - 32) + fieldName.substring(1));
                }
                try {
                    method = o.getClass().getMethod(methodName);
                } catch (NoSuchMethodException e1) {
                    replaceChar(i, fields, jsonBuffer);
                    continue;
                }
            }

            //拿到了成员变量对应的方法
            if (method != null) {
                try {
                    //可能是基本类型,可能是对象类型
                    fieldValue = method.invoke(o);
                } catch (Exception e) {
                    replaceChar(i , fields,jsonBuffer);
                    continue;
                }
            }

            /**
             * {
             *     "name":"张三",
             *     "age":12,
             *
             * }
             */
            if (fieldValue != null) {
                jsonBuffer.append("\"");
                jsonBuffer.append(fieldName);
                jsonBuffer.append("\":");
                if (fieldValue instanceof Integer ||
                        fieldValue instanceof Double ||
                        fieldValue instanceof Long ||
                        fieldValue instanceof Boolean) {
                    jsonBuffer.append(fieldValue.toString());
                } else if (fieldValue instanceof String) {
                    jsonBuffer.append("\"");
                    jsonBuffer.append(fieldValue.toString());
                    jsonBuffer.append("\"");
                } else if (fieldValue instanceof List<?>) {
                    addListToBuffer(jsonBuffer, fieldValue);
                } else if (fieldValue instanceof Map) {

                } else {
                    //对象类型数据
                    //递归
                    addObjectToJson(jsonBuffer, fieldValue);
                }
                jsonBuffer.append(",");
            }
            //去掉最后一个","
            replaceChar(i , fields,jsonBuffer);

        }
        //结束的"}"
        jsonBuffer.append("}");

    }

    private static void replaceChar(int i, List<Field> fields, StringBuffer jsonBuffer) {
        //去掉最后一个","
        if (i == fields.size() - 1 && jsonBuffer.charAt(jsonBuffer.length() - 1) == ',') {
            jsonBuffer.deleteCharAt(jsonBuffer.length() - 1);
        }
    }

    /**
     * 拼接集合类型数据
     * 递归
     *
     * @param jsonBuffer
     * @param fieldValue
     */
    private static void addListToBuffer(StringBuffer jsonBuffer, Object fieldValue) {
        List<?> list = (List<?>) fieldValue;
        jsonBuffer.append("[");

        for (int i = 0; i < list.size(); i++) {
            //遍历集合中的每一个元素
            addObjectToJson(jsonBuffer, list.get(i));
            if (i < list.size() - 1) {
                jsonBuffer.append(",");
            }
        }

        jsonBuffer.append("]");
    }

    /**
     * 获取当前class所有的成员变量
     * 会获取父类的成员变量
     * Objec 类型不需要
     * final 修饰的成员变量不需要
     *
     * @param aClass
     * @param fields
     */
    private static void getAllFields(Class<?> aClass, List<Field> fields) {
        if (null == fields) {
            fields = new ArrayList<>();
        }
        //排除Object类型
        if (aClass.getSuperclass() != null) {
            //拿到当前class所有成员变量的field
            Field[] fieldsSelf = aClass.getDeclaredFields();
            for (Field field : fieldsSelf) {
                //排除final修饰的成员变量
                if (!Modifier.isFinal(field.getModifiers())) {
                    fields.add(field);
                }

            }
            getAllFields(aClass.getSuperclass(), fields);
        }
    }
}
