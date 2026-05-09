package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.PageResult;
import com.hotel.dto.RegisterRequest;
import com.hotel.dto.UserUpdateRequest;
import com.hotel.entity.SysUser;
import com.hotel.exception.BusinessException;
import com.hotel.mapper.SysUserMapper;
import com.hotel.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    private final PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    @Transactional
    public SysUser register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已被注册");
        }

        // 检查身份证号是否已存在
        if (StringUtils.hasText(request.getIdCard()) && existsByIdCard(request.getIdCard())) {
            throw new BusinessException("身份证号已被注册");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(StringUtils.hasText(request.getRealName()) ? request.getRealName() : null);
        user.setAddress(StringUtils.hasText(request.getAddress()) ? request.getAddress() : null);
        user.setPhone(request.getPhone());
        user.setIdCard(StringUtils.hasText(request.getIdCard()) ? request.getIdCard() : null);
        user.setRole(0); // 默认普通用户

        save(user);
        return user;
    }

    /**
     * 根据用户名查询
     */
    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)) > 0;
    }

    /**
     * 检查手机号是否存在
     */
    public boolean existsByPhone(String phone) {
        return count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone)) > 0;
    }

    /**
     * 检查身份证号是否存在
     */
    public boolean existsByIdCard(String idCard) {
        return count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getIdCard, idCard)) > 0;
    }

    /**
     * 分页查询用户
     */
    public PageResult<UserVO> pageUsers(Integer current, Integer size, String username, String phone, Integer role) {
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (StringUtils.hasText(phone)) {
            wrapper.like(SysUser::getPhone, phone);
        }
        if (role != null) {
            wrapper.eq(SysUser::getRole, role);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = page(page, wrapper);
        List<UserVO> records = result.getRecords().stream()
                .map(UserVO::fromEntity)
                .collect(Collectors.toList());

        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), records);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public void updateUser(Long id, UserUpdateRequest request) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查手机号是否被其他用户使用
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            if (existsByPhone(request.getPhone())) {
                throw new BusinessException("手机号已被其他用户使用");
            }
            user.setPhone(request.getPhone());
        }

        // 检查身份证号是否被其他用户使用
        if (StringUtils.hasText(request.getIdCard()) && !request.getIdCard().equals(user.getIdCard())) {
            if (existsByIdCard(request.getIdCard())) {
                throw new BusinessException("身份证号已被其他用户使用");
            }
            user.setIdCard(request.getIdCard());
        }

        // 更新密码
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // 更新其他信息
        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getAddress())) {
            user.setAddress(request.getAddress());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        updateById(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getRole() == 1) {
            throw new BusinessException("不能删除管理员账号");
        }
        removeById(id);
    }

    /**
     * 创建用户（管理员）
     */
    @Transactional
    public SysUser createUser(RegisterRequest request, Integer role) {
        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已被注册");
        }

        // 检查身份证号是否已存在
        if (StringUtils.hasText(request.getIdCard()) && existsByIdCard(request.getIdCard())) {
            throw new BusinessException("身份证号已被注册");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(StringUtils.hasText(request.getRealName()) ? request.getRealName() : null);
        user.setAddress(StringUtils.hasText(request.getAddress()) ? request.getAddress() : null);
        user.setPhone(request.getPhone());
        user.setIdCard(StringUtils.hasText(request.getIdCard()) ? request.getIdCard() : null);
        user.setRole(role != null ? role : 0);

        save(user);
        return user;
    }
}
