# Web 后端实战 - 部门管理笔记

## 一、Web 开发整体体系

### 1. 技术分层

- **Web 前端基础**：HTML、CSS、JavaScript、Vue3、Ajax/Axios
- **Web 后端基础**：Maven、Web 基础知识、MySQL、JDBC、Mybatis
- **实战环节**：Tlias 智能学习辅助系统（前后端）
- **项目部署**：Linux、Docker

### 2. Tlias 系统核心需求

- 部门管理：查询、新增、修改、删除
- 员工管理：查询、新增、修改、删除、文件上传
- 报表统计、登录认证、日志管理
- 班级、学员管理（实战核心）

## 二、开发准备工作

### 1. 开发模式：前后端分离开发

#### （1）传统前后端混合开发问题

分工不明确、难以维护、不便管理

#### （2）前后端分离开发优势

开发、部署完全分开，前后端并行开发，效率更高

#### （3）核心开发流程

需求分析` → `接口设计（API接口文档）` → `前后端并行开发（遵守规范）` → `单独测试` → `前后端联调测试

#### （4）核心协作载体

**接口文档**：定义请求、响应格式，作为前后端开发的统一标准

### 2. 开发规范：Restful 风格

#### （1）核心概念

REST（表述性状态转换）是**软件架构风格**，核心为`URL定位资源，HTTP动词描述操作`，非强制规定，可灵活调整。

#### （2）传统风格 VS Restful 风格对比

|     传统风格 URL      | 请求方式 |       含义       | Restful 风格 URL | 请求方式 |              备注               |
| :-------------------: | :------: | :--------------: | :--------------: | :------: | :-----------------------------: |
|  /user/getById?id=1   |   GET    | 查询 id=1 的用户 |     /users/1     |   GET    | URL 用复数（users）表示资源集合 |
|    /user/saveUser     |   POST   |     新增用户     |      /users      |   POST   |        简洁、规范、优雅         |
|   /user/updateUser    |   POST   |     修改用户     |      /users      |   PUT    |                -                |
| /user/deleteUser?id=1 |   GET    | 删除 id=1 的用户 |     /users/1     |  DELETE  |                -                |

#### （3）核心特点与操作映射

- 特点：URL 定义资源，HTTP 动词描述操作

- 操作映射：

  - GET：查询操作
  - POST：新增操作
  - PUT：修改操作
  - DELETE：删除操作

  

### 3. 接口测试工具：Apifox

#### （1）核心作用

解决浏览器仅支持 GET 请求的问题，支持 POST/PUT/DELETE 等请求方式，完成**接口调试、文档管理、Mock 服务**。

#### （2）工具定位

Apifox = Postman + Swagger + Mock + JMeter，一体化 API 协作平台

#### （3）核心使用场景

- 后端开发完接口后，快速测试接口可用性
- 前端开发时，通过 Mock 服务获取模拟数据，测试页面渲染

### 4. 工程搭建步骤

1. **创建 SpringBoot 工程**：引入依赖`Spring Web`、`MyBatis Framework`、`MySQL Driver`、`Lombok`
2. **数据库准备**：创建`dept`部门表，在`application.yml`配置数据库连接信息
3. **基础代码结构**：创建实体类`Dept`、统一响应结果封装类`Result`

#### （4）统一响应结果封装`Result`

```java
@Data
public class Result {
    private Integer code; // 1=成功，0=失败
    private String msg; // 错误/提示信息
    private Object data; // 响应数据
}
```

## 三、部门管理功能实现

### 核心技术：三层架构开发

- **Controller**：接收请求、调用 Service、响应结果
- **Service**：业务逻辑处理，调用 Mapper 接口
- **Mapper(Dao)**：数据访问，编写 SQL 操作数据库

### 1. 查询部门

#### （1）功能需求

查询所有部门数据，按最后修改时间倒序排列

#### （2）三层架构实现

##### Controller 层

```java
@RestController
public class DeptController {
    @Autowired
    private DeptService deptService;
    // Restful风格：GET请求查询资源
    @GetMapping("/depts")
    public Result findAll(){
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }
}
```

##### Service 层

```java
@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptMapper deptMapper;
    @Override
    public List<Dept> findAll() {
        return deptMapper.findAll();
    }
}
```

