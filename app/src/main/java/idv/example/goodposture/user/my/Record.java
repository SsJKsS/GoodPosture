package idv.example.goodposture.user.my;

import java.util.Date;

public class Record {
    private String date;
    private String calorie;

    public Record() {
    }

    public Record(String date, String calorie) {
        this.date = date;
        this.calorie = calorie;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }
}
