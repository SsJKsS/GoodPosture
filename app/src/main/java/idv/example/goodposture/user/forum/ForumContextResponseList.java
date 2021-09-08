package idv.example.goodposture.user.forum;

import android.widget.TextView;

import java.io.Serializable;

public class ForumContextResponseList implements Serializable {
    private String tv_Response_User;
    private String tv_Response_Time;
    private String tv_Response_Context;
    private String id;
    private String br_id;

    public ForumContextResponseList() {
    }

    public ForumContextResponseList(String tv_Response_User, String tv_Response_Time, String tv_Response_Context, String id,String br_id) {
        this.tv_Response_User = tv_Response_User;
        this.tv_Response_Time = tv_Response_Time;
        this.tv_Response_Context = tv_Response_Context;
        this.id = id;
        this.br_id = br_id;
    }

    public String getTv_Response_User() {
        return tv_Response_User;
    }

    public void setTv_Response_User(String tv_Response_User) {
        this.tv_Response_User = tv_Response_User;
    }

    public String getTv_Response_Time() {
        return tv_Response_Time;
    }

    public void setTv_Response_Time(String tv_Response_Time) {
        this.tv_Response_Time = tv_Response_Time;
    }

    public String getTv_Response_Context() {
        return tv_Response_Context;
    }

    public void setTv_Response_Context(String tv_Response_Context) {
        this.tv_Response_Context = tv_Response_Context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBr_id() {
        return br_id;
    }

    public void setBr_id(String br_id) {
        this.br_id = br_id;
    }
}
