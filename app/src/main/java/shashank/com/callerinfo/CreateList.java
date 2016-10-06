package shashank.com.callerinfo;

import java.util.HashMap;

/**
 * Created by shashank on 9/30/2016.
 */
public class CreateList {

     private HashMap<String,CallerIdentity> hashmap;
    public void addItems(){
        CallerIdentity callerIdentity = new CallerIdentity("+917761841911","Shivam Srivastava","Student",false,"Airtel");
        hashmap = new HashMap<>();
        CallerIdentity callerIdentity2 = new CallerIdentity("+919631068230","Archana","Student",false,"Airtel");
        CallerIdentity callerIdentity3 = new CallerIdentity("121","Airtel Care","None",true,"Airtel");
        CallerIdentity callerIdentity4 = new CallerIdentity("123","Airtel Balance","Student",false,"Airtel");
        hashmap.put("+917761841911",callerIdentity);
        hashmap.put("+919631068230",callerIdentity2);
        hashmap.put("121",callerIdentity3);
        hashmap.put("123",callerIdentity4);



    }

    public String fetchName(String number){

        return hashmap.get(number).getName();

    }

    public String fetchTag(String number){

        return hashmap.get(number).getTag();

    }
    public String fetchOperator(String number){

        return hashmap.get(number).getOperator();

    }
    public boolean isSpam(String number){
        return hashmap.get(number).isSpam();
    }
}
