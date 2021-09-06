package idv.example.goodposture.user.my;

import java.io.Serializable;

public class Myinfo implements Serializable {
    private String id;
    private String name;
    private String account;
    private String age;
    private String nickname;
    private String gender;
    private String phone;
    private String birth;
    private String imagePath;

    public Myinfo() {
    }

    public Myinfo(String id, String name, String account, String age, String nickname,
                  String gender, String phone, String birth, String imagePath) {
        this.id = id;
        this.name = name;
        this.account = account;
        this.age = age;
        this.nickname = nickname;
        this.gender = gender;
        this.phone = phone;
        this.birth = birth;
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
