import org.fanwenjie.briefjson.JSONBean;
import org.fanwenjie.briefjson.JSONSerializer;

/**
 * The JSONException is thrown when deserialize a illegal json.
 *
 * @author  Fan Wen Jie
 * @version 2015-03-05
 */

public class Main {
    public static void main(String[] args){
        try{
            String json = "{\"Name\":\"\\u0053\\u0070\\u0069\\u006b\\u0065\",\"Sex\":true,\"Age\":75,\"Friend\":[{\"Name\":\"Tom\",\"Sex\":true,\"Age\":75,\"Friend\":[]},{\"Name\":\"Jerry\",\"Sex\":true,\"Age\":75,\"Friend\":[{\"Name\":\"Tom\",\"Sex\":true,\"Age\":75,\"Friend\":[]}]}]}";
            System.out.println(json);
            Object obj1 = JSONSerializer.deserialize(json);
            Role spike = JSONBean.deserialize(Role.class,obj1);
            System.out.println(spike.getFriend().get(1).getName());
            Object obj2 = JSONBean.serialize(spike);
            String text = JSONSerializer.serialize(obj2);
            System.out.println(text);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
