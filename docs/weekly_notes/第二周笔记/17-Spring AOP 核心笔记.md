# Spring AOP 核心笔记

## 一、AOP 基础认知

### 1. 概念

AOP：Aspect Oriented Programming（面向切面 / 方面编程），简单理解为**面向特定方法编程**，是一种编程思想，Spring 框架对其进行了具体实现（Spring AOP）。

### 2. 核心场景

对指定方法做共性功能增强，如：统计方法执行耗时、记录系统操作日志、事务管理、权限控制等。

### 3. 核心优势

1. 减少重复代码，将共性逻辑抽离统一实现
2. 代码无侵入，不修改原有业务代码
3. 提高开发效率，专注核心业务开发
4. 维护方便，共性逻辑修改只需改一处

### 4. 原始实现 vs AOP 实现对比

- **原始方式**：在每个业务方法中硬编码共性逻辑（如统计耗时），代码冗余、维护成本高
- **AOP 方式**：将共性逻辑抽离为独立的切面类，通过配置匹配目标方法，自动增强，无侵入

## 二、AOP 快速入门（统计业务方法执行耗时）

### 1. 开发步骤

#### 步骤 1：导入 AOP 依赖（pom.xml）

```xml
<!-- Spring AOP 起步依赖，父工程已管理版本 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

#### 步骤 2：编写 AOP 切面程序

```java
@Slf4j
@Aspect  // 标识为切面类
@Component  // 交给Spring容器管理
public class RecordTimeAspect {
    // 环绕通知 + 切入点表达式，匹配业务层所有方法
    @Around("execution(* com.itheima.service.impl.*.*(..))")
    public Object recordTime(ProceedingJoinPoint pjp) throws Throwable {
        // 1.记录方法开始时间
        long beginTime = System.currentTimeMillis();
        // 2.执行原始业务方法
        Object result = pjp.proceed();
        // 3.记录结束时间，计算耗时并打印
        long endTime = System.currentTimeMillis();
        log.info("执行耗时: {} ms", endTime - beginTime);
        // 4.返回原始方法的返回值
        return result;
    }
}
```

### 2. 核心思路

1. 获取方法运行的开始时间
2. 执行原始目标方法
3. 获取方法运行的结束时间，计算并输出执行耗时

## 三、AOP 核心概念

|   概念   |   英文    |                           核心说明                           |
| :------: | :-------: | :----------------------------------------------------------: |
|  连接点  | JoinPoint | 可以被 AOP 控制的方法（暗含方法执行时的类名、方法名、参数等信息） |
|   通知   |  Advice   |  抽离的**共性逻辑**（最终体现为一个方法，如统计耗时的方法）  |
|  切入点  | PointCut  |     匹配连接点的**条件**，通知仅在切入点方法执行时被应用     |
|   切面   |  Aspect   | 通知 + 切入点，描述二者的对应关系（哪个通知作用于哪些方法）  |
| 目标对象 |  Target   |         通知所应用的对象（原始业务方法所在的类对象）         |
| 代理对象 |   Proxy   | Spring AOP 通过动态代理生成，对目标对象方法增强，程序实际调用的是代理对象 |

### 核心关联

**切面类**中定义**通知方法**和**切入点表达式**，通过切入点匹配目标对象的**连接点**，最终实现对目标方法的增强。

## 四、AOP 进阶核心知识

### 1. 通知类型

根据通知方法执行时机分类，共 5 类，**@Around 为核心**，需重点掌握：

|      注解       |  通知类型  |                   执行时机                   |                           特殊说明                           |
| :-------------: | :--------: | :------------------------------------------: | :----------------------------------------------------------: |
|     @Around     |  环绕通知  |         目标方法**执行前 + 执行后**          | 需手动调用 `pjp.proceed()` 执行原始方法；返回值为 Object，接收原始方法返回值 |
|     @Before     |  前置通知  |              目标方法**执行前**              |                     无需处理原始方法执行                     |
|     @After      |  后置通知  | 目标方法**执行后**，**无论是否有异常**都执行 |                     无需处理原始方法执行                     |
| @AfterReturning | 返回后通知 |       目标方法**执行后且无异常**时执行       |                       可获取方法返回值                       |
| @AfterThrowing  | 异常后通知 |        目标方法**执行抛出异常**时执行        |                     可获取方法抛出的异常                     |

### 2. 切入点表达式

#### 作用

决定项目中**哪些方法**需要被通知增强。

#### 两种常见形式

##### 形式 1：execution (...) - 根据方法签名匹配（常用）

```
execution(访问修饰符?  返回值  包名.类名.?方法名(方法参数) throws 异常?)
```

- `?` 表示可省略的部分；
- 访问修饰符：如 public、protected，可省略；
- 包名。类名：可省略，支持通配符；
- throws 异常：指方法**声明抛出**的异常，非实际抛出，可省略。

###### 通配符

1. `*`：单个独立任意符号，通配任意返回值、包名、类名、方法名，或单个任意类型参数；
2. `..`：多个连续任意符号，通配**任意层级的包**，或**任意类型、任意个数的参数**。

###### 示例

```java
// 匹配com.itheima.service.impl包下所有类的所有方法
execution(* com.itheima.service.impl.*.*(..))
// 匹配com包下任意子包的service包下所有类的以update开头的方法，且参数为1个任意类型
execution(* com.*.service.*.update*(*))
```

###### 逻辑组合

支持**且（&&）、或（||）、非（!）** 组合复杂表达式：

```java
// 匹配增删改方法：save/delete/update
@Around("execution(* com.itheima.controller.*.save(..)) || " +
        "execution(* com.itheima.controller.*.delete(..)) || " +
        "execution(* com.itheima.controller.*.update(..))")
