# MyBatis-Plus 笔记

## 一、MyBatis-Plus 基础认知

### 1. 核心定义

MyBatis-Plus（简称 MP）是基于 MyBatis 的**增强工具**，遵循「不改变 MyBatis 原有功能」的原则，仅对其进行封装扩展，核心目标是**简化单表 CRUD 开发**，减少重复编码工作量。

### 2. 核心价值

- 解决单表 CRUD 代码**重复度高、开发繁琐、耗时费力**的痛点
- 对 MyBatis 功能进行增强（如分页、条件查询、批量操作等）
- 国内企业广泛应用，适配 MySQL、Oracle 等主流数据库
- 低侵入性，兼容 MyBatis 原有项目，可平滑迁移

### 3. 官方资源

- 官方网站：https://baomidou.com/
- 核心特性：简化配置、CRUD 接口、条件构造器、分页插件等

## 二、环境搭建（核心步骤）

### 1. 导入依赖（Maven）

```xml
<!-- MyBatis-Plus 核心依赖 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3.1</version> <!-- 推荐稳定版本 -->
</dependency>

<!-- 数据库驱动（以 MySQL 为例） -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. 核心配置（application.yml）

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_db_name?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  # 配置 mapper 接口扫描路径
  mapper-locations: classpath:mapper/*.xml
  # 配置实体类别名扫描路径（简化 XML 中类名书写）
  type-aliases-package: com.itheima.pojo
  configuration:
    # 开启 SQL 日志打印（便于调试）
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  # 全局配置
  global-config:
    db-config:
      # 主键生成策略（自增）
      id-type: auto
      # 表名前缀（可选，如数据库表名前缀为 t_，实体类无需写前缀）
      table-prefix: t_
```

### 3. 启动类注解

在 SpringBoot 启动类添加 `@MapperScan`，扫描 Mapper 接口：

```java
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itheima.mapper") // 扫描 Mapper 接口所在包
public class MyBatisPlusDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyBatisPlusDemoApplication.class, args);
    }
}
```

## 三、核心基础用法（单表 CRUD）

### 1. 实体类配置（注解说明）

实体类与数据库表映射，核心注解：

```java
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data // Lombok 注解，简化 getter/setter/toString
@TableName("t_emp") // 对应数据库表名（若配置了 table-prefix，可直接写 "emp"）
public class Emp {
    @TableId(type = IdType.AUTO) // 主键，自增策略
    private Long id;
    
    // 字段名与数据库列名一致时，无需额外注解
    private String name;
    private Integer age;
    private String gender;
    private String dept;
    
    // 若字段名与列名不一致（如实体类 fieldName，数据库 field_name），用 @TableField
    // @TableField("field_name")
    // private String fieldName;
}
```

### 2. Mapper 接口（继承 BaseMapper）

无需编写 XML 和 SQL，直接继承 MP 提供的 `BaseMapper`，获得全套单表 CRUD 方法：

```java
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.pojo.Emp;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpMapper extends BaseMapper<Emp> {
    // 无需手动编写 CRUD 方法，BaseMapper 已提供
}
```

### 3. BaseMapper 核心方法（实战案例）

注入 `EmpMapper` 后，直接调用方法完成操作：

```java
import com.itheima.mapper.EmpMapper;
import com.itheima.pojo.Emp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmpService {

    @Autowired
    private EmpMapper empMapper;

    // 1. 新增（insert）
    public boolean addEmp(Emp emp) {
        // insert 方法：返回影响行数，>0 表示成功
        int rows = empMapper.insert(emp);
        return rows > 0;
    }

    // 2. 根据 ID 查询（selectById）
    public Emp getEmpById(Long id) {
        return empMapper.selectById(id);
    }

    // 3. 查询所有（selectList）
    public List<Emp> getAllEmps() {
        // queryWrapper 为 null 时，查询所有
        return empMapper.selectList(null);
    }

    // 4. 根据 ID 修改（updateById）
    public boolean updateEmp(Emp emp) {
        // 必须设置 id（作为修改条件）
        int rows = empMapper.updateById(emp);
        return rows > 0;
    }

    // 5. 根据 ID 删除（deleteById）
    public boolean deleteEmp(Long id) {
        int rows = empMapper.deleteById(id);
        return rows > 0;
    }

    // 6. 批量删除（deleteBatchIds）
    public boolean deleteBatch(List<Long> ids) {
        int rows = empMapper.deleteBatchIds(ids);
        return rows > 0;
    }
}
```

