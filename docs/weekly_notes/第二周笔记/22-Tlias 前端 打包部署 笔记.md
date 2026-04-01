# Tlias 前端实战 - 员工管理、登录退出、打包部署 笔记

# 一、员工管理 - 修改员工

## 1. 实现步骤

1. **点击编辑按钮** → 根据 id 查询员工详情
2. **数据回显** → 表单自动填充员工信息
3. **点击保存** → 根据是否有 id 判断是新增还是修改

## 2. 查询回显（核心）

```js
// 编辑员工
const handleUpdate = async (id) => {
  // 1. 打开弹窗
  dialogVisible.value = true
  dialogTitle.value = "修改员工"

  // 2. 根据ID查询详情
  const res = await getEmpByIdApi(id)
  employee.value = res.data

  // 3. 回显工作经历
  if (!employee.value.exprList) {
    employee.value.exprList = []
  }
}
```

## 3. 保存（新增 / 修改 二合一）

```js
const save = async () => {
  await formRef.value.validate()

  if (employee.value.id) {
    // 有ID → 修改
    await updateEmpApi(employee.value)
    ElMessage.success("修改成功")
  } else {
    // 无ID → 新增
    await addEmpApi(employee.value)
    ElMessage.success("新增成功")
  }

  dialogVisible.value = false
  search() // 刷新列表
  formRef.value.resetFields()
}
```

------

# 二、员工管理 - 删除员工

## 1. 两种删除方式

- **单个删除**：点击每行删除按钮
- **批量删除**：勾选多行 → 点击批量删除

## 2. 单个删除

```js
const handleDelete = async (id) => {
  await ElMessageBox.confirm("确定删除该员工？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  })

  // 调用删除接口
  await deleteEmpApi(id)
  ElMessage.success("删除成功")
  search()
}
```

## 3. 批量删除

### 3.1 表格多选绑定

```vue
<el-table 
  :data="empList" 
  v-model:selection="selectedIds"
>		
```

```
const selectedIds = ref([]) // 选中的员工数组
```

### 3.2 批量删除方法

```js
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning("请选择要删除的记录")
    return
  }

  await ElMessageBox.confirm("确定删除选中记录？")
  const ids = selectedIds.value.map(item => item.id)
  
  await batchDeleteEmpApi(ids)
  ElMessage.success("批量删除成功")
  search()
}
```

------

# 三、登录 / 退出 功能

## 1. 登录流程

1. 输入用户名、密码
2. 调用登录接口
3. **成功：保存 token 到 localStorage**
4. 跳转到首页

## 2. 登录代码

```js
const login = async () => {
  await loginFormRef.value.validate()
  const res = await loginApi(loginForm.value)
  
  // 保存用户信息 + token
  localStorage.setItem("loginUser", JSON.stringify(res.data))
  ElMessage.success("登录成功")
  router.push("/index")
}
```

## 3. localStorage 核心 API

```js
localStorage.setItem("key", value)   // 存
localStorage.getItem("key")         // 取
localStorage.removeItem("key")       // 删
localStorage.clear()                 // 清空
```

## 4. 请求拦截器（统一携带 token）

**文件：utils/request.js**

```js
request.interceptors.request.use(config => {
  const user = JSON.parse(localStorage.getItem("loginUser"))
  if (user && user.token) {
    config.headers.token = user.token
  }
  return config
})
```

## 5. 响应拦截器（401 自动跳登录）

```js
request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response.status === 401) {
      ElMessage.error("登录已失效，请重新登录")
      router.push("/login")
      localStorage.clear()
    } else {
      ElMessage.error("服务异常")
    }
    return Promise.reject(error)
  }
)
```

## 6. 退出登录

```js
const logout = () => {
  localStorage.clear()
  ElMessage.success("退出成功")
  router.push("/login")
}
```

------

# 四、前端项目打包与部署（Nginx）

## 1. 打包项目

```bash
npm run build
```

生成：**dist 目录**（静态资源）

## 2. Nginx 部署步骤

1. 下载 Nginx
2. 将 **dist 目录下所有文件** 复制到 `nginx/html`
3. 双击 `nginx.exe` 启动
4. 访问：`http://localhost`

## 3. Nginx 常用命令

```bash
nginx.exe          # 启动
nginx.exe -s stop  # 停止
nginx.exe -s reload # 重启（修改配置后用）
```

## 4. 端口被占用处理

```bash
netstat -ano | findStr "80"
```

修改 `conf/nginx.conf` 里的 listen 端口即可。

------

# 五、重点知识总结（必背）

## 1. 修改员工

- 编辑 = **查询回显 + 弹窗复用**
- 保存 = **根据 id 判断新增 / 修改**

## 2. 删除员工

- 单个删除：根据 id 删除
- 批量删除：获取 `selection` 数组 → 提取 id 列表

## 3. 登录认证

- 登录成功 → **localStorage 存 token**
- 请求拦截器 → **统一带 token**
- 响应拦截器 → **401 自动跳登录**

## 4. 打包部署

- 打包：`npm run build` → dist
- 部署：dist 放入 nginx/html
- 启动：nginx.exe