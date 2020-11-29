package com.example.simplenewsystem;

import java.io.Serializable;            //序列化：对象--->字节数组

/**
 * Created by Administrator on 2018-11-09.
 * User类是一个简单的DTO对象，因此User对象是可序列化的
 * 这个可序列化对象可以放到Bundle中去
 */

public class User implements Serializable
{
    private String name;
    private String passwd;

    public User(String name, String passwd){
        this.name = name;
        this.passwd = passwd;
    }

    public String getName() {
        return name;
    }

    public String getPasswd() {
        return passwd;
    }
}
