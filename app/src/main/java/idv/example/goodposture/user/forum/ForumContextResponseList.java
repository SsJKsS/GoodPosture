package idv.example.goodposture.user.forum;

import android.widget.TextView;

public class ForumContextResponseList {
    private String tv_Response_User;
    private String tv_Response_Time;
    private String tv_Response_Context;

    public ForumContextResponseList() {
    }

    public ForumContextResponseList(String tv_Response_User, String tv_Response_Time, String tv_Response_Context) {
        this.tv_Response_User = tv_Response_User;
        this.tv_Response_Time = tv_Response_Time;
        this.tv_Response_Context = tv_Response_Context;
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
}
