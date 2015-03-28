import org.fanwenjie.briefjson.bean.BeanSerializer;
import org.fanwenjie.briefjson.bean.Seriable;
import org.fanwenjie.briefjson.json.JSONSerializer;

import java.util.ArrayList;

/**
 * The JSONException is thrown when deserialize a illegal json.
 *
 * @author Fan Wen Jie
 * @version 2015-03-05
 */

public class Role {

    /**
     * Bean Deserialized by Class
     */
    public static void Test1() {
        try {
            String json = "{\"Name\":\"\\u0053\\u0070\\u0069\\u006b\\u0065\",\"Sex\":true,\"Age\":75,\"Friend\":[{\"Name\":\"Tom\",\"Sex\":true,\"Age\":75,\"Friend\":[]},{\"Name\":\"Jerry\",\"Sex\":true,\"Age\":75,\"Friend\":[{\"Name\":\"Tom\",\"Sex\":true,\"Age\":75,\"Friend\":[]}]}]}";
            System.out.println(json);
            Object obj1 = JSONSerializer.deserialize(json);
            Role spike = BeanSerializer.deserialize(Role.class, obj1);
            System.out.println(spike.getFriend().get(1).getName());
            Object obj2 = BeanSerializer.serialize(spike);
            String text = JSONSerializer.serialize(obj2);
            System.out.println(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Bean Deserialized by Object Template
     */
    public static void Test2() {
        try {
            Role template = new Role();
            template.setOtherList(new Object[]{new Role(), 2.0, true, "TEMP"});
            String json = "{\"Name\":\"Tom\",\"Sex\":true,\"Age\":75,\"Friend\":[],\"Others\":[{\"Name\":\"Jerry\",\"Sex\":true,\"Age\":75,\"Friend\":[],\"Others\":[]},false,\"TEST\",3.14159]}";
            Object obj1 = JSONSerializer.deserialize(json);
            Role tom = BeanSerializer.deserialize(template, obj1);
            for (Object obj : tom.getOtherList())
                System.out.println(obj.toString());
            for (Object obj : template.getOtherList())
                System.out.println(obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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

    public Object[] getOtherList() {
        return otherList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setOtherList(Object[] otherList){
        this.otherList = otherList;
    }


    @Seriable(name = "Name", order = 0)
    private String name;
    @Seriable(name = "Sex", order = 1)
    private boolean sex;
    @Seriable(name = "Age", order = 2)
    private int age;
    @Seriable(name = "Friend", order = 3)
    private ArrayList<Role> friend = new ArrayList<Role>();
    @Seriable(name = "Others", order = 4)
    private Object[] otherList;
}
