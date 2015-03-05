package org.fanwenjie.briefjson;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Fan Wen Jie
 * @version 2015-03-05
 */
public class JSONSerializer {

    public static String serialize(Object object) throws IllegalArgumentException{
        if(object==null)
            return "null";
        if(object instanceof String)
            return '\"'+((String)object).replace("\b","\\b")
                    .replace("\t","\\t").replace("\r","\\r")
                    .replace("\f","\\f").replace("\n","\\n")+'\"';
        if(object instanceof Number || object instanceof Boolean)
            return object.toString();
        if(object instanceof Map){
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            Map map = (Map)object;
            for(Object key : map.keySet()){
                Object value = map.get(key);
                sb.append(serialize(key)).append(':').append(serialize(value)).append(',');
            }
            int last = sb.length()-1;
            if(sb.charAt(last)==',') sb.deleteCharAt(last);
            sb.append('}');
            return sb.toString();
        }
        if(object instanceof Collection){
            return serialize(((Collection)object).toArray());
        }
        if(object.getClass().isArray()){
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            int last = Array.getLength(object)-1;
            for(int i=0;i<=last;++i){
                Object value = Array.get(object,i);
                sb.append(serialize(value)).append(',');
            }
            last = sb.length()-1;
            if(sb.charAt(last)==',')    sb.deleteCharAt(last);
            sb.append(']');
            return sb.toString();
        }
        throw new IllegalArgumentException(object.toString());
    }

    public static Object deserialize(String json) throws JSONParseException {
        return new JSONSerializer(json).nextValue();
    }


    private int     position;
    private String  string;

    private JSONSerializer(String string){
        this.string = string;
        this.position = 0;
    }

    private Object nextValue() throws JSONParseException {
        char c = this.nextToken();
        switch (c) {

            case '{':
                LinkedHashMap<String,Object> map = new LinkedHashMap<String, Object>();
                while (true){
                    switch (nextToken()) {
                        case 0:
                            throw new JSONParseException(this.string,this.position,"Text which start with '{' must end with '}'");
                        case '}':
                            return map;
                    }
                    --position;
                    String key = nextValue().toString();
                    char ch = nextToken();
                    if (ch != ':') {
                        throw new JSONParseException(this.string,this.position,"Expected a ':' after a key");
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
                            throw new JSONParseException(this.string,this.position,"Expected a ',' or '}'");
                    }
                }


            case '[':
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
                                throw new JSONParseException(this.string,this.position,"Expected a ',' or ']'");
                        }
                    }
                }
                return list;

            case '"':
            case '\'':
                StringBuilder sb = new StringBuilder();
                while (true) {
                    char ch = this.string.charAt(position++);
                    switch (ch) {
                        case 0:
                        case '\n':
                        case '\r':
                            throw new JSONParseException(this.string,this.position,"Unterminated string");
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
                                    sb.append((char)Integer.parseInt(this.string.substring(position,position+=4), 16));
                                    break;
                                case '"':
                                case '\'':
                                case '\\':
                                case '/':
                                    sb.append(ch);
                                    break;
                                default:
                                    throw new JSONParseException(this.string,this.position,"Illegal escape.");
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

        StringBuilder sb = new StringBuilder();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = this.string.charAt(position++);
        }
        --position;

        String string = sb.toString().trim();
        if ("".equals(string)) {
            throw new JSONParseException(this.string,this.position,"Missing value");
        }

        if (string.equals("")) {
            return string;
        }
        if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("null")) {
            return null;
        }


        char b = string.charAt(0);
        if ((b >= '0' && b <= '9') || b == '-') {
            try {
                if (string.indexOf('.') > -1 || string.indexOf('e') > -1
                        || string.indexOf('E') > -1) {
                    Double d = Double.valueOf(string);
                    if (!d.isInfinite() && !d.isNaN()) {
                        return d;
                    }
                } else {
                    Long num = new Long(string);
                    if (string.equals(num.toString())) {
                        if (num == num.intValue()) {
                            return num.intValue();
                        } else {
                            return num;
                        }
                    }
                }
            } catch (Exception ignore) {}
        }
        return string;
    }



    private char nextToken() throws JSONParseException {
        try {
            while (this.string.charAt(position++) <= ' ') ;
            return this.string.charAt(position - 1);
        }catch (StringIndexOutOfBoundsException ignore){
            return 0;
        }
    }
}
