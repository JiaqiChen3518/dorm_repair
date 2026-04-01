# 宿舍报修管理系统

一个基于 Spring Boot + Vue 3 的现代化宿舍报修管理系统，提供完整的报修流程管理、用户管理、评价系统等功能。

## 项目简介

宿舍报修管理系统实现了从学生提交报修申请到管理员处理、完成、评价的完整流程。系统采用前后端分离架构，后端使用 Spring Boot 提供稳定的 API 服务，前端使用 Vue 3 构建用户友好的界面。

### 主要功能

- **用户管理**：注册、登录、个人信息管理、宿舍绑定
- **报修管理**：提交报修申请、查看报修记录、处理报修单、完成/取消报修
- **评价系统**：对维修服务进行评价、查看评价列表
- **通知系统**：系统通知、未读消息提醒、标记已读
- **文件上传**：支持图片上传（阿里云 OSS 存储）
- **验证码**：登录/注册验证码
- **数据统计**：报修单统计数据（管理员）

## 技术栈

### 后端技术
- **Spring Boot 3.4.3** - 核心框架
- **MyBatis 3.0.4** - 持久层框架
- **MySQL** - 数据库
- **JWT** - 令牌认证
- **阿里云 OSS** - 文件存储
- **Lombok** - 简化 Java 代码
- **Spring AOP** - 面向切面编程
- **Logback** - 日志管理
- **Maven** - 项目构建

### 前端技术
- **Vue 3** - 前端框架
- **Vue Router** - 路由管理
- **Axios** - HTTP 客户端
- **Vite** - 构建工具
- **原生 CSS** - 样式设计

## 项目结构

```
dorm_repair/
├── backend/                # 后端项目
│   └── dorm_repair/        # 后端核心代码
│       ├── src/            # 源代码
│       ├── pom.xml         # Maven 配置
├── frontend/               # 前端项目
│   ├── src/                # 源代码
│   ├── package.json        # 项目配置
│   ├── vite.config.js      # Vite 配置
├── docs/                   # 项目文档
│   ├── weekly_notes/       # 周学习笔记
│   └── weekly_reports/     # 周记
├── logs/                   # 日志文件
└── README.md               # 项目说明文档（本文件）
```

### 后端核心结构

```
backend/dorm_repair/src/main/java/com/qg/dorm/
├── aspect/            # 切面编程
├── common/            # 公共类
├── config/            # 配置类
├── constant/          # 常量类
├── controller/        # 控制器层
├── dto/               # 数据传输对象
├── entity/            # 实体类
├── exception/         # 异常处理
├── filter/            # 过滤器
├── interceptor/       # 拦截器
├── mapper/            # 数据访问层
├── service/           # 业务逻辑层
├── util/              # 工具类
└── DormApplication.java # 应用入口
```

### 前端核心结构

```
frontend/src/
├── api/             # API 模块
├── assets/          # 静态资源
├── components/      # 组件
├── router/          # 路由配置
├── utils/           # 工具类
├── views/           # 页面
├── App.vue          # 根组件
└── main.js          # 入口文件
```

## 快速开始

### 环境要求

- **后端**：JDK 17+、Maven 3.6+、MySQL 8.0+
- **前端**：Node.js 16+、npm 7+

### 1. 数据库配置

1. 创建数据库
```sql
CREATE DATABASE dormitory_repair CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行建表脚本
```bash
mysql -u root -p dormitory_repair < backend/dorm_repair/src/main/resources/db/schema.sql
```

3. 导入测试数据（可选）
```bash
mysql -u root -p dormitory_repair < backend/dorm_repair/src/main/resources/db/test_data.sql
```

### 2. 后端配置

1. 复制配置文件示例
```bash
cp backend/dorm_repair/src/main/resources/application.example.yaml backend/dorm_repair/src/main/resources/application.yaml
```

2. 修改 `application.yaml` 中的配置信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dormitory_repair?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

如需使用阿里云 OSS 上传图片，配置 OSS 相关信息：

```yaml
aliyun:
  oss:
    endpoint: oss-cn-beijing.aliyuncs.com
    accessKeyId: your_access_key_id
    accessKeySecret: your_access_key_secret
    bucketName: your_bucket_name
    urlPrefix: https://your_bucket.oss-cn-beijing.aliyuncs.com/
```

### 3. 后端运行

```bash
# 进入后端目录
cd backend/dorm_repair

# 编译项目
mvn clean package

# 运行项目
mvn spring-boot:run

# 或者直接运行 jar 包
java -jar target/dorm-0.0.1-SNAPSHOT.jar
```

后端 API 地址：http://localhost:8080/api

### 4. 前端配置

修改 `frontend/vite.config.js` 文件中的代理配置：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端服务地址
      changeOrigin: true
    }
  }
}
```

### 5. 前端运行

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

前端访问地址：http://localhost:5173

