package idv.example.goodposture.user.home;

public class Bodyinfo {
    private String id;
    private String gender;
    private String age;
    private String height;
    private String weight;

    public Bodyinfo() {
    }

    public Bodyinfo(String id, String gender, String age, String height, String weight) {
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
