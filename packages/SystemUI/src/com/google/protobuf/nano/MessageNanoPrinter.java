package com.google.protobuf.nano;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

public final class MessageNanoPrinter {
    private static void appendQuotedBytes(byte[] bArr, StringBuffer stringBuffer) {
        if (bArr == null) {
            stringBuffer.append("\"\"");
            return;
        }
        stringBuffer.append('\"');
        for (byte b : bArr) {
            int i = b & 255;
            if (i == 92 || i == 34) {
                stringBuffer.append('\\');
                stringBuffer.append((char) i);
            } else if (i < 32 || i >= 127) {
                stringBuffer.append(String.format("\\%03o", new Object[]{Integer.valueOf(i)}));
            } else {
                stringBuffer.append((char) i);
            }
        }
        stringBuffer.append('\"');
    }

    private static String deCamelCaseify(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (i == 0) {
                stringBuffer.append(Character.toLowerCase(charAt));
            } else if (Character.isUpperCase(charAt)) {
                stringBuffer.append('_');
                stringBuffer.append(Character.toLowerCase(charAt));
            } else {
                stringBuffer.append(charAt);
            }
        }
        return stringBuffer.toString();
    }

    private static String escapeString(String str) {
        int length = str.length();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt < ' ' || charAt > '~' || charAt == '\"' || charAt == '\'') {
                stringBuilder.append(String.format("\\u%04x", new Object[]{Integer.valueOf(charAt)}));
            } else {
                stringBuilder.append(charAt);
            }
        }
        return stringBuilder.toString();
    }

    public static <T extends MessageNano> String print(T t) {
        StringBuilder stringBuilder;
        if (t == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        try {
            print(null, t, new StringBuffer(), stringBuffer);
            return stringBuffer.toString();
        } catch (IllegalAccessException e) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Error printing proto: ");
            stringBuilder.append(e.getMessage());
            return stringBuilder.toString();
        } catch (InvocationTargetException e2) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Error printing proto: ");
            stringBuilder.append(e2.getMessage());
            return stringBuilder.toString();
        }
    }

    private static void print(String str, Object obj, StringBuffer stringBuffer, StringBuffer stringBuffer2) throws IllegalAccessException, InvocationTargetException {
        if (obj != null) {
            String name;
            if (obj instanceof MessageNano) {
                int modifiers;
                int length = stringBuffer.length();
                if (str != null) {
                    stringBuffer2.append(stringBuffer);
                    stringBuffer2.append(deCamelCaseify(str));
                    stringBuffer2.append(" <\n");
                    stringBuffer.append("  ");
                }
                Class cls = obj.getClass();
                Field[] fields = cls.getFields();
                int length2 = fields.length;
                for (int i = 0; i < length2; i++) {
                    Field field = fields[i];
                    modifiers = field.getModifiers();
                    String name2 = field.getName();
                    if (!("cachedSize".equals(name2) || (modifiers & 1) != 1 || (modifiers & 8) == 8 || name2.startsWith("_") || name2.endsWith("_"))) {
                        Class type = field.getType();
                        Object obj2 = field.get(obj);
                        if (type.isArray()) {
                            int i2;
                            Field[] fieldArr;
                            if (type.getComponentType() == Byte.TYPE) {
                                print(name2, obj2, stringBuffer, stringBuffer2);
                                i2 = length2;
                                fieldArr = fields;
                            } else {
                                i2 = obj2 == null ? 0 : Array.getLength(obj2);
                                for (modifiers = 0; modifiers < i2; modifiers++) {
                                    print(name2, Array.get(obj2, modifiers), stringBuffer, stringBuffer2);
                                }
                                i2 = length2;
                                fieldArr = fields;
                            }
                            fields = fieldArr;
                            length2 = i2;
                        } else {
                            print(name2, obj2, stringBuffer, stringBuffer2);
                        }
                    }
                }
                for (Method name3 : cls.getMethods()) {
                    name = name3.getName();
                    if (name.startsWith("set")) {
                        String substring = name.substring(3);
                        //try {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("has");
                            stringBuilder.append(substring);
                            try {
                                if (((Boolean) cls.getMethod(stringBuilder.toString(), new Class[0]).invoke(obj, new Object[0])).booleanValue()) {
                                    //try {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("get");
                                        stringBuilder.append(substring);
                                        try {
                                            print(substring, cls.getMethod(stringBuilder.toString(), new Class[0]).invoke(obj, new Object[0]), stringBuffer, stringBuffer2);
                                        } catch (NoSuchMethodException e) {
                                        }
                                    /*}  catch (NoSuchMethodException e2) {
                                    }*/
                                }
                            } catch (NoSuchMethodException e3) {
                            }
                        /*} catch (NoSuchMethodException e4) {
                        }*/
                    }
                }
                if (str != null) {
                    stringBuffer.setLength(length);
                    stringBuffer2.append(stringBuffer);
                    stringBuffer2.append(">\n");
                }
            }

            //else if (obj instanceof Map) {
            //     Map map = (Map) obj;
            //     String deCamelCaseify = deCamelCaseify(str);
            //     for (Entry entry : map.entrySet()) {
            //         stringBuffer2.append(stringBuffer);
            //         stringBuffer2.append(deCamelCaseify);
            //         stringBuffer2.append(" <\n");
            //         int length3 = stringBuffer.length();
            //         stringBuffer.append("  ");
            //         print("key", entry.getKey(), stringBuffer, stringBuffer2);
            //         print("value", entry.getValue(), stringBuffer, stringBuffer2);
            //         stringBuffer.setLength(length3);
            //         stringBuffer2.append(stringBuffer);
            //         stringBuffer2.append(">\n");
            //     }
            // } else {
            //     name = deCamelCaseify(str);
            //     stringBuffer2.append(stringBuffer);
            //     stringBuffer2.append(name);
            //     stringBuffer2.append(": ");
            //     if (obj instanceof String) {
            //         name = sanitizeString((String) obj);
            //         stringBuffer2.append("\"");
            //         stringBuffer2.append(name);
            //         stringBuffer2.append("\"");
            //     } else if (obj instanceof byte[]) {
            //         appendQuotedBytes((byte[]) obj, stringBuffer2);
            //     } else {
            //         stringBuffer2.append(obj);
            //     }
            //     stringBuffer2.append("\n");
            // }
        }
    } 

    private static String sanitizeString(String str) {
        if (!str.startsWith("http") && str.length() > 200) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str.substring(0, 200));
            stringBuilder.append("[...]");
            str = stringBuilder.toString();
        }
        return escapeString(str);
    }
}
