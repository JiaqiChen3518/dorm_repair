import axios from 'axios';

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
});

// 请求拦截器，添加token到请求头
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = 'Bearer ' + token;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 统一处理返回数据
    const res = response.data;
    if (res.code === 200) {
      return res;
    } else {
      // 非成功状态，返回错误
      return Promise.reject(new Error(res.msg || '请求失败'));
    }
  },
  error => {
    if (error.response && error.response.status === 401) {
      // 登录过期，跳转到登录页
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      localStorage.removeItem('userRole');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default request;