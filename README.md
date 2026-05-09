# 酒店预订管理系统

## How to Run

### Docker启动（推荐）

```bash
# 构建并启动所有服务
docker-compose up --build -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

启动后访问：
- 前端：http://localhost:8081
- 后端API：http://localhost:8435/api
- API文档：http://localhost:8435/api/swagger-ui.html

### 本地启动

#### 后端

```bash
# 进入后端目录
cd backend

# 确保MySQL和Redis已启动，并修改application.yml中的数据库配置

# 使用Maven构建并运行
mvn clean package -DskipTests
java -jar target/hotel-booking-1.0.0.jar

# 或使用Maven直接运行
mvn spring-boot:run
```

#### 前端

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 构建生产版本
npm run build
```

## Services

| 服务 | 端口 | 说明 |
|------|------|------|
| Frontend | 8081 | Vue3前端应用 |
| Backend | 8435 | Spring Boot后端API |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | testuser | user123 |

## 题目内容

1. 资源配置：固定100间单人间（room_type=1）、100间双人间（room_type=2），房间状态分「空闲（0）、已预订（1）、已入住（2）、维护（3）」 
2. 定价规则： 
- 基础价格可配置（单人间默认100元/晚，双人间默认180元/晚） 
- 季节调整：春季（3-5月）、夏季（6-8月）、秋季（9-11月）、冬季（12-2月），系数默认1.0（支持手动修改） 
- 时段折扣：周一至周五（weekday 1-5）按「基础价×季节系数×0.5」计算，周末（6-7）按「基础价×季节系数×1.0」计算 
3. 定金与取消规则： 
- 定金=总费用×10%（四舍五入保留2位小数），支付定金后预订生效 
- 取消计时：以「预订成功时间」为起点，≤6小时取消→全额退定金；＞6小时→定金不退（通过 LocalDateTime 计算时间差） 
4. 自动任务：每周一00:00自动生成「上周（周一至周日）预订清单」，支持Excel导出，存储路径可通过 application.yml 配置 

三、数据库设计（Java实体映射适配） 

要求生成建表SQL脚本（含索引、主键、外键约束），核心表与Java实体类字段一一对应（驼峰命名）： 

1. 用户表（sys_user）： 
- 字段：id（BIGINT，主键，雪花算法）、username（VARCHAR(50)，唯一）、password（VARCHAR(100)，BCrypt加密）、real_name（VARCHAR(50)）、address（VARCHAR(255)）、phone（VARCHAR(20)，唯一）、id_card（VARCHAR(18)，唯一）、role（TINYINT，0=普通用户，1=管理员）、create_time（DATETIME）、update_time（DATETIME） 
2. 房间表（sys_room）： 
- 字段：id（BIGINT，主键）、room_number（VARCHAR(20)，唯一，如「单001」「双056」）、room_type（TINYINT）、base_price（DECIMAL(10,2)）、season_coefficient（DECIMAL(3,2)）、status（TINYINT）、create_time（DATETIME）、update_time（DATETIME） 
3. 预订表（sys_reservation）： 
- 字段：id（BIGINT，主键）、user_id（BIGINT，外键关联sys_user.id）、room_id（BIGINT，外键关联sys_room.id）、check_in_date（DATE）、check_out_date（DATE）、reserve_days（INT，自动计算）、total_price（DECIMAL(10,2)）、deposit（DECIMAL(10,2)）、deposit_status（TINYINT，0=未支付，1=已支付，2=已退款，3=已扣罚）、reserve_time（DATETIME）、cancel_time（DATETIME，可为NULL）、status（TINYINT，0=待入住，1=已取消，2=已完成，3=已违约） 
4. 定金记录表（sys_deposit_record）： 
- 字段：id（BIGINT，主键）、reservation_id（BIGINT，外键）、amount（DECIMAL(10,2)）、operate_type（TINYINT，0=支付，1=退款，2=扣罚）、operate_time（DATETIME）、remark（VARCHAR(255)） （二）用户模块（普通用户，role=0） 
二 用户 
1. 注册： 
2. 登录： 
3. 房间查询： 
4. 房间预订： 
5. 取消预订： 
（三）管理员模块（role=1） 

