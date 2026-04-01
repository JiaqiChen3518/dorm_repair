# Maven 高级核心笔记

## 一、分模块设计与开发

### 1. 核心概念

将一个大型 Maven 项目拆分成若干个**独立的子模块**，每个模块负责单一的功能 / 层级，降低项目复杂度。

### 2. 分模块的原因

- 解决单模块项目**不便维护、难以复用**的问题
- 方便模块间相互引用、资源共享
- 便于项目扩展和团队协作开发

### 3. 分模块设计策略

|        策略         |                      说明                      |                   示例                    |
| :-----------------: | :--------------------------------------------: | :---------------------------------------: |
| 按**功能模块**拆分  |     按业务功能划分，每个模块对应一个业务域     |    mall-common、mall-goods、mall-order    |
|    按**层**拆分     | 按项目架构层级划分，分离实体、控制层、业务层等 | mall-pojo、mall-controller、mall-service  |
| 按**功能 + 层**拆分 |   结合前两种策略，细粒度拆分，适用于大型项目   | mall-goods-controller、mall-order-service |

### 4. 分模块开发步骤（实战）

以拆分`tlias-web-management`项目为例：

1. 按需创建独立 Maven 模块（如`tlias-pojo`存实体类、`tlias-utils`存工具类）
2. 将原项目中对应代码迁移至新模块
3. 模块间通过**Maven 依赖**实现引用
4. 保留主业务模块`tlias-web-management`作为核心运行模块

### 5. 关键注意事项

**先设计，后编码**：分模块需在项目开发初期规划，而非开发完成后再拆分，避免代码冗余和依赖混乱。

### 6. 核心小结

- 分模块本质：拆分复杂项目，解耦功能 / 层级
- 核心优势：易维护、易复用、易扩展
- 开发原则：单一职责，模块间低耦合、高内聚

## 二、继承与聚合

### 1. Maven 继承

#### 1.1 核心概念

与 Java 继承类似，**子工程**可继承**父工程**的配置信息（核心为依赖配置），是**纵向**的模块关系。

#### 1.2 继承的作用

- 简化子工程的依赖配置，避免重复编写
- 统一管理项目依赖版本，降低版本冲突风险

#### 1.3 继承的实现步骤

##### 步骤 1：创建父工程（打包方式为`pom`）

父工程仅做依赖管理，不编写业务代码，`pom.xml`核心配置：

```xml
<!-- 父工程必须设置打包方式为pom -->
<packaging>pom</packaging>
<groupId>com.itheima</groupId>
<artifactId>tlias-parent</artifactId>
<version>1.0-SNAPSHOT</version>

<!-- 父工程继承SpringBoot官方父工程（可选，SpringBoot项目必备） -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.10</version>
    <relativePath/>
</parent>

<!-- 配置子工程公共依赖，子工程可直接继承 -->
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

**Maven 打包方式说明**：

- `jar`：普通模块 / SpringBoot 项目打包（内嵌 Tomcat，可直接运行）
- `war`：传统 Web 项目打包，需部署到外部 Tomcat
- `pom`：父工程 / 聚合工程专用，仅做配置管理

##### 步骤 2：子工程配置继承关系

子工程`pom.xml`中通过`<parent>`标签指定父工程，核心配置：

```xml
<!-- 继承自定义父工程 -->
<parent>
    <groupId>com.itheima</groupId>
    <artifactId>tlias-parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <!-- 指定父工程pom.xml的相对路径，本地开发必备 -->
    <relativePath>../tlias-parent/pom.xml</relativePath>
</parent>

<!-- 子工程可省略groupId，自动继承父工程 -->
<artifactId>tlias-pojo</artifactId>
<version>1.0-SNAPSHOT</version>
```

#### 1.4 继承的核心细节

- 子工程可**省略 groupId**，自动继承父工程的`groupId`和`version`
- `relativePath`：指定父工程 pom 文件位置，本地开发需配置，远程仓库可省略
- **版本优先级**：父子工程配置同一依赖不同版本时，**子工程版本为准**（就近原则）

#### 1.5 版本锁定：`dependencyManagement`

##### 核心问题

直接在父工程`<dependencies>`中配置的依赖，子工程会**强制继承**，若子工程无需该依赖会造成冗余。

##### 解决方案：`<dependencyManagement>`

**统一管理依赖版本，不强制子工程继承**，子工程需手动引入依赖（无需指定版本）。

###### 父工程配置（版本锁定）

```xml
<!-- 自定义属性，统一管理版本号，便于维护 -->
<properties>
    <jjwt.version>0.9.1</jjwt.version>
    <lombok.version>1.18.30</lombok.version>
