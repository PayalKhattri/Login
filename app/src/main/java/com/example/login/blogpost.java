package com.example.login;
import java.util.Date;

public class blogpost extends BlogPostId{

    public String id;
    public String image;
    public String desc;


    public Date timestamp;


    public blogpost(){

    }

    public blogpost(String id, String image, String desc,Date timestamp) {
        this.id = id;
        this.image = image;
        this.desc = desc;
        this.timestamp = timestamp;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