##### Mapper 层

```java
@Mapper
public interface DeptMapper {
    @Select("select * from dept order by update_time desc")
    List<Dept> findAll();
}
```

#### （3）MyBatis 数据封装问题解决

**默认规则**：实体类属性名与数据库表字段名**完全一致**时，MyBatis 自动封装。

**不一致解决方案**（推荐优先级从高到低）：

1. 开启驼峰命名

   ：配置

   ```yaml
   application.yml 自动映射xxx_abc→xxxAbc
   ```

   ```yaml
   mybatis:
     configuration:
       map-underscore-to-camel-case: true
   ```

   

2. SQL 起别名

   ：将数据库字段名改为实体类属性名

   ```java
   @Select("select id, name, create_time createTime, update_time updateTime from dept order by update_time desc")
   List<Dept> findAll();
   ```

3. 手动结果映射

   ：通过

   ```
   @Results+@Result
   ```

   ```xml
   @Results({
       @Result(column = "create_time", property = "createTime"),
       @Result(column = "update_time", property = "updateTime")
   })
   @Select("select * from dept order by update_time desc")
   List<Dept> findAll();
   ```

   

#### （4）前后端联调：Nginx 反向代理

##### 核心问题

前端请求地址`http://localhost:90/api/depts`，后端服务地址`http://localhost:8080/depts`，跨端口访问需代理。

##### 反向代理概念

通过代理服务器（Nginx）接收客户端请求，转发给后端服务器，具备**安全、灵活、负载均衡**优势。

##### Nginx 核心配置

```xml
server {
   listen 90; # 前端访问端口
   location ^~ /api/ { # 匹配/api/开头的请求
      rewrite ^/api/(.*)$ /$1 break; # 重写路径，去掉/api/
      proxy_pass http://localhost:8080; # 转发到后端Tomcat
   }
}
```

### 2. 删除部门

#### （1）功能需求

根据部门主键 ID 删除指定部门数据

#### （2）Controller 参数接收方式（简单参数）

Restful 风格：DELETE 请求，参数为`id`（如`/depts?id=8`），三种接收方式：

1. 原始 HttpServletRequest（繁琐）

   ```java
   @DeleteMapping("/depts")
   public Result delete(HttpServletRequest request){
       String idStr = request.getParameter("id");
       int id = Integer.parseInt(idStr);
       deptService.delete(id);
       return Result.success();
   }
   ```

2. @RequestParam 注解（参数名不一致时使用）

   ```java
   @DeleteMapping("/depts")
   // required=true（默认）：参数必须传递，否则报错；可选则设为false
   public Result delete(@RequestParam("id") Integer deptId){
       deptService.delete(deptId);
       return Result.success();
   }
   ```

3. 直接定义形参（推荐，参数名与请求参数一致）

   ```java
   @DeleteMapping("/depts")
   public Result delete(Integer id){
       deptService.delete(id);
       return Result.success();
   }
   ```

   

#### （3）三层架构实现（Service+Mapper）

##### Service 层

```java
@Override
public void delete(Integer id) {
    deptMapper.delete(id);
}
```

##### Mapper 层

```java
@Mapper
public interface DeptMapper {
    @Delete("delete from dept where id = #{id}")
    void delete(Integer id);
}
```

### 3. 新增部门

#### （1）功能需求

新增部门名称，自动补全创建时间、更新时间

#### （2）请求规范

- 请求方式：POST
- 请求路径：/depts
- 请求参数：JSON 格式（`{"name":"教研部"}`）
- 注解：`@RequestBody`接收 JSON 格式参数

#### （3）三层架构实现

##### Controller 层

```java
@PostMapping("/depts")
// @RequestBody：将JSON请求体转为实体对象，键名与属性名一致
public Result add(@RequestBody Dept dept){
    deptService.add(dept);
    return Result.success();
}
```

##### Service 层

```java
@Override
public void add(Dept dept) {
    // 补全公共字段：创建时间、更新时间
    dept.setCreateTime(LocalDateTime.now());
    dept.setUpdateTime(LocalDateTime.now());
    deptMapper.add(dept);
}
```

##### Mapper 层

```java
@Insert("insert into dept(name, create_time, update_time) values(#{name}, #{createTime}, #{updateTime})")
void add(Dept dept);
```

