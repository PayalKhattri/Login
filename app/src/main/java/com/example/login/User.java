package com.example.login;

public class User {
    String id,editText,gender,age,dob,mobile,add,mh;
    public User(){

    }

    public User(String id,String editText, String gender, String age, String dob, String mobile, String add, String mh) {
       this.id=id;
        this.editText = editText;
        this.gender = gender;
        this.age = age;
        this.dob = dob;
        this.mobile = mobile;
        this.add = add;
        this.mh = mh;
    }

    public String getId() {
        return id;
    }

    public String getEditText() {
        return editText;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getDob() {
        return dob;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAdd() {
        return add;
    }

    public String getMh() {
        return mh;
    }
}
