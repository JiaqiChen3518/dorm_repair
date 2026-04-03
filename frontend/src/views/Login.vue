<template>
  <div class="login-container">
    <div class="card">
      <div class="header">
        <h1>🏠 宿舍报修管理系统</h1>
        <p>请登录或注册</p>
      </div>
      <div class="tabs">
        <div class="tab" :class="{ active: loginTab === 'login' }" @click="loginTab = 'login'">登录</div>
        <div class="tab" :class="{ active: loginTab === 'register' }" @click="loginTab = 'register'">注册</div>
      </div>
      
      <div v-if="loginTab === 'login'">
        <div class="form-group">
          <label>账号</label>
          <input type="text" v-model="loginForm.account" placeholder="请输入学号/工号">
        </div>
        <div class="form-group">
          <label>密码</label>
          <input type="password" v-model="loginForm.password" placeholder="请输入密码">
        </div>
        <div class="form-group">
          <label>验证码</label>
          <div class="captcha-container">
            <input type="text" v-model="loginForm.captcha" placeholder="请输入验证码">
            <div class="captcha-box" @click="generateCaptcha" title="点击刷新验证码">
              {{ captchaCode }}
            </div>
          </div>
        </div>
        <button class="btn btn-primary" @click="login">登录</button>
      </div>
      
      <div v-if="loginTab === 'register'">
        <div class="form-group">
          <label>角色</label>
          <select v-model="registerForm.role">
            <option :value="1">学生</option>
            <option :value="2">维修人员/管理员</option>
          </select>
        </div>
        <div class="form-group">
          <label>账号</label>
          <input type="text" v-model="registerForm.account" :placeholder="registerForm.role === 1 ? '学号（3125或3225开头）' : '工号（0025开头）'">
        </div>
        <div class="form-group">
          <label>密码</label>
          <input type="password" v-model="registerForm.password" placeholder="请输入密码">
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input type="password" v-model="registerForm.confirmPassword" placeholder="请再次输入密码">
        </div>
        <div class="form-group">
          <label>验证码</label>
          <div class="captcha-container">
            <input type="text" v-model="registerForm.captcha" placeholder="请输入验证码">
            <div class="captcha-box" @click="generateCaptcha" title="点击刷新验证码">
              {{ captchaCode }}
            </div>
          </div>
        </div>
        <button class="btn btn-primary" @click="register">注册</button>
      </div>
    </div>
    
    <Toast 
      :show="toast.show" 
      :message="toast.message" 
      :type="toast.type" 
      @close="toast.show = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Toast from '../components/Toast.vue';
import { userApi, captchaApi } from '../api';

const router = useRouter();

// 状态管理
const loginTab = ref('login');
const captchaCode = ref('');

// 登录表单
const loginForm = ref({
  account: '',
  password: '',
  captcha: ''
});

// 注册表单
const registerForm = ref({
  account: '',
  password: '',
  confirmPassword: '',
  role: 1,
  captcha: ''
});

// 提示信息
const toast = ref({
  show: false,
  message: '',
  type: 'success'
});

// 生成验证码
const generateCaptcha = async () => {
  try {
    const res = await captchaApi.generate();
    captchaCode.value = res.data;
  } catch (error) {
    console.error('生成验证码失败:', error);
  }
};

// 登录
const login = async () => {
  if (!loginForm.value.account || !loginForm.value.password || !loginForm.value.captcha) {
    showToast('请填写完整信息', 'error');
    return;
  }
  
  try {
    const res = await userApi.login(loginForm.value);
    // 登录成功后，将token、用户信息和角色存储到localStorage
    localStorage.setItem('token', res.data.token);
    localStorage.setItem('userInfo', JSON.stringify(res.data));
    localStorage.setItem('userRole', res.data.role);
    showToast('登录成功');
    setTimeout(() => {
      if (res.data.role === 1) {
        router.push('/student');
      } else {
        router.push('/admin');
      }
    }, 1000);
  } catch (error) {
    console.error('登录失败:', error);
     showToast(error.message || '登录失败，请重试', 'error');
    generateCaptcha();
  }
};

// 注册
const register = async () => {
  if (!registerForm.value.account || !registerForm.value.password || !registerForm.value.confirmPassword || !registerForm.value.captcha) {
    showToast('请填写完整信息', 'error');
    return;
  }
  
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    showToast('两次密码输入不一致', 'error');
    return;
  }
  
  try {
    await userApi.register({
      account: registerForm.value.account,
      password: registerForm.value.password,
      role: registerForm.value.role,
      captcha: registerForm.value.captcha
    });
    
    showToast('注册成功，请登录');
    loginTab.value = 'login';
    generateCaptcha();
  } catch (error) {
    console.error('注册失败:', error);
    showToast(error.message || '注册失败，请重试', 'error');
    generateCaptcha();
  }
};

// 显示提示
const showToast = (message, type = 'success') => {
  toast.value.message = message;
  toast.value.type = type;
  toast.value.show = true;
};

// 组件挂载时生成验证码
onMounted(() => {
  generateCaptcha();
});
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  padding: 20px;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  width: 100%;
  max-width: 400px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.header p {
  color: #666;
  font-size: 14px;
}

.tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  transition: all 0.3s;
}

.tab.active {
  color: #409eff;
  border-bottom: 2px solid #409eff;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #409eff;
}

.captcha-container {
  display: flex;
  gap: 10px;
}

.captcha-container input {
  flex: 1;
}

.captcha-box {
  width: 100px;
  height: 40px;
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  font-weight: bold;
  color: #666;
  user-select: none;
}

.btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #409eff;
  color: white;
}

.btn-primary:hover {
  background-color: #66b1ff;
}
</style>