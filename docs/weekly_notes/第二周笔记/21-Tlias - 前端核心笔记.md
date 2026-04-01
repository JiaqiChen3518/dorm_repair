# Tlias 智能学习辅助系统 - 前端实战核心笔记

## 一、开发模式：前后端分离

### 1. 核心定义

当前主流的 Web 开发模式，**前端、后端开发人员独立开发、独立部署**，通过**接口文档**约定交互规则，实现解耦和并行开发。

### 2. 开发流程

需求分析` → `接口定义` → `前后端并行开发` → `各自测试` → `前后端联调测试

### 3. 核心协作依据

- 前端：根据**原型 + 需求 + 接口文档**开发，专注页面渲染和交互
- 后端：根据**需求 + 接口文档**开发，专注接口实现和数据处理
- 交互：前端发送**请求**，后端返回**响应**，数据格式通常为 JSON

## 二、项目整体布局

### 1. 准备工作

1. 导入基础工程包`vue-tlias-management.zip`到 VSCode
2. 执行`npm install`安装依赖，`npm run dev`启动项目，默认访问`http://localhost:5173`

### 2. 页面布局核心：ElementPlus Container 布局容器

使用 ElementPlus 提供的布局组件实现经典**后台管理系统布局**，包含顶栏、侧边栏、主内容区，组件嵌套关系：

```vue
<el-container> <!-- 外层容器 -->
  <el-header>顶栏（系统名称、用户信息、退出登录）</el-header>
  <el-container>
    <el-aside>左侧菜单栏（功能导航）</el-aside>
    <el-main>主内容区（核心业务展示）</el-main>
  </el-container>
</el-container>
```

### 3. 动态菜单实现：Vue Router（Vue 官方路由）

#### 3.1 路由核心定义

Vue 中的路由指**路径与组件的对应关系**，实现单页应用（SPA）的页面切换，无需刷新浏览器。

#### 3.2 Vue Router 三大核心组成

1. **Router 实例**：基于`createRouter`创建，维护应用所有路由规则（`routes`配置）
2. **<router-link>**：路由链接组件，浏览器解析为`<a>`标签，通过`to`属性指定目标路径
3. **<router-view>**：动态视图组件，渲染与当前路由路径匹配的组件，是路由的出口

#### 3.3 项目核心路由模式：嵌套路由

后台管理系统的经典路由方案，**主布局作为父路由**，各业务模块作为子路由，实现布局复用。

##### 1. 路由规则配置（router/index.js）

```js
import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'
import IndexPage from '@/views/index/index.vue'
import DeptPage from '@/views/dept/index.vue'
import EmpPage from '@/views/emp/index.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/', // 父路由路径
      component: Layout, // 父路由组件（整体布局：顶栏+侧边栏+主内容区）
      children: [ // 子路由规则
        { path: 'index', component: IndexPage }, // 首页
        { path: 'dept', component: DeptPage }, // 部门管理
        { path: 'emp', component: EmpPage } // 员工管理
      ]
    }
  ]
})
export default router
```

##### 2. 路由出口匹配

- `App.vue`中添加根路由出口：`<router-view></router-view>`（渲染 Layout 布局组件）
- `layout/index.vue`的`<el-main>`中添加子路由出口：`<router-view></router-view>`（渲染各业务模块组件）

##### 3. 左侧菜单与路由绑定

将 ElementPlus 的`<el-menu>`与路由结合，`index`属性为路由路径，实现点击菜单切换页面：

```vue
<el-menu>
  <el-menu-item index="/index">
    <el-icon><House /></el-icon>
    <span>首页</span>
  </el-menu-item>
  <el-menu-item index="/dept">
    <el-icon><OfficeBuilding /></el-icon>
    <span>部门管理</span>
  </el-menu-item>
  <el-menu-item index="/emp">
    <el-icon><User /></el-icon>
    <span>员工管理</span>
  </el-menu-item>
</el-menu>
```

## 三、核心业务模块：部门管理

实现**列表查询、新增、修改、删除**四大核心 CRUD 操作，是后台管理系统的基础业务模板，技术栈：`Vue3组合式API + Axios + ElementPlus`。

### 3.1 列表查询

#### 3.1.1 页面布局

使用 ElementPlus 的 **<el-button>（新增按钮）** + **<el-table>（表格展示）** 实现基础布局，核心是表格的`data`属性绑定数据源。

#### 3.1.2 数据加载

页面挂载后自动发送异步请求加载部门数据，使用`onMounted`生命周期钩子 + Axios 实现：

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { queryAllApi } from '@/api/dept'

