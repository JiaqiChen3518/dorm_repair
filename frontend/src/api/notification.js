import request from '../utils/request';
import { API } from './config';

// 通知相关API
export const notificationApi = {
  /**
   * 获取我的通知
   * @returns {Promise}
   */
  getMyNotifications() {
    return request.get(API.NOTIFICATION.MY_NOTIFICATIONS);
  },
  
  /**
   * 获取未读通知数量
   * @returns {Promise}
   */
  getUnreadCount() {
    return request.get(API.NOTIFICATION.UNREAD_COUNT);
  },
  
  /**
   * 标记所有通知为已读
   * @returns {Promise}
   */
  markAllAsRead() {
    return request.post(API.NOTIFICATION.MARK_ALL_AS_READ);
  }
};