#### （4）核心知识点

- JSON 参数接收：**实体对象 +@RequestBody**，适用于 POST/PUT 请求
- 公共字段补全：创建 / 更新时间等非前端传递字段，在 Service 层补全

### 4. 修改部门

#### （1）功能需求

分两步：① 根据 ID 查询部门（回显数据）；② 根据 ID 修改部门名称，更新修改时间

#### （2）步骤 1：根据 ID 查询部门（路径参数）

##### 请求规范

- 请求方式：GET
- 请求路径：/depts/{id}（如`/depts/1`）
- 路径参数：通过`@PathVariable`接收

##### Controller 层

```java
// 路径参数用{}标识，@PathVariable绑定形参
@GetMapping("/depts/{id}")
// 参数名一致时，可省略@PathVariable("id")
public Result getInfo(@PathVariable Integer id){
    Dept dept = deptService.getInfo(id);
    return Result.success(dept);
}
```

##### Service+Mapper 层

```java
// Service层
@Override
public Dept getInfo(Integer id) {
    return deptMapper.getById(id);
}
// Mapper层
@Select("select id, name, create_time, update_time from dept where id = #{id}")
Dept getById(Integer id);
```

#### （3）步骤 2：修改部门数据

##### 请求规范

- 请求方式：PUT
- 请求路径：/depts
- 请求参数：JSON 格式（`{"id":1,"name":"新教研部"}`）

##### Controller 层

```java
@PutMapping("/depts")
public Result update(@RequestBody Dept dept){
    deptService.update(dept);
    return Result.success();
}
```

##### Service+Mapper 层

```java
java// Service层
@Override
public void update(Dept dept) {
    // 补全更新时间
    dept.setUpdateTime(LocalDateTime.now());
    deptMapper.update(dept);
}
// Mapper层
@Update("update dept set name = #{name}, update_time = #{updateTime} where id = #{id}")
void update(Dept dept);
```

#### （4）路径参数扩展

URL 可携带多个路径参数，如`/depts/{id}/{type}`，接收方式：

```java
@GetMapping("/depts/{id}/{type}")
public Result getInfo(@PathVariable Integer id, @PathVariable String type){
    // 业务逻辑
    return Result.success();
}
```

### 5. @RequestMapping 注解优化

#### （1）使用场景

当多个接口路径前缀相同时（如所有部门接口都是`/depts`开头），可在**类上**添加`@RequestMapping`，方法上仅保留请求方式注解。

#### （2）优化后代码

```java
@RestController
@RequestMapping("/depts") // 类上定义公共路径
public class DeptController {
    @Autowired
    private DeptService deptService;

    @GetMapping // 完整路径：/depts
    public Result findAll(){...}

    @DeleteMapping // 完整路径：/depts
    public Result delete(Integer id){...}

    @PostMapping // 完整路径：/depts
    public Result add(@RequestBody Dept dept){...}

    @GetMapping("/{id}") // 完整路径：/depts/{id}
    public Result getInfo(@PathVariable Integer id){...}

    @PutMapping // 完整路径：/depts
    public Result update(@RequestBody Dept dept){...}
}
```

#### （3）核心规则

**完整请求路径 = 类上 @RequestMapping 值 + 方法上 @RequestMapping / 请求方式注解值**

## 四、日志技术

### 1. 日志的作用

替代`System.out.println`，解决控制台输出的**不可扩展、不易维护**问题，核心用途：

- 问题排查：记录程序运行异常信息
- 数据追踪：记录用户操作、接口调用轨迹
- 性能优化：分析程序运行耗时
- 系统监控：监控程序运行状态

### 2. 主流日志框架

- **JUL**：JavaSE 官方日志，配置简单、性能差、灵活性低
- **Log4j**：经典日志框架，配置灵活，支持多输出目标
- **Logback**：基于 Log4j 升级，性能更优、功能更全（**推荐使用**）
- **Slf4j**：日志门面，定义统一日志接口，底层可对接 Logback/Log4j 等框架

### 3. Logback 快速使用（SpringBoot 集成）

#### （1）准备工作

SpringBoot 已自动引入 Logback 依赖，无需手动导入，仅需在`resources`下添加配置文件`logback.xml`。