</properties>

<!-- 版本锁定，仅管理版本，不引入依赖 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

###### 子工程配置（引入依赖，无需版本）

```xml
<dependencies>
    <!-- 直接引入，版本由父工程dependencyManagement管理 -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
    </dependency>
</dependencies>
```

##### `<dependencies>` vs `<dependencyManagement>

|           标签           |         核心作用         | 子工程是否需要手动引入 |
| :----------------------: | :----------------------: | :--------------------: |
|     `<dependencies>`     | 直接依赖，子工程自动继承 |           否           |
| `<dependencyManagement>` |  仅锁定版本，不自动依赖  |   是（无需指定版本）   |

### 2. Maven 聚合

#### 2.1 核心概念

将多个独立的子模块**组织成一个整体**，是**横向**的模块关系，聚合工程为所有子模块的统一构建入口。

#### 2.2 聚合的作用

- 快速构建整个项目，**无需按依赖关系手动逐个构建**
- 聚合工程会根据模块间的依赖关系，**自动确定构建顺序**

#### 2.3 聚合的实现步骤

聚合工程与父工程通常**合二为一**（同一`pom.xml`），在父工程中通过`<modules>`标签配置聚合的子模块：

```xml
<!-- 聚合配置：指定当前工程包含的子模块 -->
<modules>
    <!-- 子模块的相对路径 -->
    <module>../tlias-pojo</module>
    <module>../tlias-utils</module>
    <module>../tlias-web-management</module>
</modules>
```

#### 2.4 聚合的核心细节

- 聚合工程的打包方式必须为`pom`
- 模块构建顺序与`<modules>`中书写顺序**无关**，由模块间的依赖关系自动决定
- 对聚合工程执行`clean`/`install`等命令，会自动对所有子模块执行相同命令

### 3. 继承与聚合的联系与区别

|   特性   |                             继承                             |           聚合            |
| :------: | :----------------------------------------------------------: | :-----------------------: |
| 关系类型 |                       纵向（父子工程）                       |     横向（兄弟模块）      |
| 配置位置 |                    子工程的`<parent>`标签                    | 聚合工程的`<modules>`标签 |
| 核心作用 |                    简化依赖配置、统一版本                    |     快速构建整个项目      |
| 打包方式 |                        父工程为`pom`                         |      聚合工程为`pom`      |
| **联系** | ① 均为设计型模块，无业务代码；② 实际开发中通常**合二为一**，一个工程既做父工程又做聚合工程 |                           |

## 三、Maven 私服

### 1. 私服核心概念

私服是架设在**局域网内**的特殊远程仓库，作为本地仓库和中央仓库之间的中间层，是企业级开发的必备组件。

### 2. 私服的核心作用

- 解决**团队内部的资源共享与同步**问题（如自定义工具类、公共模块）
- 代理中央仓库，加速依赖下载，降低中央仓库访问压力
- 管理企业内部的私有依赖，避免核心代码外泄

### 3. 依赖查找顺序（有私服时）

Maven 查找依赖的优先级：

**本地仓库** → **私服** → **中央仓库**

### 4. 私服的仓库类型

私服会划分不同仓库，用于存储不同类型的资源：

- **RELEASE 仓库**：存储**发行版本**（功能稳定、停止更新，如`1.0.RELEASE`）
- **SNAPSHOT 仓库**：存储**快照版本**（功能不稳定、开发中，如`1.0-SNAPSHOT`）
- **Central 仓库**：代理官方中央仓库，用于下载公共依赖

### 5. 项目版本说明

| 版本类型 |   标识   |           特性           |   存储仓库    |
| :------: | :------: | :----------------------: | :-----------: |
| 发行版本 | RELEASE  |  稳定、可上线、停止更新  | RELEASE 仓库  |
| 快照版本 | SNAPSHOT | 开发中、不稳定、频繁更新 | SNAPSHOT 仓库 |

