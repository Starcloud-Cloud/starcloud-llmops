package com.starcloud.ops.business.user.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


public class WpUserDTO {

    @ExcelProperty("user_login")
    private String username;

    @ExcelProperty("user_nicename")
    private String nickname;

    @ExcelProperty("user_email")
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
