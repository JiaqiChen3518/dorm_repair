import request from '../utils/request';
import { API } from './config';

// 用户相关API
export const userApi = {
  /**
   * 登录
   * @param {Object} data - 登录参数
   * @param {string} data.account - 账号
   * @param {string} data.password - 密码
   * @param {string} data.captcha - 验证码
   * @returns {Promise}
   */
  login(data) {
    return request.post(API.USER.LOGIN, data);
  },
  
  /**
   * 注册
   * @param {Object} data - 注册参数
   * @param {string} data.account - 账号
   * @param {string} data.password - 密码
   * @param {number} data.role - 角色
   * @param {string} data.captcha - 验证码
   * @returns {Promise}
   */
  register(data) {
    return request.post(API.USER.REGISTER, data);
  },
  
  /**
   * 获取用户信息
   * @returns {Promise}
   */
  getInfo() {
    return request.get(API.USER.INFO);
  },
  
  /**
   * 修改密码
   * @param {Object} data - 修改密码参数
   * @param {string} data.oldPassword - 原密码
   * @param {string} data.newPassword - 新密码
   * @param {string} data.confirmPassword - 确认新密码
   * @returns {Promise}
   */
  updatePassword(data) {
    return request.post(API.USER.UPDATE_PASSWORD, data);
  },
  
  /**
   * 绑定宿舍
   * @param {Object} data - 绑定宿舍参数
   * @param {string} data.building - 楼栋
   * @param {string} data.room - 房间号
   * @returns {Promise}
   */
  bindDormitory(data) {
    return request.post(API.USER.BIND_DORMITORY, data);
  }
};