import org.fanwenjie.briefjson.JSONBean;
import org.fanwenjie.briefjson.JSONSeriable;
import org.fanwenjie.briefjson.JSONSerializer;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * The JSONException is thrown when deserialize a illegal json.
 *
 * @author  Fan Wen Jie
 * @version 2015-03-05
 */

public class Role {

    /**
     * Bean Deserialized by Class
     */
    public static void Test1(){
        try{
            String json = "{\"Name\":\"\\u0053\\u0070\\u0069\\u006b\\u0065\",\"Sex\":true,\"Age\":75,\"Friend\":[{\"Name\":\"Tom\",\"Sex\":true,\"Age\":75,\"Friend\":[]},{\"Name\":\"Jerry\",\"Sex\":true,\"Age\":75,\"Friend\":[{\"Name\":\"Tom\",\"Sex\":true,\"Age\":75,\"Friend\":[]}]}]}";
            System.out.println(json);
            Object obj1 = JSONSerializer.deserialize(json);
            Role spike = JSONBean.deserialize(Role.class, obj1);
            System.out.println(spike.getFriend().get(1).getName());
            Object obj2 = JSONBean.serialize(spike);
            String text = JSONSerializer.serialize(obj2);
            System.out.println(text);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    /**
     * Bean Deserialized by Object Template
     */
    public static void Test2(){
        try{
            Role template = new Role();
            Role tmp1 = new Role();
            double tmp2 = 3.1415926;
            boolean tmp3 = false;
            String tmp4 = "TEST";
            template.getOtherList().add(tmp1);
            template.getOtherList().add(tmp2);
            template.getOtherList().add(tmp3);
            template.getOtherList().add(tmp4);
            String json = "{\"Name\":\"Tom\",\"Sex\":true,\"Age\":75,\"Friend\":[],\"Others\":[{\"Name\":\"Jerry\",\"Sex\":true,\"Age\":75,\"Friend\":[],\"Others\":[]},false,\"TEST\",3.14159]}";
            Object obj1 = JSONSerializer.deserialize(json);
            Role tom = JSONBean.deserialize(template,obj1);
            for(Object obj : tom.getOtherList())
                System.out.println(obj.toString());
        }catch (Exception ex){
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

    public ArrayList getOtherList() {
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

    @JSONSeriable(name = "Name",order = 0)
    private String name;
    @JSONSeriable(name = "Sex",order = 1)
    private boolean sex;
    @JSONSeriable(name = "Age",order = 2)
    private int age;
    @JSONSeriable(name = "Friend",order = 3)
    private ArrayList<Role> friend = new ArrayList<Role>();
    @JSONSeriable(name = "Others",order = 4)
    private ArrayList otherList = new ArrayList();
}
