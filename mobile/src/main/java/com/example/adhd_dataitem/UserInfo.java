package com.example.adhd_dataitem;

public class UserInfo {
    private String email;
    private String id;
    private String who;
    private String high;
    private String weight;
    private String sex;
    private String DOB;
    private int age;
    private int sum;

    public UserInfo() {

    }

    public UserInfo(String email, String id, String who, String high, String weight, String sex, String DOB, int age, int sum) {
        this.email = email;
        this.id = id;
        this.who = who;
        this.high = high;
        this.weight = weight;
        this.sex = sex;
        this.DOB = DOB;
        this.age = age;
        this.sum = sum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
