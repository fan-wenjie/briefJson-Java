package org.fanwenjie.briefjson;

/**
 * The JSONException is thrown when deserialize a illegal json.
 *
 * @author  Fan Wen Jie
 * @version 2015-03-05
 */
public class JSONParseException extends RuntimeException {
    private static final long serialVersionUID = 3674125742687171239L;
    private int position =0;
    private String json = "";

    /** Constructs a new json exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param json the json text which cause JSONExceptio
     * @param position  the position of illegal escape char at json text;
     * @param message the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public JSONParseException(String json, int position, String message){
        super(message);
        this.json = json;
        this.position = position;
    }

    @Override
    public String getMessage(){
        final int maxTipLength = 10;
        int start = position + 1 - maxTipLength;
        if(start<0) start=0;
        return  json.substring(start,position+1)+"< '"+json.charAt(position)+"' "
                +Integer.valueOf(position).toString()+":"+super.getMessage();
    }

    public String getJson(){return this.json;}

    public int getPosition(){return this.position;}

}
