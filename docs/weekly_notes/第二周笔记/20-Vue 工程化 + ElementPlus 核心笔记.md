# Vue 工程化 + ElementPlus 核心笔记

## 一、Vue 基础认知

### 1. Vue 核心定义

Vue 是一款用于构建用户界面的**渐进式 JavaScript 框架**，核心能力是**基于数据渲染用户界面**，支持从局部模块改造到整站开发的渐进式使用。

### 2. Vue 生态体系

Vue 不仅是核心框架，更是完整生态，配套工具满足工程化开发需求：

- 核心包：Vue3 核心语法与功能
- 构建工具：Vite、Webpack（项目打包、热部署）
- 路由管理：VueRouter（单页应用路由）
- 状态管理：Pinia、Vuex（全局数据管理）
- 组件系统：Vue 单文件组件（SFC）+ 第三方组件库（ElementPlus）

## 二、Vue 工程化

### 1. 前端工程化核心概念

在企业级开发中，将前端开发的**工具、技术、流程、经验**进行**规范化、标准化**，解决原生开发**不规范、难维护、难复用**的问题，核心特性：

- **模块化**：项目拆分为若干模块，单独开发、维护，提高效率
- **组件化**：将页面组成部分封装为组件，提高复用性
- **规范化**：统一目录结构、编码规范、开发流程
- **自动化**：项目构建、调试、测试、打包、部署全自动化

### 2. 环境准备

#### 2.1 核心依赖

- **NodeJS**：跨平台 JavaScript 运行时环境，提供 npm 包管理工具（推荐 LTS 长期支持版）

- npm

  ：Node Package Manager，NodeJS 的软件包管理器，用于安装 / 管理项目依赖

  - 核心命令：`npm install 包名`（从远程仓库下载依赖到本地`node_modules`）

  

#### 2.2 脚手架工具

**create-vue**：Vue 官方最新脚手架工具，用于快速生成工程化 Vue 项目，提供能力：

- 统一目录结构、本地调试、热部署
- 单元测试、集成打包上线

### 3. Vue 项目核心操作（命令行）

#### 3.1 创建项目

```bash
# 创建指定版本的Vue3项目（推荐3.3.4）
npm create vue@3.3.4
```

执行后按提示选择配置（默认选 No 即可，后续可按需添加）：

- Project name：项目名称
- Add TypeScript：是否添加 TypeScript
- Add Vue Router：是否添加路由
- Add Pinia：是否添加状态管理
- Add ESLint：是否添加代码质量检查

#### 3.2 安装依赖

进入项目目录后执行，下载项目所需所有依赖：

```bash
cd 项目名称
npm install
```

> 注意：创建和安装依赖均需联网，依赖会下载到`node_modules`目录。

#### 3.3 启动项目

```bash
# 启动开发环境，支持热部署
npm run dev
```

启动成功后访问默认地址：`http://localhost:5173`

### 4. Vue 项目目录结构（核心）

|   目录 / 文件    |                           核心作用                           |
| :--------------: | :----------------------------------------------------------: |
|  `node_modules`  |                    存放下载的第三方依赖包                    |
|     `public`     |          静态资源公共目录，存放不会被打包的静态文件          |
|      `src`       |                 源代码主目录（开发核心目录）                 |
|   `src/assets`   |    静态资源目录，存放图片、字体、CSS 等（会被 Vite 打包）    |
| `src/components` |       组件目录，存放通用公共组件（如按钮、卡片、表格）       |
|  `src/App.vue`   |                 项目根组件，所有页面的父组件                 |
|  `src/main.js`   |    项目入口文件，创建 Vue 实例、挂载根组件、配置全局依赖     |
|  `package.json`  | 项目配置文件，包含项目名称、版本、依赖包、脚本命令（如 dev/build） |
| `vite.config.js` |       Vite 配置文件，可配置项目端口、代理、打包规则等        |

### 5. Vue 项目开发流程

1. 入口文件`main.js`

   ：创建 Vue 应用、挂载到 HTML 的

   ```js
   #app
   ```

   节点，引入全局依赖（如 ElementPlus）

   ```js
   import './assets/main.css'
   import { createApp } from 'vue'
   import App from './App.vue'
   // 创建Vue应用并挂载到id为app的DOM节点
   createApp(App).mount('#app')
   ```

   

2. **根组件`App.vue`**：项目的根容器，引入其他子组件、搭建页面整体布局

3. **单文件组件（.vue）**：Vue 工程化开发的核心，一个文件封装**模板、脚本、样式**三部分，实现组件化开发

### 6. Vue 单文件组件（SFC）

后缀为`.vue`的文件，是 Vue 工程化的核心，将组件的**模板（HTML）、逻辑（JS）、样式（CSS）** 封装在同一个文件中，结构固定：

