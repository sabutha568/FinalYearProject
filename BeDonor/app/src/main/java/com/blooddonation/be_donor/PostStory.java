package com.blooddonation.be_donor;

public class PostStory extends PostId {

    public String user_id,title,desc,image_url;


    public PostStory(){}

    public PostStory(String user_id, String title, String desc, String image_url) {
        this.user_id = user_id;
        this.title = title;
        this.desc = desc;
        this.image_url = image_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

}
