package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户更新请求
 */
@Data
public class UserUpdateRequest {

    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    private String password;

    @Size(max = 50, message = "真实姓名最多50个字符")
    private String realName;

    @Size(max = 255, message = "地址最多255个字符")
    private String address;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "^$|^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$", 
             message = "身份证号格式不正确")
    private String idCard;

    /**
     * 角色（仅管理员可修改）
     */
    private Integer role;
}
