import org.fanwenjie.briefjson.JSONSeriable;

import java.util.ArrayList;

/**
 * The JSONException is thrown when deserialize a illegal json.
 *
 * @author  Fan Wen Jie
 * @version 2015-03-05
 */

public class Role {
    public String getName() {
        return name;
    }

    public boolean isSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public ArrayList<Role> getFriend() {
        return friend;
    }

    @JSONSeriable(name = "Name",order = 0)
    private String name;
    @JSONSeriable(name = "Sex",order = 1)
    private boolean sex;
    @JSONSeriable(name = "Age",order = 2)
    private int age;
    @JSONSeriable(name = "Friend",order = 3)
    private ArrayList<Role> friend = new ArrayList<Role>();
}
