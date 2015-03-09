package org.fanwenjie.briefjson.json;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Fan Wen Jie
 * @version 2015-03-05
 */
public class JSONSerializer {

    /**
     * Serializing a data object combined by values which types are Number Bollean Map Collection Array null to Json
     *
     * @param object object which will be serialized
     * @return Json string made from object
     * @throws IllegalArgumentException the node object of the data object whose type is not one of Number Bollean Map Collection Array null
     */

    public static String serialize(Object object) throws IllegalArgumentException {
        if (object == null)
            return "null";
        if (object instanceof String)
            return '\"' + ((String) object).replace("\b", "\\b")
                    .replace("\t", "\\t").replace("\r", "\\r")
                    .replace("\f", "\\f").replace("\n", "\\n") + '\"';
        if (object instanceof Number || object instanceof Boolean)
            return object.toString();
        if (object instanceof Map) {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            Map map = (Map) object;
            for (Object key : map.keySet()) {
                Object value = map.get(key);
                sb.append(serialize(key)).append(':').append(serialize(value)).append(',');
            }
            int last = sb.length() - 1;
            if (sb.charAt(last) == ',') sb.deleteCharAt(last);
            sb.append('}');
            return sb.toString();
        }
        if (object instanceof Collection) {
            return serialize(((Collection) object).toArray());
        }
        if (object.getClass().isArray()) {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            int last = Array.getLength(object) - 1;
            for (int i = 0; i <= last; ++i) {
                Object value = Array.get(object, i);
                sb.append(serialize(value)).append(',');
            }
            last = sb.length() - 1;
            if (sb.charAt(last) == ',') sb.deleteCharAt(last);
            sb.append(']');
            return sb.toString();
        }
        throw new IllegalArgumentException(object.toString());
    }

    /**
     * Deserializing a json string to data object
     *
     * @param json the json string which will be deserialized
     * @return the data object made from json
     * @throws ParseException th
     */
    public static Object deserialize(String json) throws ParseException {
        return new JSONSerializer(json).nextValue();
    }


    private int position;
    private String string;

    private JSONSerializer(String string) {
        this.string = string;
        this.position = 0;
    }

    private Object nextValue() throws ParseException {
        try {
            char c = this.nextToken();
            switch (c) {
                case '{':
                    try {
                        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                        while (true) {
                            if (nextToken() == '}') return map;
                            --position;
                            String key = nextValue().toString();
                            char ch = nextToken();
                            if (ch != ':') {
                                throw new ParseException(this.string, this.position, "Expected a ':' after a key");
                            }
                            map.put(key, nextValue());

                            switch (nextToken()) {
                                case ';':
                                case ',':
                                    if (nextToken() == '}') {
                                        return map;
                                    }
                                    --position;
                                    break;
                                case '}':
                                    return map;
                                default:
                                    throw new ParseException(this.string, this.position, "Expected a ',' or '}'");
                            }
                        }
                    } catch (StringIndexOutOfBoundsException ignore) {
                        throw new ParseException(this.string, this.position, "Expected a ',' or '}'");
                    }


                case '[':
                    try {
                        ArrayList<Object> list = new ArrayList<Object>();
                        if (nextToken() != ']') {
                            --position;
                            while (true) {
                                if (nextToken() == ',') {
                                    --position;
                                    list.add(null);
                                } else {
                                    --position;
                                    list.add(nextValue());
                                }
                                switch (nextToken()) {
                                    case ',':
                                        if (nextToken() == ']') {
                                            return list;
                                        }
                                        --position;
                                        break;
                                    case ']':
                                        return list;
                                    default:
                                        throw new ParseException(this.string, this.position, "Expected a ',' or ']'");
                                }
                            }
                        }
                        return list;
                    } catch (StringIndexOutOfBoundsException ignore) {
                        throw new ParseException(this.string, this.position, "Expected a ',' or ']'");
                    }


                case '"':
                case '\'':
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        char ch = this.string.charAt(position++);
                        switch (ch) {
                            case 0:
                            case '\n':
                            case '\r':
                                throw new ParseException(this.string, this.position, "Unterminated string");
                            case '\\':
                                ch = this.string.charAt(position++);
                                switch (ch) {
                                    case 'b':
                                        sb.append('\b');
                                        break;
                                    case 't':
                                        sb.append('\t');
                                        break;
                                    case 'n':
                                        sb.append('\n');
                                        break;
                                    case 'f':
                                        sb.append('\f');
                                        break;
                                    case 'r':
                                        sb.append('\r');
                                        break;
                                    case 'u':
                                        sb.append((char) Integer.parseInt(this.string.substring(position, position += 4), 16));
                                        break;
                                    case '"':
                                    case '\'':
                                    case '\\':
                                    case '/':
                                        sb.append(ch);
                                        break;
                                    default:
                                        throw new ParseException(this.string, this.position, "Illegal escape.");
                                }
                                break;
                            default:
                                if (ch == c) {
                                    return sb.toString();
                                }
                                sb.append(ch);
                        }
                    }
            }

            int startPosition = this.position;
            while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0)
                c = this.string.charAt(position++);
            String substr = this.string.substring(startPosition,position);
            --position;
            if (substr.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }
            if (substr.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }
            if (substr.equalsIgnoreCase("null")) {
                return null;
            }

            char b = "-+".indexOf(substr.charAt(0)) < 0 ? substr.charAt(0) : substr.charAt(1);
            if (b >= '0' && b <= '9') {
                try {
                    Long l = new Long(substr);
                    if (l.intValue() == l)
                        return l.intValue();
                    return l;
                } catch (NumberFormatException exInt) {
                    try {
                        return new Double(substr);
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
            return substr;
        } catch (StringIndexOutOfBoundsException ignore) {
            throw new ParseException(this.string, this.position, "Unexpected end");
        }
    }


    private char nextToken() throws StringIndexOutOfBoundsException {
        while (this.string.charAt(position++) <= ' ') ;
        return this.string.charAt(position - 1);
    }
}