## 四、条件查询（QueryWrapper 核心用法）

### 1. 核心说明

`QueryWrapper` 是 MP 提供的条件构造器，用于动态拼接 SQL 条件，无需手动写 WHERE 子句。

### 2. 常用方法案例

```java
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

@Service
public class EmpService {

    @Autowired
    private EmpMapper empMapper;

    // 1. 多条件精确查询（姓名=张三 + 部门=研发部）
    public List<Emp> getEmpByCondition(String name, String dept) {
        QueryWrapper<Emp> queryWrapper = new QueryWrapper<>();
        // eq：等于（=）
        queryWrapper.eq("name", name)
                   .eq("dept", dept);
        return empMapper.selectList(queryWrapper);
    }

    // 2. 模糊查询（姓名包含"张"）
    public List<Emp> getEmpLikeName(String name) {
        QueryWrapper<Emp> queryWrapper = new QueryWrapper<>();
        // like：模糊查询（%值%）
        queryWrapper.like("name", name);
        return empMapper.selectList(queryWrapper);
    }

    // 3. 范围查询（年龄 between 20-30）
    public List<Emp> getEmpByAgeRange(Integer minAge, Integer maxAge) {
        QueryWrapper<Emp> queryWrapper = new QueryWrapper<>();
        // between：范围查询（between min and max）
        queryWrapper.between("age", minAge, maxAge);
        return empMapper.selectList(queryWrapper);
    }

    // 4. 排序查询（按年龄降序）
    public List<Emp> getEmpOrderByAge() {
        QueryWrapper<Emp> queryWrapper = new QueryWrapper<>();
        // orderByDesc：降序排序
        queryWrapper.orderByDesc("age");
        return empMapper.selectList(queryWrapper);
    }

    // 5. 非空条件查询（姓名不为空 + 部门=研发部）
    public List<Emp> getEmpByNotNull(String dept) {
        QueryWrapper<Emp> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("name")
                   .eq("dept", dept);
        return empMapper.selectList(queryWrapper);
    }
}
```

### 3. QueryWrapper 常用方法对照表

|         方法名         |    作用    |         SQL 对应语法          |
| :--------------------: | :--------: | :---------------------------: |
|      eq(key, val)      |    等于    |        WHERE key = val        |
|      ne(key, val)      |   不等于   |       WHERE key != val        |
|     like(key, val)     |  模糊查询  |     WHERE key LIKE %val%      |
|   likeLeft(key, val)   |   左模糊   |      WHERE key LIKE %val      |
|  likeRight(key, val)   |   右模糊   |      WHERE key LIKE val%      |
| between(key, min, max) |  范围查询  | WHERE key BETWEEN min AND max |
|      isNull(key)       |  字段为空  |       WHERE key IS NULL       |
|     isNotNull(key)     | 字段不为空 |     WHERE key IS NOT NULL     |
|    orderByAsc(key)     |  升序排序  |       ORDER BY key ASC        |
|    orderByDesc(key)    |  降序排序  |       ORDER BY key DESC       |
|     in(key, list)      |  包含查询  | WHERE key IN (val1, val2...)  |

## 五、分页查询（分页插件）

### 1. 配置分页插件

创建 MP 配置类，注册分页插件：

```java
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件（指定数据库类型为 MySQL）
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(com.baomidou.mybatisplus.core.metadata.IPage.class));
        return interceptor;
    }
}
```

### 2. 分页查询案例

```java
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

@Service
public class EmpService {

    @Autowired
    private EmpMapper empMapper;

    // 分页查询所有员工（每页10条，查询第2页）
    public IPage<Emp> getEmpByPage(Integer pageNum, Integer pageSize) {
        // 1. 创建分页对象（pageNum：页码，pageSize：每页条数）
        IPage<Emp> page = new Page<>(pageNum, pageSize);
        
        // 2. 执行分页查询（第二个参数为条件构造器，null 表示无额外条件）
        IPage<Emp> empPage = empMapper.selectPage(page, null);
        
        // 3. 分页结果获取
        long total = empPage.getTotal(); // 总条数
        long pages = empPage.getPages(); // 总页数
        List<Emp> records = empPage.getRecords(); // 当前页数据
        
        return empPage;
    }

    // 带条件的分页查询（部门=研发部，每页10条，第1页）
    public IPage<Emp> getEmpByPageAndDept(Integer pageNum, Integer pageSize, String dept) {
        IPage<Emp> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Emp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dept", dept);
        return empMapper.selectPage(page, queryWrapper);
    }
}
```

