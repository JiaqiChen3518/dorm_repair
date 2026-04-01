import request from '../utils/request';
import { API } from './config';

// 验证码相关API
export const captchaApi = {
  /**
   * 生成验证码
   * @returns {Promise}
   */
  generate() {
    return request.get(API.CAPTCHA.GENERATE);
  }
};