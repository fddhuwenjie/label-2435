package com.hotel.security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户主体信息
 */
@Data
@AllArgsConstructor
public class UserPrincipal {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    private Integer role;
}