## 六、Service 层封装（IService 接口）

### 1. 核心作用

MP 提供 `IService` 和 `ServiceImpl`，进一步封装 Service 层逻辑，提供批量操作、链式调用等增强功能。

### 2. 用法步骤

#### 步骤 1：定义 Service 接口，继承 IService

```java
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.pojo.Emp;

public interface EmpService extends IService<Emp> {
    // 可添加自定义业务方法
    List<Emp> getEmpByAge(Integer age);
}
```

#### 步骤 2：实现 Service 类，继承 ServiceImpl

```java
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.mapper.EmpMapper;
import com.itheima.pojo.Emp;
import com.itheima.service.EmpService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmpServiceImpl extends ServiceImpl<EmpMapper, Emp> implements EmpService {

    // 继承 IService 后，直接使用父类提供的 CRUD 方法
    @Override
    public List<Emp> getEmpByAge(Integer age) {
        // 调用 mapper 方法或使用条件构造器
        return baseMapper.selectList(
            new QueryWrapper<Emp>().eq("age", age)
        );
    }

    // 批量新增（IService 提供的增强方法）
    public boolean addBatchEmp(List<Emp> empList) {
        return saveBatch(empList); // 父类方法，无需手动实现
    }

    // 链式查询（IService 增强）
    public List<Emp> getEmpByDeptChain(String dept) {
        return lambdaQuery()
                .eq(Emp::getDept, dept)
                .list();
    }
}
```

#### 步骤 3：IService 核心方法（常用）

|            方法名             |               作用               |
| :---------------------------: | :------------------------------: |
|        save(T entity)         |           新增单条数据           |
|    saveBatch(List<T> list)    |             批量新增             |
|  removeById(Serializable id)  |           根据 ID 删除           |
| removeBatchByIds(List<?> ids) |             批量删除             |
|     updateById(T entity)      |           根据 ID 修改           |
|   getById(Serializable id)    |           根据 ID 查询           |
|            list()             |           查询所有数据           |
|      page(IPage<T> page)      |             分页查询             |
|         lambdaQuery()         | 链式条件查询（避免硬编码字段名） |

## 七、核心注解汇总

|    注解     |                 作用                 |                用法示例                |
| :---------: | :----------------------------------: | :------------------------------------: |
| @TableName  |         实体类与数据库表映射         |          @TableName("t_emp")           |
|  @TableId   |             主键字段映射             |      @TableId(type = IdType.AUTO)      |
| @TableField | 普通字段映射（字段名与列名不一致时） |        @TableField("emp_name")         |
| @TableLogic |        逻辑删除字段（假删除）        | @TableLogic private Integer isDeleted; |
|  @Version   |              乐观锁字段              |   @Version private Integer version;    |

## 八、常见问题与注意事项

1. **主键生成策略**：`IdType.AUTO` 需数据库表主键设置为自增，否则报错；非自增主键可使用 `IdType.ASSIGN_ID`（雪花算法）。
2. **字段名与列名映射**：默认下划线转驼峰（如实体类 `empName` 对应数据库 `emp_name`），无需额外注解。
3. **逻辑删除**：配置 `@TableLogic` 后，删除操作会自动转为更新 `is_deleted` 字段（1 = 删除，0 = 正常），查询时自动过滤已删除数据。
4. **分页插件**：3.5+ 版本需使用 `MybatisPlusInterceptor` 配置，旧版本 `PaginationInterceptor` 已废弃。
5. **SQL 日志打印**：通过 `mybatis-plus.configuration.log-impl` 配置，便于调试 SQL 语句。

## 九、核心总结

1. MyBatis-Plus 核心是「简化单表 CRUD」，通过 BaseMapper、IService 封装重复操作。
2. 条件查询用 QueryWrapper，动态拼接 SQL 无需手动写 WHERE 子句。
3. 分页查询需配置分页插件，通过 IPage 对象获取分页结果。
4. 注解核心：@TableName（表映射）、@TableId（主键）、@TableField（字段映射）。
5. 优势：减少重复代码、提高开发效率、低侵入性、兼容 MyBatis 原有功能。