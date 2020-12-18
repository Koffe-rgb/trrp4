package classes;

import com.google.gson.Gson;
import msg.ClientMsg;

public class JSONParser {
    public static String Parser(ClientMsg entities){
        Gson gson = new Gson();
        return gson.toJson(entities);
    }
}
