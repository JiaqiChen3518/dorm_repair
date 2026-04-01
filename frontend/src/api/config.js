// API接口路径常量
export const API = {
  // 用户相关
  USER: {
    LOGIN: '/user/login',
    REGISTER: '/user/register',
    INFO: '/user/info',
    UPDATE_PASSWORD: '/user/updatePassword',
    BIND_DORMITORY: '/user/bindDormitory'
  },
  
  // 报修单相关
  ORDER: {
    CREATE: '/order/create',
    MY_ORDERS: '/order/myOrders',
    ALL: '/order/all',
    DETAIL: '/order/detail',
    CANCEL: '/order/cancel',
    UPDATE_STATUS: '/order/updateStatus',
    COMPLETE: '/order/complete',
    DELETE: '/order/delete',
    STATISTICS: '/order/statistics'
  },
  
  // 评价相关
  EVALUATION: {
    CREATE: '/evaluation/create',
    ALL: '/evaluation/all'
  },
  
  // 通知相关
  NOTIFICATION: {
    MY_NOTIFICATIONS: '/notification/myNotifications',
    UNREAD_COUNT: '/notification/unreadCount',
    MARK_ALL_AS_READ: '/notification/markAllAsRead'
  },
  
  // 验证码相关
  CAPTCHA: {
    GENERATE: '/captcha/generate'
  },
  
  // 文件上传相关
  FILE: {
    UPLOAD: '/file/upload'
  }
};