const deptList = ref([]) // 部门列表数据源

// 页面挂载后加载数据
onMounted(async () => {
  const res = await queryAllApi()
  deptList.value = res.data
})
</script>

<template>
  <el-table :data="deptList" border style="width: 100%">
    <el-table-column prop="id" label="序号" width="80" />
    <el-table-column prop="name" label="部门名称" />
    <el-table-column prop="updateTime" label="最后修改时间" />
    <el-table-column label="操作" width="180">
      <template #default="scope">
        <el-button type="primary" size="small">编辑</el-button>
        <el-button type="danger" size="small">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>
```

#### 3.1.3 前端请求优化（解决原生 Axios 问题）

原生直接使用 Axios 存在**请求路径难以维护、数据解析繁琐**的问题，通过三层封装优化：

##### 1. 封装请求工具类：`utils/request.js`

基于 Axios 创建实例，配置基础路径、超时时间，添加**响应拦截器**统一处理返回数据：

```js
import axios from 'axios'

// 创建Axios实例
const request = axios.create({
  baseURL: '/api', // 基础路径，配合代理使用
  timeout: 60000 // 超时时间
})

// 响应拦截器：统一解析返回数据，只返回业务数据
request.interceptors.response.use(
  (response) => {
    // 成功回调：直接返回响应体的data
    return response.data
  },
  (error) => {
    // 失败回调：返回Promise错误
    return Promise.reject(error)
  }
)

export default request
```

##### 2. 封装模块 API：`api/dept.js`

按业务模块封装接口请求，统一管理接口路径，提高可维护性：

```js
import request from '@/utils/request'

// 部门列表查询
export const queryAllApi = () => request.get('/depts')
// 新增部门
export const addDeptApi = (data) => request.post('/depts', data)
// 根据ID查询部门
export const getDeptByIdApi = (id) => request.get(`/depts/${id}`)
// 修改部门
export const updateDeptApi = (data) => request.put('/depts', data)
// 删除部门
export const deleteDeptApi = (id) => request.delete(`/depts/${id}`)
```

##### 3. 配置开发环境代理：`vite.config.js`

解决前端开发时的**跨域问题**，将`/api`开头的请求代理到后端服务地址：

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    // 配置路径别名，@指向src目录
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    // 代理配置
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址
        secure: false, // 允许非HTTPS请求
        changeOrigin: true, // 开启跨域
        rewrite: (path) => path.replace(/^\/api/, '') // 去掉路径中的/api前缀
      }
    }
  }
})
```

##### 代理请求流程

前端请求/api/depts` → `Vite代理去掉/api` → `转发到后端http://localhost:8080/depts` → `后端响应` → `代理转发回前端

#### 3.1.4 前端接口测试方案

前后端并行开发时，后端接口未实现前，前端可通过以下方式测试：

1. **Mock.js**：本地生成模拟假数据
2. **Apifox/Postman Mock 服务**：基于接口文档创建远程 Mock 服务，返回模拟数据

### 3.2 新增部门

#### 3.2.1 页面布局

使用 ElementPlus 的 **<el-dialog>（对话框）** + **<el-form>（表单）** 实现，点击「新增」按钮弹出对话框，表单收集部门名称。

#### 3.2.2 核心要点：表单校验

ElementPlus 表单组件内置校验功能，三步实现**必填、长度限制**等校验规则：

##### 1. 定义校验规则和表单引用

```js
<script setup>
import { ref } from 'vue'
import { addDeptApi } from '@/api/dept'
import { ElMessage } from 'element-plus'

const deptFormRef = ref(null) // 表单引用，用于触发校验
const dept = ref({ name: '' }) // 表单数据对象
// 表单校验规则
const rules = ref({
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }, // 失焦校验，必填
    { min: 2, max: 10, message: '部门名称长度在2-10之间', trigger: 'blur' } // 长度限制
  ]
})
const dialogVisible = ref(false) // 控制对话框显示/隐藏
</script>
```

##### 2. 表单绑定规则和引用

```vue
<template>
  <el-button type="primary" @click="dialogVisible = true">新增部门</el-button>
  <!-- 新增对话框 -->
  <el-dialog v-model="dialogVisible" title="新增部门" width="30%">
    <el-form 
      :model="dept" 
      :rules="rules" 
      ref="deptFormRef" 
      label-width="80px"
    >
      <el-form-item label="部门名称" prop="name">
        <el-input v-model="dept.name" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveDept">确定</el-button>
    </template>
  </el-dialog>
</template>
```

##### 3. 提交时触发校验

