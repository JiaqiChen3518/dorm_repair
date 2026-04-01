import request from '../utils/request';
import { API } from './config';

// 评价相关API
export const evaluationApi = {
  /**
   * 创建评价
   * @param {Object} data - 创建评价参数
   * @param {number} data.orderId - 订单ID
   * @param {number} data.rating - 评分
   * @param {string} data.content - 评价内容
   * @returns {Promise}
   */
  create(data) {
    return request.post(API.EVALUATION.CREATE, data);
  },
  
  /**
   * 获取所有评价
   * @returns {Promise}
   */
  getAll() {
    return request.get(API.EVALUATION.ALL);
  }
};