```

###### 书写建议

1. 业务方法命名规范（如 findXxx、updateXxx），方便快速匹配；
2. 基于**接口**描述切入点，而非实现类，增强拓展性；
3. 满足业务的前提下，尽量缩小匹配范围（如包名少用..）。

##### 形式 2：@annotation (...) - 根据注解匹配

```java
@annotation(注解的全类名)
```

###### 示例

1. 自定义注解`@LogOperation`；
2. 在需要增强的方法上添加该注解；
3. 切入点表达式匹配该注解：

```java
// 匹配所有标注了@LogOperation的方法
@Around("@annotation(com.itheima.anno.LogOperation)")
```

###### 适用场景

当`execution`表达式难以描述目标方法时，使用注解匹配更灵活。

#### 切点表达式抽取：@Pointcut

##### 作用

将公共的切入点表达式抽离，提高复用性，避免重复编写。

##### 用法

```java
@Aspect
@Component
public class RecordTimeAspect {
    // 1. 抽取切入点表达式，pt()为自定义方法名（无逻辑）
    @Pointcut("execution(* com.itheima.service.impl.*.*(..))")
    public void pt(){}

    // 2. 引用切入点表达式
    @Around("pt()")
    public Object recordTime(ProceedingJoinPoint pjp) throws Throwable {
        // 业务逻辑...
    }
}
```

##### 访问修饰符

- `private`：仅当前切面类可引用；
- `public`：外部切面类也可引用。

### 3. 通知执行顺序

#### 场景

多个切面类的切入点都匹配到目标方法时，多个通知会依次执行。

#### 默认顺序

按**切面类的类名字母排序**：

- 目标方法**前**的通知：字母排名靠前的**先执行**；
- 目标方法**后**的通知：字母排名靠前的**后执行**。

#### 手动指定顺序：@Order

在切面类上添加`@Order(数字)`，数字越小，优先级越高：

- 目标方法**前**的通知：数字小的**先执行**；
- 目标方法**后**的通知：数字小的**后执行**。

##### 示例

```java
@Order(1)  // 优先级更高，前置先执行，后置后执行
@Aspect
@Component
public class AspectA {
    // 通知方法...
}

@Order(2)
@Aspect
@Component
public class AspectB {
    // 通知方法...
}
```

### 4. 连接点信息获取

Spring 用`JoinPoint`抽象连接点，可获取方法执行时的**类名、方法名、参数**等信息。

#### 核心 API

|            方法            |          作用          |
| :------------------------: | :--------------------: |
|       `getTarget()`        |      获取目标对象      |
|      `getSignature()`      | 获取目标方法的签名信息 |
| `getSignature().getName()` |     获取目标方法名     |
|        `getArgs()`         | 获取目标方法的运行参数 |

#### 注意事项

- **@Around 通知**：只能使用`ProceedingJoinPoint`（JoinPoint 子类，包含`proceed()`方法）；
- **其他 4 种通知**：只能使用`JoinPoint`。

#### 示例

```java
// 环绕通知获取连接点信息
@Around("pt()")
public Object around(ProceedingJoinPoint pjp) throws Throwable {
    // 获取目标类名
    String className = pjp.getTarget().getClass().getName();
    // 获取目标方法名
    String methodName = pjp.getSignature().getName();
    // 获取方法参数
    Object[] args = pjp.getArgs();
    log.info("类名：{}，方法名：{}，参数：{}", className, methodName, args);
    // 执行原始方法
    Object result = pjp.proceed();
    return result;
}

