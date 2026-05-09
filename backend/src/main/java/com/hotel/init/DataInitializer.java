package com.hotel.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.entity.SysRoom;
import com.hotel.entity.SysUser;
import com.hotel.mapper.SysRoomMapper;
import com.hotel.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 数据初始化器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper userMapper;
    private final SysRoomMapper roomMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUsers();
        initRooms();
    }

    /**
     * 初始化用户数据
     */
    private void initUsers() {
        // 检查管理员是否存在
        SysUser existingAdmin = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
        if (existingAdmin == null) {
            SysUser admin = new SysUser();
            admin.setId(1L);
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRealName("系统管理员");
            admin.setPhone("13800000000");
            admin.setRole(1);
            userMapper.insert(admin);
            log.info("初始化管理员账号成功，密码: admin123");
        } else {
            // 更新密码确保正确
            existingAdmin.setPassword(passwordEncoder.encode("admin123"));
            userMapper.updateById(existingAdmin);
            log.info("更新管理员密码成功");
        }

        // 检查测试用户是否存在
        SysUser existingUser = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "testuser"));
        if (existingUser == null) {
            SysUser user = new SysUser();
            user.setId(2L);
            user.setUsername("testuser");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRealName("测试用户");
            user.setPhone("13800000001");
            user.setIdCard("110101199001011234");
            user.setRole(0);
            userMapper.insert(user);
            log.info("初始化测试用户成功，密码: user123");
        } else {
            // 更新密码确保正确
            existingUser.setPassword(passwordEncoder.encode("user123"));
            userMapper.updateById(existingUser);
            log.info("更新测试用户密码成功");
        }
    }

    /**
     * 初始化房间数据
     */
    private void initRooms() {
        Long roomCount = roomMapper.selectCount(null);
        if (roomCount > 0) {
            log.info("房间数据已存在，跳过初始化");
            return;
        }

        log.info("开始初始化房间数据...");

        // 生成100间单人间
        for (int i = 1; i <= 100; i++) {
            SysRoom room = new SysRoom();
            room.setId((long) i);
            room.setRoomNumber(String.format("单%03d", i));
            room.setRoomType(1);
            room.setBasePrice(new BigDecimal("100.00"));
            room.setSeasonCoefficient(new BigDecimal("1.00"));
            room.setStatus(0);
            roomMapper.insert(room);
        }

        // 生成100间双人间
        for (int i = 1; i <= 100; i++) {
            SysRoom room = new SysRoom();
            room.setId((long) (100 + i));
            room.setRoomNumber(String.format("双%03d", i));
            room.setRoomType(2);
            room.setBasePrice(new BigDecimal("180.00"));
            room.setSeasonCoefficient(new BigDecimal("1.00"));
            room.setStatus(0);
            roomMapper.insert(room);
        }

        log.info("房间数据初始化完成，共200间");
    }
}
