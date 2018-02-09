package com.jpr.myapplication;

/**
 * 类描述:
 * 创建日期:2018/2/9 on 15:52
 * 作者:JiaoPeiRong
 */

public class User {
    private String name;
    private Long password;
//    private boolean isVip;
    private boolean vip;
    public User(){

    }

    public User(String name, Long password, boolean vip) {
        this.name = name;
        this.password = password;
        this.vip = vip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPassword() {
        return password;
    }

    public void setPassword(Long password) {
        this.password = password;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }
}
