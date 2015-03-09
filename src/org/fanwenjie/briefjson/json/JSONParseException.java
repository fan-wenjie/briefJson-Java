package org.fanwenjie.briefjson.json;

/**
 * The JSONException is thrown when deserialize a illegal json.
 *
 * @author Fan Wen Jie
 * @version 2015-03-05
 */
public class JSONParseException extends RuntimeException {
    private static final long serialVersionUID = 3674125742687171239L;
    private int position = 0;
    private String json = "";

    /**
     * Constructs a new json exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param json     the json text which cause JSONParseException
     * @param position the position of illegal escape char at json text;
     * @param message  the detail message. The detail message is saved for
     *                 later retrieval by the {@link #getMessage()} method.
     */
    public JSONParseException(String json, int position, String message) {
        super(message);
        this.json = json;
        this.position = position;
    }

    /**
     * Get message about error when parsing illegal json
     *
     * @return error message
     */
    @Override
    public String getMessage() {
        final int maxTipLength = 10;
        int end = position + 1;
        int start = end - maxTipLength;
        if (start < 0) start = 0;
        if (end > json.length()) end = json.length();
        return String.format("%s  (%d):%s", json.substring(start, end), position, super.getMessage());
    }

    public String getJson() {
        return this.json;
    }

    public int getPosition() {
        return this.position;
    }

}
