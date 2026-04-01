# SpringBoot 原理核心笔记

## 一、配置优先级

### 1. 支持的配置方式

SpringBoot 支持**配置文件**、**Java 系统属性**、**命令行参数**三种配置方式，其中配置文件包含 3 种格式，**yml 是主流**，项目开发建议统一使用一种格式。

|   配置类型    |       示例配置        |      配置文件名称      |
| :-----------: | :-------------------: | :--------------------: |
|   配置文件    |  `server.port=8081`   | application.properties |
|   配置文件    | `server: port: 8082`  |    application.yml     |
|   配置文件    | `server: port: 8083`  |    application.yaml    |
| Java 系统属性 | `-Dserver.port=9000`  |       运行时指定       |
|  命令行参数   | `--server.port=10010` |       运行时指定       |

### 2. 配置优先级（低 → 高）

**application.yaml** < **application.yml** < **application.properties** < **Java 系统属性（-Dxxx=xxx）** < **命令行参数（--xxx=xxx）**

> 优先级高的配置会覆盖优先级低的配置

### 3. 命令行运行 Jar 包配置

```bash
# 打包（需引入spring-boot-maven-plugin插件，骨架创建项目自动添加）
mvn package
# 运行Jar包，同时指定Java系统属性和命令行参数
java -Dserver.port=9000 -jar 项目包名.jar --server.port=10010
```

## 二、Bean 管理

### 1. Bean 的作用域

Spring 支持 5 种作用域，**后 3 种仅在 Web 环境生效**，默认作用域为`singleton`。

|   作用域    |                           核心说明                           |
| :---------: | :----------------------------------------------------------: |
|  singleton  |    容器内**同名称**的 Bean 仅有一个实例（单例），**默认**    |
|  prototype  |          每次使用 Bean 时都会创建**新实例**（多例）          |
|   request   |    每个 HTTP 请求创建一个新实例，请求结束销毁（Web 环境）    |
|   session   |    每个 HTTP 会话创建一个新实例，会话结束销毁（Web 环境）    |
| application | 每个 Web 应用创建一个新实例，应用启动到销毁仅一个（Web 环境） |

#### 1.1 作用域使用方式

通过`@Scope`注解指定，加在类上（Bean 所在类）：

```java
@Scope("prototype") // 指定为多例
@RestController
@RequestMapping("/depts")
public class DeptController {
}
```

#### 1.2 关键注意事项

1. 单例 Bean（singleton）在**容器启动时**创建，可通过`@Lazy`注解**延迟初始化**（第一次使用时创建）；
2. 实际开发中**绝大部分 Bean 为单例**，无需手动配置`@Scope`；
3. 多例 Bean（prototype）每次注入 / 获取都会创建新对象，注意性能损耗。

### 2. 第三方 Bean 的管理

#### 2.1 声明 Bean 的注解选择

|        注解类型         |           适用场景           |            示例            |
| :---------------------: | :--------------------------: | :------------------------: |
| @Component 及其衍生注解 | 项目**自定义类**的 Bean 声明 |  @Controller、@Service 等  |
|       @Bean 注解        | **第三方依赖类**的 Bean 声明 | 阿里云 OSS 工具类、Gson 等 |

#### 2.2 @Bean 注解的使用

##### 核心要求

第三方 Bean 建议**集中配置**，在 **@Configuration 注解标注的配置类 ** 中声明（而非启动类），解耦配置与业务。

##### 基础用法

```java
// 配置类（推荐）
@Configuration
public class OSSConfig {
    // 将方法返回值交给IOC容器管理，成为第三方Bean
    @Bean // 不指定名称时，Bean名称默认为方法名aliyunOSSOperator
    public AliyunOSSOperator aliyunOSSOperator(AliyunOSSProperties ossProperties) {
        return new AliyunOSSOperator(ossProperties);
    }
}
```

##### 自定义 Bean 名称

通过`@Bean`的`name`/`value`属性指定：

```java
@Bean(name = "ossOperator") // 自定义Bean名称为ossOperator
public AliyunOSSOperator aliyunOSSOperator(AliyunOSSProperties ossProperties) {
    return new AliyunOSSOperator(ossProperties);
}
```

##### Bean 的依赖注入

第三方 Bean 需要依赖其他 Bean 时，**直接在方法形参中声明**，IOC 容器会根据**类型自动装配**：

```java
// 形参ossProperties为自定义Bean，容器自动注入
@Bean
public AliyunOSSOperator aliyunOSSOperator(AliyunOSSProperties ossProperties) {
    return new AliyunOSSOperator(ossProperties);
}
```

## 三、SpringBoot 核心原理

SpringBoot 的核心优势是**简化 Spring 开发**，解决了原生 Spring 依赖繁琐、配置复杂的问题，核心实现为**起步依赖**和**自动配置**两大特性。

### 1. 起步依赖

#### 1.1 核心概念