// 前置通知获取连接点信息
@Before("pt()")
public void before(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    log.info("前置通知：方法{}即将执行", methodName);
}
```

## 五、AOP 实战案例：记录系统操作日志

### 1. 需求

记录系统**增、删、改**接口的操作日志，日志信息包含：

操作人、操作时间、执行方法全类名、执行方法名、方法运行参数、返回值、方法执行时长。

### 2. 核心技术选型

1. 通知类型：**@Around**（需在方法执行前后获取时间、参数、返回值）；
2. 切入点表达式：可选择`execution`（匹配 save/delete/update 方法）或`@annotation`（自定义注解标注目标方法）；
3. 操作人获取：通过**ThreadLocal**从请求线程中获取当前登录员工 ID。

### 3. 关键技术：ThreadLocal

#### 概念

ThreadLocal 是**线程的局部变量**，为每个线程提供**单独的存储空间**，具有**线程隔离**效果，不同线程之间不会相互干扰。

#### 核心作用

在**同一个线程 / 同一个请求**中实现**数据共享**（如从过滤器传递登录信息到 AOP、Controller、Service）。

#### 常用方法

|      方法      |                    作用                    |
| :------------: | :----------------------------------------: |
| `set(T value)` |        设置当前线程的线程局部变量值        |
|    `get()`     |      返回当前线程对应的线程局部变量值      |
|   `remove()`   | 移除当前线程的线程局部变量（防止内存泄漏） |

#### 操作步骤（获取当前登录人）

1. 定义 ThreadLocal 工具类

   ：封装对登录员工 ID 的操作

   ```java
   public class ThreadLocalUtil {
       // 定义ThreadLocal对象，存储Long类型的员工ID
       private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();
       
       // 设置值
       public static void set(Long id) {
           THREAD_LOCAL.set(id);
       }
       
       // 获取值
       public static Long get() {
           return THREAD_LOCAL.get();
       }
       
       // 移除值
       public static void remove() {
           THREAD_LOCAL.remove();
       }
   }
   ```

   

2. 过滤器中存入员工 ID

   ：在 TokenFilter 中解析 JWT 令牌，获取登录员工 ID 并存入 ThreadLocal，请求结束后移除

   ```java
   public class TokenFilter implements Filter {
       @Override
       public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
           // 1. 解析JWT，获取员工ID
           Long empId = JwtUtil.parseToken(getToken(request));
           if (empId != null) {
               // 2. 存入ThreadLocal
               ThreadLocalUtil.set(empId);
           }
           try {
               // 3. 放行请求
               chain.doFilter(request, response);
           } finally {
               // 4. 请求结束，移除值（防止内存泄漏）
               ThreadLocalUtil.remove();
           }
       }
   }
   ```

   

3. AOP 中获取员工 ID

   ：从 ThreadLocal 中取出操作人 ID，用于日志记录

   ```java
   @Around("pt()")
   public Object recordLog(ProceedingJoinPoint pjp) throws Throwable {
       // 获取当前操作人ID
       Long operatorId = ThreadLocalUtil.get();
       // 后续日志记录逻辑...
   }
   ```

   

### 4. 核心实现思路

1. 通过`ProceedingJoinPoint`获取方法全类名、方法名、运行参数；
2. 记录方法开始时间，执行`pjp.proceed()`获取返回值；
3. 记录方法结束时间，计算执行时长；
4. 从 ThreadLocal 中获取操作人 ID，获取当前系统时间；
5. 将所有日志信息封装为实体类，调用 Mapper 插入数据库。

## 六、Spring AOP 核心小结

### 1. 开发流程

1. 导入`spring-boot-starter-aop`依赖；
2. 编写切面类，添加`@Aspect + @Component`注解；
3. 定义通知方法，添加对应通知注解（如 @Around）；
4. 编写切入点表达式，匹配目标方法（execution/@annotation）；
5. 在通知方法中实现共性逻辑，按需获取连接点信息。

### 2. 核心重点

1. **环绕通知**：需手动执行`pjp.proceed()`，返回原始方法返回值；
2. **切入点表达式**：execution 按方法签名匹配，@annotation 按注解匹配，可通过 @Pointcut 抽离；
3. **通知顺序**：默认按类名字母排序，可通过 @Order 手动指定；
4. **连接点**：@Around 用 ProceedingJoinPoint，其他通知用 JoinPoint；
5. **ThreadLocal**：线程隔离，用于同一请求中数据共享，使用后需 remove 防止内存泄漏。

### 3. 典型应用场景

统计方法执行耗时、记录操作日志、全局事务管理、接口权限控制、全局异常处理。