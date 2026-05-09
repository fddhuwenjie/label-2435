package com.hotel.vo;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 角色
     */
    private Integer role;

    /**
     * Token
     */
    private String token;
}