```vue
<!-- 脚本部分：组件逻辑（数据、方法、钩子函数），Vue3推荐<script setup> -->
<script setup>
// 引入Vue的响应式API
import { ref } from 'vue'
// 声明响应式变量
const message = ref('Hello Vue3')
</script>

<!-- 模板部分：组件的HTML结构，基于数据渲染界面 -->
<template>
  <h1>{{ message }}</h1>
</template>

<!-- 样式部分：组件的CSS样式，scoped表示样式仅作用于当前组件 -->
<style scoped>
h1 {
  color: #42b983;
}
</style>
```

- **`<script setup>`**：Vue3 组合式 API 的语法糖，简化开发，无需手动暴露变量 / 方法
- **`<template>`**：唯一根节点，使用 Vue 插值表达式`{{ 变量 }}`渲染数据
- **`<style scoped>`**：`scoped`属性实现样式隔离，避免组件间样式冲突

### 7. Vue3 的两种 API 风格

Vue3 支持**选项式 API**和**组合式 API**，工程化开发推荐**组合式 API（`<script setup>`）**。

#### 7.1 选项式 API

通过包含多个选项的**对象**描述组件逻辑，如`data`、`methods`、`mounted`，通过`this`访问组件实例：

```vue
<script>
export default {
  // 响应式数据
  data() {
    return { count: 0 }
  },
  // 方法
  methods: {
    increment() {
      this.count++
    }
  },
  // 生命周期钩子
  mounted() {
    console.log('组件挂载完成')
  }
}
</script>
<template>
  <button @click="increment">count: {{ count }}</button>
</template>
```

#### 7.2 组合式 API（推荐）

基于**函数**的编写方式，通过 Vue 提供的 API（如`ref`、`onMounted`）组织逻辑，无`this`，更灵活、易复用：

```vue
<script setup>
// 引入所需API
import { ref, onMounted } from 'vue'
// 声明响应式变量（ref包裹基本类型）
const count = ref(0)
// 声明方法
function increment() {
  // 组合式API中需通过.value修改ref响应式变量的值
  count.value++
}
// 生命周期钩子
onMounted(() => {
  console.log('组件挂载完成')
})
</script>
<template>
  <button @click="increment">count: {{ count }}</button>
</template>
```

#### 7.3 组合式 API 核心注意点

1. 无`this`对象，`this`为`undefined`；
2. `ref`声明的响应式变量，**修改值时必须通过`.value`**，模板中使用无需`.value`；
3. `<script setup>`是语法糖，自动暴露变量 / 方法到模板，无需手动`export`。

### 8. 工程化中使用 Axios

项目中需发送异步请求时，先安装 Axios 依赖，再在组件中引入使用：

```bash
# 安装Axios
npm install axios
```

```js
<script setup>
import axios from 'axios'
import { ref, onMounted } from 'vue'

const empList = ref([])
// 页面挂载后发送请求
onMounted(async () => {
  const res = await axios.get('https://web-server.itheima.net/emps/list')
  empList.value = res.data.data
})
</script>
```

## 三、ElementPlus

### 1. ElementPlus 核心定义

- 由饿了么团队开发的**基于 Vue3 的开源 UI 组件库**；
- 提供丰富的开箱即用的组件（按钮、表格、表单、分页等），快速搭建前端页面；
- 官网：https://element-plus.org/zh-CN/

### 2. ElementPlus 快速入门（三步法）

#### 步骤 1：安装依赖

在 Vue 项目目录下执行，指定稳定版本（2.4.4）：

```bash
npm install element-plus@2.4.4 --save
```

#### 步骤 2：全局引入（main.js）

在项目入口文件中引入 ElementPlus 并注册，全局所有组件均可使用：

```js
import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
// 1. 引入ElementPlus核心和样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 2. 引入中文语言包（可选，默认英文）
import zhCn from 'element-plus/es/locale/lang/zh-cn'

// 3. 注册ElementPlus，配置中文
createApp(App).use(ElementPlus, { locale: zhCn }).mount('#app')
```

#### 步骤 3：使用组件

从 ElementPlus 官网复制组件代码，粘贴到 Vue 组件中，按需调整**数据、样式、事件**即可。

### 3. ElementPlus 核心常用组件

#### 3.1 表格组件（el-table）

**作用**：展示多条结构类似的数据，支持排序、筛选、自定义列，核心是**数据绑定**。

```vue
<template>
  <!-- :data 绑定表格数据源 -->
  <el-table :data="tableData" style="width: 100%">
    <!-- prop 绑定数据源的属性名，label 列标题 -->
    <el-table-column prop="id" label="ID" width="80" />
    <el-table-column prop="name" label="姓名" width="120" />
    <el-table-column prop="gender" label="性别" />
    <el-table-column prop="job" label="职位" />
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const tableData = ref([])
// 异步请求获取表格数据
onMounted(async () => {
  const res = await axios.get('https://web-server.itheima.net/emps/list')
  tableData.value = res.data.data
})
</script>
```

**核心关键点**：`:data`绑定数据源数组，`el-table-column`的`prop`对应数组对象的属性名。

