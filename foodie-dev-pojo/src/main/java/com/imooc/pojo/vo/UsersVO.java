package com.imooc.pojo.vo;

import javax.persistence.Id;
import java.util.Date;

public class UsersVO {
    /**
     * 主键id 用户id
     */
    @Id
    private String id;

    /**
     * 用户名 用户名
     */
    private String username;


    /**
     * 昵称 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 头像
     */
    private String face;


    /**
     * 性别 性别 1:男  0:女  2:保密
     */
    private Integer sex;

    /**
     * 生日 生日
     */
    private Date birthday;



    //用户会话token
    private String userUniqueToken;

    public String getUserUniqueToken() {
        return userUniqueToken;
    }

    public void setUserUniqueToken(String userUniqueToken) {
        this.userUniqueToken = userUniqueToken;
    }

    /**
     * 获取主键id 用户id
     *
     * @return id - 主键id 用户id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键id 用户id
     *
     * @param id 主键id 用户id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取用户名 用户名
     *
     * @return username - 用户名 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名 用户名
     *
     * @param username 用户名 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * 获取昵称 昵称
     *
     * @return nickname - 昵称 昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置昵称 昵称
     *
     * @param nickname 昵称 昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取真实姓名
     *
     * @return realname - 真实姓名
     */
    public String getRealname() {
        return realname;
    }

    /**
     * 设置真实姓名
     *
     * @param realname 真实姓名
     */
    public void setRealname(String realname) {
        this.realname = realname;
    }

    /**
     * 获取头像
     *
     * @return face - 头像
     */
    public String getFace() {
        return face;
    }

    /**
     * 设置头像
     *
     * @param face 头像
     */
    public void setFace(String face) {
        this.face = face;
    }



    /**
     * 获取性别 性别 1:男  0:女  2:保密
     *
     * @return sex - 性别 性别 1:男  0:女  2:保密
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置性别 性别 1:男  0:女  2:保密
     *
     * @param sex 性别 性别 1:男  0:女  2:保密
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取生日 生日
     *
     * @return birthday - 生日 生日
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * 设置生日 生日
     *
     * @param birthday 生日 生日
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

}