```js
// 保存部门
const saveDept = async () => {
  // 触发表单校验
  deptFormRef.value.validate(async (valid) => {
    if (valid) {
      // 校验通过，发送新增请求
      await addDeptApi(dept.value)
      ElMessage.success('新增部门成功')
      dialogVisible.value = false // 关闭对话框
      // 重新加载列表数据
      onMounted()
      // 重置表单
      deptFormRef.value.resetFields()
    }
  })
}
```

#### 3.2.3 复用优化

**新增和修改部门可共用同一个对话框**，通过区分**标题、按钮逻辑**实现，减少代码冗余。

### 3.3 修改部门

分为**查询回显**和**保存修改**两步，基于新增的对话框复用实现。

#### 3.3.1 核心交互逻辑

1. 点击表格的「编辑」按钮，获取当前行的部门 ID，发送请求查询该部门详情
2. 将查询结果赋值给表单数据对象，实现**页面回显**，同时修改对话框标题为「编辑部门」
3. 点击对话框「确定」按钮，发送修改请求，成功后关闭对话框、刷新列表

#### 3.3.2 核心代码实现

```js
// 编辑部门
const editDept = async (id) => {
  dialogVisible.value = true
  // 修改对话框标题
  dialogTitle.value = '编辑部门'
  // 根据ID查询部门详情
  const res = await getDeptByIdApi(id)
  dept.value = res.data // 回显数据
}

// 保存修改（与新增共用一个方法，通过ID判断）
const saveDept = async () => {
  deptFormRef.value.validate(async (valid) => {
    if (valid) {
      if (dept.value.id) {
        // 有ID，执行修改操作
        await updateDeptApi(dept.value)
        ElMessage.success('修改部门成功')
      } else {
        // 无ID，执行新增操作
        await addDeptApi(dept.value)
        ElMessage.success('新增部门成功')
      }
      dialogVisible.value = false
      onMounted() // 刷新列表
      deptFormRef.value.resetFields()
    }
  })
}
```

### 3.4 删除部门

#### 3.4.1 核心交互

点击「删除」按钮，弹出**确认框**（避免误操作），用户确认后发送删除请求，成功后刷新列表。

#### 3.4.2 核心技术：ElementPlus ElMessageBox

```vue
<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteDeptApi } from '@/api/dept'

// 删除部门
const delDept = async (id) => {
  // 弹出确认框
  ElMessageBox.confirm(
    '您确定要删除该部门吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    // 用户确认，执行删除
    await deleteDeptApi(id)
    ElMessage.success('删除部门成功')
    onMounted() // 刷新列表
  }).catch(() => {
    // 用户取消，提示
    ElMessage.info('已取消删除')
  })
}
</script>

<template>
  <el-table-column label="操作" width="180">
    <template #default="scope">
      <el-button type="primary" size="small" @click="editDept(scope.row.id)">编辑</el-button>
      <el-button type="danger" size="small" @click="delDept(scope.row.id)">删除</el-button>
    </template>
  </el-table-column>
</template>
```

## 四、项目核心通用技术总结

### 1. 前后端分离核心

- 接口为中心，通过接口文档约定请求 / 响应格式
- 前端专注交互和渲染，后端专注数据和业务逻辑
- 开发阶段通过 Mock 技术实现前后端并行开发

### 2. Vue Router 核心

- 嵌套路由是后台管理系统的标配，实现布局复用
- 路由三要素：Router 实例（规则）、<router-link>（链接）、<router-view>（出口）
- 左侧菜单与路由的`index`属性绑定，实现页面切换

### 3. Axios 请求封装三层次

- `utils/request.js`：全局封装 Axios 实例、拦截器、基础配置
- `api/xxx.js`：按业务模块封装接口，统一管理路径
- `vite.config.js`：配置开发环境代理，解决跨域问题

### 4. ElementPlus 核心组件使用

- **表格**：`:data`绑定数据源，`prop`对应对象属性，`template #default`实现自定义列
- **表单**：`v-model`绑定数据，`rules`配置校验规则，`ref`触发校验，`prop`绑定表单项
- **对话框**：`v-model`控制显示 / 隐藏，可复用实现新增 / 修改
- **消息框**：`ElMessageBox`实现确认操作，`ElMessage`实现操作结果提示

### 5. Vue3 组合式 API 核心

- `ref`：声明响应式数据（基本类型 / 对象）
- `onMounted`：页面挂载后执行，用于初始化加载数据
- 无`this`，直接调用方法和访问变量
- 子组件数据通过`scope.row`获取（ElementPlus 表格）