起步依赖是 SpringBoot 提供的**依赖包组合**，将开发某一功能所需的所有依赖统一封装，只需引入一个起步依赖，即可自动引入相关所有依赖，无需手动逐个添加。

#### 1.2 常见起步依赖

```xml
<!-- Web开发起步依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- AOP起步依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<!-- MyBatis整合起步依赖 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
</dependency>
```

#### 1.3 实现原理

基于**Maven 的依赖传递**特性：起步依赖内部已经依赖了该功能所需的所有底层依赖，引入起步依赖后，Maven 会自动传递引入所有相关依赖，无需手动配置。

> 例如：`spring-boot-starter-web`会自动引入`spring-web`、`spring-webmvc`、`tomcat-embed-core`等 Web 开发相关依赖。

### 2. 自动配置

#### 2.1 核心概念

SpringBoot 启动后，会**自动将指定的配置类、Bean 对象注入到 IOC 容器**，无需开发者手动声明 / 配置，从而省去繁琐的 XML 配置和注解声明，实现**零配置开发**。

#### 2.2 第三方 Bean 自动配置的实现方案

第三方依赖中的 Bean 无法被默认的`@ComponentScan`扫描到（包名不匹配），有两种实现方案，**@Import 注解方案为推荐方案**。

##### 方案一：@ComponentScan 扫描指定包

手动指定第三方依赖的包名，让组件扫描注解扫描到第三方 Bean：

```java
// 扫描自定义包com.itheima + 第三方包com.example
@ComponentScan({"com.itheima","com.example"})
@SpringBootApplication
public class SpringbootWebConfigApplication {
}
```

**缺点**：需要手动指定所有第三方包名，使用繁琐、性能低，不推荐。

##### 方案二：@Import 注解导入（推荐）

`@Import`注解可直接将类导入到 IOC 容器，**无需被 @ComponentScan 扫描**，是第三方 Bean 自动配置的核心方案，支持 4 种导入形式：

1. 导入普通类

   ：直接导入第三方普通类，成为 IOC 容器 Bean

   ```java
   @Import(TokenParser.class) // 导入第三方普通类
   @SpringBootApplication
   public class SpringbootWebConfigApplication {
   }
   ```

   

2. 导入配置类

   ：导入第三方的 @Configuration 配置类，批量导入 Bean

   ```java
   @Import(HeaderConfig.class) // 导入第三方配置类
   @SpringBootApplication
   public class SpringbootWebConfigApplication {
   }
   ```

   

3. **导入 ImportSelector 接口实现类**：动态批量导入类，适用于大量 Bean 导入

4. @EnableXxxx 注解封装

   ：自定义注解封装 @Import，优雅实现开启 / 关闭自动配置（SpringBoot 官方主流方式）

   ```java
   // 自定义@EnableHeaderConfig注解，封装@Import
   @Import(HeaderConfig.class)
   public @interface EnableHeaderConfig {
   }
   // 启动类添加注解，开启自动配置
   @EnableHeaderConfig
   @SpringBootApplication
   public class SpringbootWebConfigApplication {
   }
   ```

   

#### 2.3 自动配置的源码跟踪

SpringBoot 自动配置的核心入口是 **@SpringBootApplication**注解，该注解是一个**组合注解 **，包含三个核心注解：

```java
@SpringBootConfiguration // 等同于@Configuration，声明当前是配置类
@EnableAutoConfiguration // 自动配置核心注解
@ComponentScan // 组件扫描，默认扫描启动类所在包及其子包
public @interface SpringBootApplication {
}
```

##### 核心步骤 1：@EnableAutoConfiguration 注解

该注解是自动配置的核心，内部通过`@Import`导入了**AutoConfigurationImportSelector**类：

```java
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class) // 导入自动配置选择器
public @interface EnableAutoConfiguration {
}
```

##### 核心步骤 2：加载自动配置类列表

`AutoConfigurationImportSelector`会加载**META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports**文件，该文件中定义了 SpringBoot 所有的**自动配置类（XxxAutoConfiguration）**。

> 低版本 SpringBoot（2.7.0 以前）的自动配置类列表定义在`META-INF/spring.factories`文件中。

##### 核心步骤 3：@Conditional 条件装配

**并非所有自动配置类都会被注入 IOC 容器**，SpringBoot 通过 **@Conditional 及其衍生注解**做**条件判断 **，**满足条件后才会注册对应的 Bean**，避免无用 Bean 的创建。

#### 2.4 @Conditional 条件注解

##### 核心作用

按指定条件判断，满足条件后才将 Bean 注册到 IOC 容器，是自动配置的**核心过滤机制**。

##### 注解适用位置

- **方法上**：仅对当前方法声明的 Bean 生效；
- **类上**：对当前配置类中**所有方法声明的 Bean**生效。

##### 常见衍生注解（核心）