#### 3.2 分页组件（el-pagination）

**作用**：数据量过多时，分页展示数据，需配合表格使用，核心是**分页事件**和**数据联动**。

> 已引入中文语言包后，分页组件自动显示中文。

#### 3.3 对话框组件（el-dialog）

**作用**：保留页面状态的前提下，弹出弹窗做提示 / 操作，核心是**显示 / 隐藏控制**。

```vue
<template>
  <el-button @click="dialogVisible = true">打开弹窗</el-button>
  <!-- v-model 绑定布尔值，控制对话框显示/隐藏 -->
  <el-dialog v-model="dialogVisible" title="对话框标题" width="30%">
    <span>这是对话框的内容</span>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="dialogVisible = false">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
// 控制对话框显示的布尔值
const dialogVisible = ref(false)
</script>
```

**核心关键点**：通过`v-model`绑定布尔值（`true`显示 /`false`隐藏）控制弹窗状态。

#### 3.4 表单组件（el-form + el-form-item + 表单项）

**作用**：收集、验证、提交用户输入的表单数据，核心是**数据绑定（v-model）\**和\**事件绑定**。

- 常用表单项：`el-input`（输入框）、`el-select`（下拉选择）、`el-radio`（单选）、`el-checkbox`（多选）
- 核心：`el-form`的`model`绑定表单数据对象，`el-form-item`的`prop`做表单验证，表单项通过`v-model`绑定对象属性。

```vue
<template>
  <el-form :model="searchForm" inline @submit.prevent="search">
    <el-form-item label="姓名">
      <el-input v-model="searchForm.name" placeholder="请输入姓名" />
    </el-form-item>
    <el-form-item label="性别">
      <el-select v-model="searchForm.gender" placeholder="请选择性别">
        <el-option label="男" value="1" />
        <el-option label="女" value="2" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="clear">清空</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref } from 'vue'
// 表单数据对象
const searchForm = ref({
  name: '',
  gender: ''
})
// 查询方法
function search() {
  console.log('查询条件：', searchForm.value)
}
// 清空方法
function clear() {
  searchForm.value = { name: '', gender: '' }
}
</script>
```

**核心关键点**：

1. `v-model`实现表单项数据的双向绑定；
2. 事件绑定（如`@click`）实现表单提交 / 清空操作；
3. `@submit.prevent`阻止表单默认提交行为。

### 4. ElementPlus 组件使用通用原则

1. **查文档**：从官网找到对应组件，查看**基础用法、属性、事件、插槽**；
2. **粘代码**：复制官网基础代码到 Vue 组件中；
3. **改数据**：将官网的示例数据替换为项目的实际数据（异步请求获取）；
4. **绑事件**：根据业务需求绑定组件的事件（如点击、分页、关闭）；
5. **调样式**：通过组件属性或自定义 CSS 调整样式，满足页面需求。

## 四、核心实战案例：用户列表查询页面

### 1. 需求

搭建包含**查询表单、用户表格**的页面，页面加载后异步请求数据，支持按姓名 / 性别 / 职位查询、清空查询条件。

### 2. 技术栈

Vue3 组合式 API + Axios + ElementPlus（el-form、el-input、el-select、el-table、el-button）。

### 3. 核心步骤

1. 安装并全局引入 ElementPlus 和 Axios；
2. 在组件中搭建页面结构：el-form（查询条件） + el-table（用户列表）；
3. 声明响应式数据：表单查询对象、表格数据源；
4. 生命周期钩子`onMounted`中发送异步请求，加载初始数据；
5. 编写查询 / 清空方法，实现表单与表格的数据联动；
6. 按需调整组件样式和属性，完成页面开发。

## 五、核心总结

### 1. Vue 工程化核心

1. 开发环境依赖 NodeJS+npm，通过 create-vue 脚手架快速创建项目；
2. 核心命令：`npm create vue@3.3.4` → `npm install` → `npm run dev`；
3. 单文件组件（.vue）是开发核心，封装模板、脚本、样式，`scoped`实现样式隔离；
4. Vue3 推荐使用**组合式 API（`<script setup>`）**，无`this`，`ref`声明响应式变量，修改需`.value`。

### 2. ElementPlus 核心

1. 三步使用：安装依赖 → main.js 全局引入 → 组件中复制使用；

2. 核心组件关键点：

   - 表格：`:data`绑定数据源，`prop`对应对象属性；
   - 对话框：`v-model`绑定布尔值控制显示 / 隐藏；
   - 表单：`v-model`实现双向数据绑定，事件绑定实现提交 / 清空；

   

3. 开发原则：**查官网→粘代码→改数据→绑事件→调样式**。

### 3. 前后端交互

1. 安装 Axios：`npm install axios`；
2. 组合式 API 中通过`async/await`发送异步请求；
3. 响应式变量接收后端返回数据，绑定到 ElementPlus 组件实现页面渲染。