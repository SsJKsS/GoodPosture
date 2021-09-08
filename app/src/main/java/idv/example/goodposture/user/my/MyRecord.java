package idv.example.goodposture.user.my;

public class MyRecord {
    private String uid;
    private String date;
    private String time;
    private String calorieType;
    private Float calorie;

    public MyRecord() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCalorieType() {
        return calorieType;
    }

    public void setCalorieType(String calorieType) {
        this.calorieType = calorieType;
    }

    public Float getCalorie() {
        return calorie;
    }

    public void setCalorie(Float calorie) {
        this.calorie = calorie;
    }
}