### 6. 私服的资源上传与下载配置

#### 6.1 核心配置文件

所有私服配置均在 Maven 的全局配置文件`settings.xml`（或项目本地`settings.xml`）中完成，核心包含**服务器认证、镜像、配置文件**三部分。

#### 6.2 步骤 1：配置私服服务器认证（`settings.xml`）

配置访问私服的用户名和密码，对应私服的 RELEASE/SNAPSHOT 仓库：

```xml
<servers>
    <!-- RELEASE仓库认证 -->
    <server>
        <id>maven-releases</id>
        <username>admin</username>
        <password>admin</password>
    </server>
    <!-- SNAPSHOT仓库认证 -->
    <server>
        <id>maven-snapshots</id>
        <username>admin</username>
        <password>admin</password>
    </server>
</servers>
```

> 注：`id`必须与后续 pom.xml 中仓库`id`一致。

#### 6.3 步骤 2：配置资源上传地址（项目`pom.xml`）

在需要上传到私服的模块`pom.xml`中，配置`distributionManagement`，指定 RELEASE/SNAPSHOT 仓库的地址：

```xml
<distributionManagement>
    <!-- 发行版本上传地址 -->
    <repository>
        <id>maven-releases</id>
        <url>http://192.168.150.101:8081/repository/maven-releases/</url>
    </repository>
    <!-- 快照版本上传地址 -->
    <snapshotRepository>
        <id>maven-snapshots</id>
        <url>http://192.168.150.101:8081/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

#### 6.4 步骤 3：配置资源下载地址（`settings.xml`）

配置**镜像**和**配置文件**，让 Maven 从私服下载依赖（而非直接访问中央仓库）：

##### 配置镜像（mirrors）

```xml
<mirrors>
    <mirror>
        <!-- 镜像ID，自定义 -->
        <id>maven-public</id>
        <!-- 匹配所有仓库，*表示所有依赖都从私服下载 -->
        <mirrorOf>*</mirrorOf>
        <!-- 私服的公共仓库组地址（包含RELEASE/SNAPSHOT/Central） -->
        <url>http://192.168.150.101:8081/repository/maven-public/</url>
    </mirror>
</mirrors>
```

##### 配置仓库（profiles）

```xml
<profiles>
    <profile>
        <id>allow-snapshots</id>
        <!-- 默认激活该配置 -->
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <repositories>
            <repository>
                <id>maven-public</id>
                <url>http://192.168.150.101:8081/repository/maven-public/</url>
                <!-- 允许下载RELEASE版本 -->
                <releases><enabled>true</enabled></releases>
                <!-- 允许下载SNAPSHOT版本 -->
                <snapshots><enabled>true</enabled></snapshots>
            </repository>
        </repositories>
    </profile>
</profiles>
```

### 7. 私服的资源操作命令

- **本地安装**：`mvn install` → 将模块安装到**本地仓库**
- **私服上传**：`mvn deploy` → 将模块上传到**私服**（需完成上述配置，否则报错）
- **依赖下载**：Maven 自动执行，按「本地→私服→中央」顺序查找

### 8. 私服核心注意事项

- 企业中私服**仅需搭建一台**，所有团队共用
- 私服地址、用户名、密码由运维人员提供，开发人员仅需配置使用
- 快照版本会自动更新，发行版本一旦上传不可修改 / 删除

## 四、Maven 高级核心总结

### 1. 分模块开发

- 核心：拆分子模块，解耦功能 / 层级，遵循**先设计后编码**
- 策略：按功能、按层、按功能 + 层，根据项目规模选择

### 2. 继承与聚合

- 继承：纵向管理，简化依赖、统一版本（核心`dependencyManagement`版本锁定）
- 聚合：横向管理，快速构建项目（核心`<modules>`配置）
- 实际开发：**父工程 + 聚合工程合二为一**，打包方式均为`pom`

### 3. 私服

- 作用：团队资源共享、代理中央仓库、管理私有依赖
- 依赖查找：本地仓库 → 私服 → 中央仓库
- 核心操作：配置`settings.xml`（认证、镜像、仓库）+ 项目`pom.xml`（上传地址），通过`mvn deploy`上传资源