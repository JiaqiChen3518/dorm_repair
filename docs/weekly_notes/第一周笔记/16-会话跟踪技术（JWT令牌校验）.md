# Web 后端实战 - 登录认证笔记

## 一、登录功能实现

### 1. 核心需求

用户输入用户名和密码，系统校验合法性：

- 校验通过：生成令牌（JWT），返回用户信息 + 令牌
- 校验失败：返回错误提示（用户名或密码错误）
- 本质：根据用户名和密码查询员工信息，判定是否匹配

### 2. 接口规范

|    项    |                             内容                             |
| :------: | :----------------------------------------------------------: |
| 请求路径 |                           `/login`                           |
| 请求方式 |                             POST                             |
| 请求格式 |                             JSON                             |
| 请求参数 |     `username`（用户名，必填）、`password`（密码，必填）     |
| 响应格式 |                             JSON                             |
| 响应数据 | `code`（1 成功 / 0 失败）、`msg`（提示）、`data`（用户信息 + token） |

### 3. 实现步骤

#### （1）实体类与 VO

```java
// 登录请求参数封装
@Data
public class LoginRequest {
    private String username;
    private String password;
}

// 登录响应数据封装（VO）
@Data
public class LoginVO {
    private Integer id;
    private String username;
    private String name;
    private String token; // JWT令牌
}
```

#### （2）Mapper 层：查询员工信息

```java
@Mapper
public interface EmpMapper {
    // 根据用户名和密码查询员工
    @Select("select id, username, name from emp where username = #{username} and password = #{password}")
    Emp getByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
}
```

#### （3）Service 层：校验 + 生成 JWT 令牌

```java
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private JwtUtil jwtUtil; // JWT工具类

    @Override
    public LoginVO login(LoginRequest request) {
        // 1. 校验用户名和密码
        Emp emp = empMapper.getByUsernameAndPassword(request.getUsername(), request.getPassword());
        if (emp == null) {
            throw new BusinessException("用户名或密码错误");
        }
        // 2. 生成JWT令牌（包含用户ID、用户名）
        String token = jwtUtil.generateToken(emp.getId(), emp.getUsername());
        // 3. 封装响应数据
        LoginVO loginVO = new LoginVO();
        loginVO.setId(emp.getId());
        loginVO.setUsername(emp.getUsername());
        loginVO.setName(emp.getName());
        loginVO.setToken(token);
        return loginVO;
    }
}
```

#### （4）Controller 层：接收请求 + 响应结果

```java
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        LoginVO loginVO = loginService.login(request);
        return Result.success(loginVO);
    }
}
```

## 二、登录校验核心技术

### 1. 会话跟踪技术对比

登录校验的核心是**识别后续请求是否来自已登录用户**，即会话跟踪，三种主流方案：

|    方案     |                      原理                      |                   优点                   |                    缺点                     |           适用场景           |
| :---------: | :--------------------------------------------: | :--------------------------------------: | :-----------------------------------------: | :--------------------------: |
|   Cookie    |  响应头`Set-Cookie`存标记，请求头`Cookie`携带  |     HTTP 协议原生支持，无需额外开发      |       移动端不支持、可禁用、不能跨域        |       纯 PC 端简单系统       |
|   Session   | 基于 Cookie 存储`JSESSIONID`，服务端存会话数据 |            存储在服务端，安全            | 集群环境不兼容、依赖 Cookie、占用服务端内存 |        单体 PC 端系统        |
| 令牌（JWT） |      登录成功生成令牌，后续请求头携带令牌      | 支持 PC / 移动端、跨域友好、无服务端存储 |         需要手动实现生成 / 校验逻辑         | 前后端分离、集群系统（推荐） |

### 2. JWT 令牌技术

#### （1）JWT 核心概念

- 全称：JSON Web Token，自包含的安全传输格式

- 组成（三部分用`.`分隔）：

  1. Header（头）：存储令牌类型（JWT）和签名算法（如HS256）

     ```json
     {"alg":"HS256","type":"JWT"}
     ```

  2. Payload（载荷）

     ：存储自定义信息（如用户 ID、用户名）和默认字段（如过期时间）

     ```json
     {"id":1,"username":"songjiang","exp":1716835200}
     ```

     

  3. **Signature（签名）**：Header+Payload + 秘钥通过签名算法生成，防止令牌篡改

  