|             注解             |                  条件判断规则                  |
| :--------------------------: | :--------------------------------------------: |
|     @ConditionalOnClass      |   环境中存在指定类的字节码文件，才注册 Bean    |
|  @ConditionalOnMissingBean   | 环境中**不存在**指定类型 / 名称的 Bean，才注册 |
|    @ConditionalOnProperty    |  配置文件中存在指定属性且值匹配，才注册 Bean   |
| @ConditionalOnWebApplication |          当前是 Web 环境，才注册 Bean          |

##### 示例

```java
// 仅当容器中不存在Gson类型的Bean时，才创建并注册该Gson Bean
@Bean
@ConditionalOnMissingBean
public Gson gson(GsonBuilder gsonBuilder) {
    return gsonBuilder.create();
}
```

#### 2.5 自动配置的核心原理

1. 启动类添加`@SpringBootApplication`，触发`@EnableAutoConfiguration`；
2. `@EnableAutoConfiguration`通过`AutoConfigurationImportSelector`加载**自动配置类列表**；
3. 自动配置类通过 **@Conditional 系列注解 ** 做条件判断；
4. **满足条件的自动配置类**被注入 IOC 容器，完成 Bean 的自动注册；
5. 开发者可直接注入使用容器中的 Bean，无需手动配置。

### 3. 自定义 Starter

#### 3.1 核心概念

Starter 是 SpringBoot 的**自定义起步依赖**，封装了**特定功能的依赖管理**和**自动配置**，引入后即可直接使用该功能，无需手动配置依赖和 Bean。

> SpringBoot 官方 Starter 命名规范：`spring-boot-starter-xxx`；第三方 Starter 命名规范：`xxx-spring-boot-starter`。

#### 3.2 自定义 Starter 的核心功能

1. **依赖管理**：封装功能所需的所有依赖，实现一键引入；
2. **自动配置**：实现功能相关 Bean 的自动注册，开发者可直接注入使用。

#### 3.3 自定义 Starter 的开发步骤（以阿里云 OSS 为例）

**需求**：自定义`aliyun-oss-spring-boot-starter`，实现阿里云 OSS 工具类`AliyunOSSOperator`的自动配置，引入后可直接注入使用。

##### 步骤 1：创建两个 Maven 模块

- **aliyun-oss-spring-boot-starter**：**起步依赖模块**，仅做依赖管理，无业务代码，打包方式为`pom`；
- **aliyun-oss-spring-boot-autoconfigure**：**自动配置模块**，实现 Bean 的自动配置逻辑，是 Starter 的核心。

##### 步骤 2：在 Starter 模块中引入自动配置模块

```xml
<!-- aliyun-oss-spring-boot-starter的pom.xml -->
<dependencies>
    <!-- 引入自动配置模块 -->
    <dependency>
        <groupId>com.itheima</groupId>
        <artifactId>aliyun-oss-spring-boot-autoconfigure</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <!-- 引入阿里云OSS的核心依赖 -->
    <dependency>
        <groupId>com.aliyun.oss</groupId>
        <artifactId>aliyun-sdk-oss</artifactId>
        <version>3.17.4</version>
    </dependency>
</dependencies>
```

##### 步骤 3：在自动配置模块中实现自动配置逻辑

1. 编写配置类，声明第三方 Bean（`AliyunOSSOperator`）；
2. 添加`@Conditional`系列注解，做条件装配；
3. 编写**自动配置类列表文件**：`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`，将自定义配置类写入文件。

##### 步骤 4：使用自定义 Starter

在业务项目中引入自定义 Starter 依赖，即可直接注入`AliyunOSSOperator`使用，无需手动配置依赖和 Bean：

```xml
<dependency>
    <groupId>com.itheima</groupId>
    <artifactId>aliyun-oss-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 四、核心总结

### 1. 配置相关

- SpringBoot 配置优先级：yaml < yml < properties < Java 系统属性 < 命令行参数；
- 命令行运行 Jar 包：`java -Dxxx=xxx -jar 包名.jar --xxx=xxx`。

### 2. Bean 管理相关

- 绝大部分 Bean 为单例（singleton），无需手动配置`@Scope`；
- 自定义类用`@Component`系列注解，第三方类用`@Bean`注解（配置类中声明）；
- `@Bean`的依赖注入直接通过方法形参实现，容器自动装配。

### 3. SpringBoot 核心原理

- **起步依赖**：基于 Maven 依赖传递，封装功能所需依赖，一键引入；
- **自动配置**：核心为`@EnableAutoConfiguration`，通过加载自动配置类 +`@Conditional`条件装配，实现 Bean 的自动注册；
- **@Import 注解**：第三方 Bean 自动配置的核心，支持 4 种导入形式，推荐封装为`@EnableXxxx`注解使用。

### 4. 自定义 Starter

- 核心是**依赖管理**+**自动配置**，分为 starter 模块和 autoconfigure 模块；
- 命名规范：第三方 Starter 为`xxx-spring-boot-starter`；
- 核心步骤：创建模块 → 依赖管理 → 自动配置逻辑 → 写入自动配置类列表文件。