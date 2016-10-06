package shashank.com.callerinfo;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by shashank on 9/28/2016.
 */
@RealmClass
public class CallerIdentity implements RealmModel {

    private String number;
    private String name;
    private String tag;
    private boolean isSpam;
    private String operator;

    public CallerIdentity(String number, String name,String tag,boolean isSpam, String operator ){
    this.number = number;
    this.name = name;
    this.tag = tag;
    this.isSpam = isSpam;
    this.operator = operator;

    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public void setSpam(boolean spam) {
        isSpam = spam;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