1. 用户管理：SysUserService实现增删改查，新增可指定role，修改支持密码重置（加密后更新） 
2. 房间管理：修改base_price、season_coefficient、status，查询房间占用统计（按类型、状态分组） 
3. 预订管理：多条件筛选（时间范围、状态、用户ID），手动处理异常订单（强制取消，需填remark） 
4. 定金管理：查询deposit_record，统计周期内收支，导出统计报表 
5. 清单导出：

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2
- MyBatis Plus
- MySQL 8.0
- Redis
- JWT认证
- EasyExcel导出

### 前端
- Vue 3
- Vite
- Element Plus
- Pinia
- Vue Router
- Axios

## 项目结构

```
├── backend/                          # 后端项目
│   ├── src/main/java/com/hotel/
│   │   ├── HotelBookingApplication.java    # 启动类
│   │   ├── common/                   # 公共类
│   │   │   ├── Constants.java        # 常量定义
│   │   │   ├── PageResult.java       # 分页结果
│   │   │   └── Result.java           # 统一响应
│   │   ├── config/                   # 配置类
│   │   │   ├── MybatisPlusConfig.java
│   │   │   └── SecurityConfig.java   # 安全配置(CORS/JWT)
│   │   ├── controller/               # 控制器
│   │   │   ├── AdminController.java  # 管理员接口
│   │   │   ├── AuthController.java   # 认证接口
│   │   │   └── UserController.java   # 用户接口
│   │   ├── dto/                      # 数据传输对象
│   │   ├── entity/                   # 实体类
│   │   │   ├── SysUser.java          # 用户
│   │   │   ├── SysRoom.java          # 房间
│   │   │   ├── SysReservation.java   # 预订
│   │   │   └── SysDepositRecord.java # 定金记录
│   │   ├── exception/                # 异常处理
│   │   ├── init/                     # 初始化
│   │   │   └── DataInitializer.java  # 数据初始化
│   │   ├── mapper/                   # MyBatis映射
│   │   ├── security/                 # 安全相关
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── JwtTokenProvider.java
│   │   ├── service/                  # 业务服务
│   │   │   ├── SysUserService.java
│   │   │   ├── SysRoomService.java
│   │   │   ├── SysReservationService.java
│   │   │   ├── SysDepositRecordService.java
│   │   │   └── ExportService.java    # Excel导出
│   │   ├── task/                     # 定时任务
│   │   │   └── WeeklyReportTask.java # 周报任务
│   │   ├── util/                     # 工具类
│   │   │   ├── PriceCalculator.java  # 价格计算
│   │   │   └── SecurityUtils.java
│   │   └── vo/                       # 视图对象
│   └── src/main/resources/
│       ├── application.yml           # 配置文件
│       └── schema.sql                # 数据库脚本
│
├── frontend/                         # 前端项目
│   ├── src/
│   │   ├── api/                      # API接口
│   │   │   ├── admin.js              # 管理员API
│   │   │   ├── auth.js               # 认证API
│   │   │   └── user.js               # 用户API
│   │   ├── layouts/                  # 布局组件
│   │   │   ├── MainLayout.vue        # 用户布局
│   │   │   └── AdminLayout.vue       # 管理员布局
│   │   ├── router/                   # 路由配置
│   │   ├── stores/                   # Pinia状态
│   │   │   └── user.js               # 用户状态
│   │   ├── styles/                   # 全局样式
│   │   ├── utils/                    # 工具函数
│   │   │   └── request.js            # Axios封装
│   │   ├── views/                    # 页面组件
│   │   │   ├── Login.vue             # 登录
│   │   │   ├── Register.vue          # 注册
│   │   │   ├── admin/                # 管理员页面
│   │   │   │   ├── Users.vue         # 用户管理
│   │   │   │   ├── Rooms.vue         # 房间管理
│   │   │   │   ├── Reservations.vue  # 预订管理
│   │   │   │   └── Deposits.vue      # 定金管理
│   │   │   └── user/                 # 用户页面
│   │   │       ├── Rooms.vue         # 房间查询
│   │   │       ├── MyReservations.vue# 我的预订
│   │   │       └── Profile.vue       # 个人中心
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
│
├── docker-compose.yml                # Docker编排
└── README.md
```