- 特点：Base64 编码（可解码），签名后不可篡改，无需服务端存储

#### （2）JWT 使用步骤

##### 1. 引入依赖

```xml
<!-- JJWT：JWT生成/解析工具 -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

##### 2. 编写 JWT 工具类

```java
@Component
public class JwtUtil {
    // 签名秘钥（必须保密，生产环境建议配置在配置文件）
    private static final String SECRET_KEY = "SVRIRUlNQQ==";
    // 令牌过期时间（12小时）
    private static final long EXPIRATION = 12 * 3600 * 1000;

    /**
     * 生成JWT令牌
     */
    public String generateToken(Integer userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims) // 载荷信息
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 签名算法+秘钥
                .compact();
    }

    /**
     * 解析JWT令牌，获取载荷信息
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY) // 必须与生成时的秘钥一致
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 令牌篡改、过期等异常直接抛出
            throw new BusinessException("令牌无效或已过期");
        }
    }
}
```

##### 3. 关键注意事项

- 秘钥必须保密，避免硬编码（建议配置在`application.yml`）
- 令牌过期时间按需设置（太短频繁登录，太长有安全风险）
- 解析失败场景：令牌篡改、过期、秘钥不匹配

## 三、登录校验实现（过滤器 Filter）

### 1. Filter 核心概念

- 定义：JavaWeb 三大组件之一，用于拦截资源请求，实现通用功能（登录校验、统一编码等）

- 作用：拦截所有请求（包括静态资源），在请求到达 Controller 前执行校验

- 核心方法：

  - `init()`：服务器启动时初始化，仅执行一次
  - `doFilter()`：拦截请求时执行（核心），需调用`chain.doFilter()`放行
  - `destroy()`：服务器关闭时销毁，仅执行一次

  

### 2. 登录校验 Filter 实现步骤

#### （1）定义 Filter 类

```java
@WebFilter(urlPatterns = "/*") // 拦截所有请求
@Slf4j
public class LoginFilter implements Filter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        // 1. 转换请求/响应对象（ServletRequest→HttpServletRequest）
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 2. 排除登录请求（无需校验）
        String url = request.getRequestURI();
        if (url.contains("/login")) {
            chain.doFilter(request, response); // 放行登录请求
            return;
        }

        // 3. 获取请求头中的令牌（前端需在Header中携带token）
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.error("未登录，请先登录")));
            return;
        }

        // 4. 解析令牌
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.error("令牌无效或已过期")));
            return;
        }

        // 5. 令牌有效，放行（可将用户信息存入请求域，供后续使用）
        request.setAttribute("userId", claims.get("id"));
        request.setAttribute("username", claims.get("username"));
        chain.doFilter(request, response);
    }
}
```

#### （2）开启 Servlet 组件扫描

在 SpringBoot 引导类上添加`@ServletComponentScan`，使 Filter 生效：

```java
@SpringBootApplication
@ServletComponentScan // 扫描Servlet组件（Filter、Servlet、Listener）
public class TliasApplication {
    public static void main(String[] args) {
        SpringApplication.run(TliasApplication.class, args);
    }
}
```

### 3. Filter 关键细节

#### （1）拦截路径配置

|    拦截路径    |    语法    |                             含义                             |
| :------------: | :--------: | :----------------------------------------------------------: |
|  拦截所有资源  |    `/*`    |             匹配所有请求（如`/login`、`/depts`）             |
| 拦截目录下资源 | `/depts/*` | 匹配`/depts`下的一级资源（如`/depts/1`，不匹配`/depts/1/2`） |
|  拦截具体资源  |  `/login`  |                      仅匹配`/login`请求                      |

#### （2）执行流程

```
浏览器请求 → Filter拦截 → 放行前逻辑 → chain.doFilter()放行 → 访问资源 → 放行后逻辑 → 响应浏览器
```

- 必须调用`chain.doFilter()`才会放行，否则请求会被拦截终止
- 放行后资源执行完毕，会回到 Filter 的放行后逻辑

#### （3）过滤器链

- 多个 Filter 按类名字典序执行（如`LoginFilter`先于`LogFilter`）
- 执行顺序：Filter1 放行前 → Filter2 放行前 → 资源 → Filter2 放行后 → Filter1 放行后

## 四、登录校验实现（拦截器 Interceptor）

### 1. Interceptor 核心概念

- 定义：Spring 框架提供的拦截机制，仅拦截 SpringMVC 的 Controller 请求（不拦截静态资源）

- 作用：与 Filter 类似，用于登录校验、权限控制等，更贴合 Spring 生态

- 核心方法：

  - `preHandle()`：Controller 方法执行前执行（核心，返回`true`放行，`false`拦截）
  - `postHandle()`：Controller 方法执行后、视图渲染前执行
  - `afterCompletion()`：视图渲染后执行（最终执行）

  

### 2. 登录校验 Interceptor 实现步骤

#### （1）定义 Interceptor 类

```java
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 排除登录请求
        String url = request.getRequestURI();
        if (url.contains("/login")) {
            return true; // 放行
        }

        // 2. 获取请求头令牌
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.error("未登录，请先登录")));
            return false; // 拦截
        }

        // 3. 解析令牌
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.error("令牌无效或已过期")));
            return false; // 拦截
        }

        // 4. 存入请求域，供Controller使用
        request.setAttribute("userId", claims.get("id"));
        request.setAttribute("username", claims.get("username"));
        return true; // 放行
    }
}
```

#### （2）注册 Interceptor

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns("/login"); // 排除登录请求
    }
}
```

### 3. Interceptor 关键细节

#### （1）拦截路径配置

|    拦截路径    |    语法     |                           含义                           |
| :------------: | :---------: | :------------------------------------------------------: |
|   任意级路径   |    `/**`    | 匹配所有层级请求（如`/depts`、`/depts/1`、`/depts/1/2`） |
|    一级路径    |    `/*`     |      仅匹配一级请求（如`/depts`，不匹配`/depts/1`）      |
| 目录下任意路径 | `/depts/**` |                匹配`/depts`下所有层级请求                |

#### （2）Filter 与 Interceptor 区别

| 对比维度 |               Filter（过滤器）                |                    Interceptor（拦截器）                     |
| :------: | :-------------------------------------------: | :----------------------------------------------------------: |
| 接口规范 |        实现`javax.servlet.Filter`接口         | 实现`org.springframework.web.servlet.HandlerInterceptor`接口 |
| 拦截范围 | 拦截所有请求（静态资源、Servlet、Controller） |             仅拦截 SpringMVC 的 Controller 请求              |
| 执行时机 |       请求进入 Tomcat 后，Controller 前       |             DispatcherServlet 后，Controller 前              |
| 依赖环境 |            无（JavaWeb 标准组件）             |                       依赖 Spring 环境                       |
| 核心方法 |           `doFilter()`（单一方法）            |  `preHandle()`/`postHandle()`/`afterCompletion()`（分阶段）  |

## 五、核心知识点总结

### 1. 登录功能

- 核心逻辑：用户名 + 密码查询校验，通过则生成 JWT 令牌
- 响应数据：必须返回令牌，供后续请求携带

### 2. JWT 令牌

- 组成：Header（算法 + 类型）、Payload（用户信息）、Signature（签名）
- 核心：秘钥保密、设置过期时间、解析异常处理
- 工具类：`Jwts.builder()`生成，`Jwts.parser()`解析

### 3. 登录校验

- 两种方案：Filter（拦截所有请求）、Interceptor（仅拦截 Controller）
- 校验流程：排除登录请求 → 获取令牌 → 校验令牌 → 放行 / 拦截
- 前端配合：登录后所有请求在 Header 中携带`token`字段

### 4. 关键注解

|          注解           |                  作用                   |
| :---------------------: | :-------------------------------------: |
|      `@WebFilter`       |      标识 Filter 类，配置拦截路径       |
| `@ServletComponentScan` | 扫描 Servlet 组件（Filter、Servlet 等） |
|    `@Configuration`     |               标识配置类                |
|      `@Component`       |   标识 Spring 组件（Interceptor 等）    |
|       `@Override`       |   重写接口方法（Filter/Interceptor）    |

### 5. 常见问题

- 令牌失效：检查过期时间、秘钥是否一致、令牌是否被篡改
- 拦截失效：Filter 未加`@ServletComponentScan`，Interceptor 未注册
- 跨域问题：JWT 令牌在请求头携带，无 Cookie 跨域限制，适配前后端分离