## 测试账号

- **学生账号**：3125000001 到 3125000010（共10个），密码都是 123456
- **管理员账号**：0025000001、0025000002，密码都是 123456

## API 文档

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `POST /api/user/logout` - 用户退出
- `GET /api/user/info` - 获取用户信息
- `POST /api/user/updatePassword` - 修改密码
- `POST /api/user/bindDormitory` - 绑定宿舍
- `POST /api/user/updateInfo` - 更新用户信息

### 报修单相关
- `POST /api/order/create` - 提交报修
- `GET /api/order/myOrders` - 查看我的报修列表
- `GET /api/order/all` - 查看所有报修列表（管理员）
- `GET /api/order/detail/{orderId}` - 查看报修详情
- `POST /api/order/updateStatus` - 更新报修状态（处理）
- `POST /api/order/complete` - 完成报修
- `POST /api/order/cancel/{orderId}` - 取消报修
- `POST /api/order/delete/{orderId}` - 删除报修
- `GET /api/order/statistics` - 获取统计数据

### 评价相关
- `POST /api/evaluation/create` - 提交评价
- `GET /api/evaluation/getByOrder/{orderId}` - 根据订单ID获取评价
- `GET /api/evaluation/all` - 获取所有评价

### 通知相关
- `GET /api/notification/myNotifications` - 查看我的通知列表
- `GET /api/notification/unread` - 查看未读通知
- `GET /api/notification/unreadCount` - 获取未读通知数量
- `POST /api/notification/markAsRead/{notificationId}` - 标记为已读
- `POST /api/notification/markAllAsRead` - 全部标记为已读

### 文件上传
- `POST /api/file/upload` - 上传文件

### 验证码
- `GET /api/captcha/generate` - 生成验证码

## 技术亮点

### 1. JWT 令牌认证

使用 JWT 令牌进行身份认证，相比传统的 Session 认证，具有以下优势：
- **无状态**：不需要在服务器端存储会话信息
- **跨域支持**：可以在不同域名间使用
- **性能好**：验证过程不需要查询数据库
- **安全性高**：令牌具有过期时间和签名验证

### 2. 阿里云 OSS 集成

集成阿里云 OSS 实现图片存储，相比本地存储，具有以下优势：
- **可靠性高**：数据多副本存储，不会丢失
- **访问速度快**：CDN 加速，用户访问图片速度快
- **扩展性强**：存储空间可按需扩展
- **成本低**：按使用量付费，无需维护服务器

### 3. 统一响应结果

使用 Result 类统一封装响应结果，具有以下优势：
- **一致性**：所有接口返回格式一致
- **可读性强**：包含状态码、消息和数据
- **错误处理**：便于前端统一处理错误

### 4. 全局异常处理

实现 GlobalExceptionHandler 统一处理异常，具有以下优势：
- **代码简洁**：不需要在每个方法中捕获异常
- **错误信息统一**：所有异常都以统一格式返回
- **便于调试**：详细的错误信息和堆栈跟踪

### 5. AOP 日志记录

使用 AOP 实现统一的日志记录，具有以下优势：
- **无侵入**：不需要在业务代码中添加日志代码
- **统一管理**：所有日志记录逻辑集中管理
- **详细信息**：记录请求参数、响应结果、执行时间等

## 开发规范

### 后端规范
- 使用 Lombok 简化实体类代码
- 统一使用 Result 类封装响应结果
- 常量统一抽取到 constant 包中
- 工具类统一放在 util 包中
- 异常统一处理，使用 GlobalExceptionHandler
- 类名使用大驼峰命名法（PascalCase）
- 方法名和变量名使用小驼峰命名法（camelCase）
- 常量使用全大写下划线分隔（UPPER_SNAKE_CASE）
- 包名使用全小写

### 前端规范
- 使用 Vue 3 组合式 API
- 组件命名使用 PascalCase
- 变量和方法命名使用 camelCase
- 常量命名使用 UPPER_SNAKE_CASE
- 代码缩进使用 2 个空格
- 提交信息使用中文，清晰描述修改内容
- 每次提交只包含一个功能或修复

## 常见问题

### 1. 图片上传失败
- 检查 OSS 配置是否正确
- 检查 Bucket 是否设置了公共读权限
- 检查网络连接是否正常

### 2. JWT Token 过期
- Token 有效期为 7 天
- 过期后需要重新登录获取新 Token

### 3. 数据库连接失败
- 检查 MySQL 服务是否启动
- 检查数据库连接信息是否正确
- 检查数据库是否已创建

### 4. 前端请求无法访问后端
- 检查 `vite.config.js` 中的代理配置是否正确
- 确保后端服务已启动
- 检查后端服务的端口是否与代理配置一致

### 5. 权限不足
- 检查用户角色是否正确
- 检查接口是否需要管理员权限

## 许可证

本项目仅供学习交流使用。