#### （2）核心步骤

1. **定义 Logger 对象**：通过`LoggerFactory`获取
2. **调用日志方法**：根据级别调用`debug/info/warn/error`等方法

#### （3）代码示例

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeptController {
    // 定义Logger对象，传入当前类的Class
    private static final Logger log = LoggerFactory.getLogger(DeptController.class);

    @DeleteMapping("/depts")
    public Result delete(Integer id){
        // 日志记录，替代System.out.println，支持占位符{}
        log.info("根据ID删除部门，ID：{}", id);
        deptService.delete(id);
        return Result.success();
    }
}
```

#### （4）Lombok 简化：@Slf4j

添加`@Slf4j`注解，无需手动定义 Logger 对象，直接使用`log`变量：

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j // 自动生成Logger对象log
public class DeptController {
    @DeleteMapping("/depts")
    public Result delete(Integer id){
        log.info("根据ID删除部门，ID：{}", id);
        deptService.delete(id);
        return Result.success();
    }
}
```

### 4. Logback 配置文件（logback.xml）

#### （1）核心作用

配置日志的**输出位置、输出格式、日志级别**，核心标签：

- ```
  <appender>
  ```

  ：定义日志输出目标，常用 2 种：

  1. `ConsoleAppender`：控制台输出
  2. `RollingFileAppender`：文件输出（支持日志滚动）

- `<root>`：全局日志配置，指定日志级别和关联的 appender

#### （2）基础配置示例

```java
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 控制台输出appender -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- 日志输出格式 -->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 文件输出appender（滚动生成） -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/tlias.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/tlias-%d{yyyy-MM-dd}.log</fileNamePattern>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 全局日志级别配置：info级别，关联控制台和文件输出 -->
    <root level="info">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

### 5. Logback 日志级别

#### （1）级别划分（从低到高）

trace` < `debug` < `info` < `warn` < `error

#### （2）级别说明与使用场景

| 级别  |          说明          |                        使用场景                        |
| :---: | :--------------------: | :----------------------------------------------------: |
| trace | 追踪，记录程序运行轨迹 |                几乎不用，仅极端调试场景                |
| debug |        调试信息        |   开发阶段，记录程序调试过程（如接口入参、中间结果）   |
| info  |        普通信息        | 生产环境，记录程序关键运行状态（如接口调用、服务启动） |
| warn  |        警告信息        |          记录潜在风险（如参数为空、资源不足）          |
| error |        错误信息        |     记录程序异常（如数据库连接失败、接口调用报错）     |

#### （3）核心规则

配置文件中指定的日志级别，会**输出大于等于该级别的所有日志**，例如：

- 配置`root level="info"`：输出`info`、`warn`、`error`级别日志，忽略`trace`、`debug`
- 配置`root level="debug"`：输出`debug`、`info`、`warn`、`error`级别日志
- 特殊级别：`ALL`（开启所有日志）、`OFF`（关闭所有日志）

## 五、核心知识点总结

### 1. 前后端分离

- 开发流程：需求→接口设计→并行开发→测试→联调
- 代理工具：Nginx 反向代理解决跨端口 / 跨域问题

### 2. Restful 风格

- 核心：URL 定资源，HTTP 动词定操作（GET/POST/PUT/DELETE）
- 规范：资源名用复数，路径简洁

### 3. SpringMVC 参数接收

| 参数类型  |        接收方式         |     注解      |              适用场景              |
| :-------: | :---------------------: | :-----------: | :--------------------------------: |
| 简单参数  | 直接形参 /@RequestParam | @RequestParam |     GET/DELETE 请求的 URL 参数     |
| JSON 参数 |        实体对象         | @RequestBody  |       POST/PUT 请求的请求体        |
| 路径参数  |          形参           | @PathVariable | URL 路径中的参数（如 /depts/{id}） |

### 4. MyBatis 数据封装

- 自动封装：属性名 = 字段名
- 手动解决：驼峰命名（推荐）、SQL 起别名、@Results 映射

### 5. Logback 日志

- 核心注解：@Slf4j（Lombok 简化）
- 日志级别：trace<debug<info<warn<error，生产环境常用 info/warn/error
- 配置文件：logback.xml 定义输出位置和格式