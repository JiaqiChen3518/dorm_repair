import request from '../utils/request';
import { API } from './config';

// 报修单相关API
export const orderApi = {
  /**
   * 创建报修单
   * @param {Object} data - 创建报修单参数
   * @param {string} data.building - 楼栋
   * @param {string} data.room - 房间号
   * @param {number} data.deviceType - 设备类型
   * @param {number} data.priority - 优先级
   * @param {string} data.description - 描述
   * @param {string} data.images - 图片
   * @returns {Promise}
   */
  create(data) {
    return request.post(API.ORDER.CREATE, data);
  },
  
  /**
   * 获取我的报修单
   * @param {number} status - 状态
   * @returns {Promise}
   */
  getMyOrders(status) {
    return request.get(API.ORDER.MY_ORDERS, { params: { status } });
  },
  
  /**
   * 获取所有报修单
   * @param {number} status - 状态
   * @param {string} building - 楼栋
   * @returns {Promise}
   */
  getAllOrders(status, building) {
    return request.get(API.ORDER.ALL, { params: { status, building } });
  },
  
  /**
   * 获取报修单详情
   * @param {number} orderId - 订单ID
   * @returns {Promise}
   */
  getOrderDetail(orderId) {
    return request.get(`${API.ORDER.DETAIL}/${orderId}`);
  },
  
  /**
   * 取消报修单
   * @param {number} orderId - 订单ID
   * @returns {Promise}
   */
  cancelOrder(orderId) {
    return request.post(`${API.ORDER.CANCEL}/${orderId}`);
  },
  
  /**
   * 更新报修单状态
   * @param {Object} data - 更新状态参数
   * @param {number} data.orderId - 订单ID
   * @param {number} data.status - 状态
   * @returns {Promise}
   */
  updateStatus(data) {
    return request.post(API.ORDER.UPDATE_STATUS, data);
  },
  
  /**
   * 完成报修单
   * @param {Object} data - 完成订单参数
   * @param {number} data.orderId - 订单ID
   * @param {string} data.note - 处理备注
   * @returns {Promise}
   */
  completeOrder(data) {
    return request.post(API.ORDER.COMPLETE, data);
  },
  
  /**
   * 删除报修单
   * @param {number} orderId - 订单ID
   * @returns {Promise}
   */
  deleteOrder(orderId) {
    return request.post(`${API.ORDER.DELETE}/${orderId}`);
  },
  
  /**
   * 获取统计数据
   * @returns {Promise}
   */
  getStatistics() {
    return request.get(API.ORDER.STATISTICS);
  }
};