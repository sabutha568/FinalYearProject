package com.blooddonation.be_donor;

public class Message {

    public String user_id,msg;

    public Message(){};

    public Message(String user_id, String msg) {
        this.user_id = user_id;
        this.msg = msg;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
