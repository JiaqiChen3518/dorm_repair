import request from '../utils/request';
import { API } from './config';

// 文件上传相关API
export const fileApi = {
  /**
   * 上传文件
   * @param {FormData} formData - 表单数据
   * @returns {Promise}
   */
  upload(formData) {
    return request.post(API.FILE.UPLOAD, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
};