<template>
  <div class="container">
    <div class="card">
      <!-- 用户信息 -->
      <div class="user-info">
        <div>
          <strong>👤 {{ userInfo.account }}</strong>
          <span style="margin-left: 10px; color: #666;">学生</span>
          <span v-if="userInfo.isBound" style="margin-left: 10px; color: #4caf50;">📍 {{ userInfo.building }}-{{ userInfo.room }}</span>
        </div>
        <div>
          <span class="notification-badge" style="margin-right: 20px; cursor: pointer;" @click="showNotificationModal">
            🔔
            <span v-if="unreadCount > 0" class="notification-count">{{ unreadCount }}</span>
          </span>
          <button class="btn btn-secondary" @click="logout">退出</button>
        </div>
      </div>

      <!-- 未绑定宿舍提示 -->
      <div v-if="!userInfo.isBound && currentView !== 'bindDormitory'" class="card" style="background: #fff3cd; border: 1px solid #ffc107;">
        <p>⚠️ 您还未绑定宿舍，请先绑定宿舍信息</p>
        <button class="btn btn-warning" @click="currentView = 'bindDormitory'">立即绑定</button>
      </div>

      <!-- 菜单 -->
      <div v-if="currentView === 'menu'" class="menu-grid">
        <div class="menu-item" @click="currentView = 'bindDormitory'">
          <span>🏠</span>
          <div>绑定/修改宿舍</div>
        </div>
        <div class="menu-item" @click="showCreateOrder">
          <span>📝</span>
          <div>创建报修单</div>
        </div>
        <div class="menu-item" @click="currentView = 'myOrders'; loadMyOrders()">
          <span>📋</span>
          <div>我的报修记录</div>
        </div>
        <div class="menu-item" @click="currentView = 'notifications'; loadNotifications()">
          <span>🔔</span>
          <div>消息通知</div>
        </div>
        <div class="menu-item" @click="currentView = 'profile'">
          <span>⚙️</span>
          <div>修改密码</div>
        </div>
      </div>

      <!-- 绑定/修改宿舍 -->
      <div v-if="currentView === 'bindDormitory'">
        <h3 style="margin-bottom: 20px;">{{ userInfo.isBound ? '修改宿舍' : '绑定宿舍' }}</h3>
        <div class="form-group">
          <label>宿舍楼栋</label>
          <input type="text" v-model="dormForm.building" placeholder="如：A1, B2">
        </div>
        <div class="form-group">
          <label>房间号</label>
          <input type="text" v-model="dormForm.room" placeholder="如：301, 402">
        </div>
        <button class="btn btn-primary" @click="bindDormitory">保存</button>
        <button class="btn btn-secondary" @click="currentView = 'menu'">返回</button>
      </div>

      <!-- 创建报修单 -->
      <div v-if="currentView === 'createOrder'">
        <h3 style="margin-bottom: 20px;">创建报修单</h3>
        <div class="form-group">
          <label>宿舍楼栋</label>
          <input type="text" v-model="orderForm.building" placeholder="宿舍楼栋" readonly style="background-color: #f5f5f5;">
        </div>
        <div class="form-group">
          <label>房间号</label>
          <input type="text" v-model="orderForm.room" placeholder="房间号" readonly style="background-color: #f5f5f5;">
        </div>
        <div class="form-group">
          <label>设备类型</label>
          <select v-model="orderForm.deviceType">
            <option :value="1">水龙头</option>
            <option :value="2">电灯</option>
            <option :value="3">门窗</option>
            <option :value="4">空调</option>
            <option :value="5">热水器</option>
            <option :value="6">其他</option>
          </select>
        </div>
        <div class="form-group">
          <label>优先级</label>
          <select v-model="orderForm.priority">
            <option :value="1">紧急</option>
            <option :value="2">普通</option>
            <option :value="3">低</option>
          </select>
        </div>
        <div class="form-group">
          <label>问题描述</label>
          <textarea v-model="orderForm.description" placeholder="请详细描述问题..."></textarea>
        </div>
        <div class="form-group">
          <label>上传图片</label>
          <input type="file" ref="fileInput" @change="handleFileChange" accept="image/*" multiple style="display: none;">
          <button class="btn btn-secondary" @click="$refs.fileInput.click()">选择图片</button>
          <div v-if="previewImages.length > 0" class="image-preview">
            <div v-for="(img, index) in previewImages" :key="index" class="image-preview-item">
              <img :src="img">
              <button class="remove-btn" @click="removeImage(index)">×</button>
            </div>
          </div>
          <div v-if="orderForm.images" class="image-count">
            已上传 {{ uploadedImageCount }} 张图片
          </div>
        </div>
        <button class="btn btn-primary" @click="createOrder">提交</button>
        <button class="btn btn-secondary" @click="currentView = 'menu'">返回</button>
      </div>

      <!-- 我的报修记录 -->
      <div v-if="currentView === 'myOrders'">
        <h3 style="margin-bottom: 20px;">我的报修记录</h3>
        <div class="filter-bar">
          <select v-model="orderFilter" @change="loadMyOrders">
            <option :value="null">全部状态</option>
            <option :value="0">待处理</option>
            <option :value="1">处理中</option>
            <option :value="2">已完成</option>
            <option :value="3">已取消</option>
          </select>
        </div>
        <div v-if="myOrders.length === 0" class="empty-state">
          <div class="empty-state-icon">📋</div>
          <p>暂无报修记录</p>
        </div>
        <table v-else class="table">
          <thead>
            <tr>
              <th>单号</th>
              <th>设备</th>
              <th>优先级</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in myOrders" :key="order.id">
              <td>{{ order.orderNo }}</td>
              <td>{{ getDeviceTypeText(order.deviceType) }}</td>
              <td><span class="badge" :class="'badge-' + getPriorityClass(order.priority)">{{ getPriorityText(order.priority) }}</span></td>
              <td><span class="badge" :class="'badge-' + getStatusClass(order.status)">{{ getStatusText(order.status) }}</span></td>
              <td>{{ formatDate(order.createTime) }}</td>
              <td>
                <button class="btn btn-secondary" style="padding: 6px 12px;" @click="viewOrderDetail(order)">详情</button>
                <button v-if="order.status === 0" class="btn btn-danger" style="padding: 6px 12px;" @click="cancelOrder(order.id)">取消</button>
                <button v-if="order.status === 2 && !order.hasEvaluation" class="btn btn-success" style="padding: 6px 12px;" @click="openEvaluation(order)">评价</button>
              </td>
            </tr>
          </tbody>
        </table>
        <button class="btn btn-secondary" @click="currentView = 'menu'" style="margin-top: 20px;">返回</button>
      </div>

      <!-- 消息通知 -->
      <div v-if="currentView === 'notifications'">
        <h3 style="margin-bottom: 20px;">消息通知</h3>
        <button class="btn btn-secondary" style="margin-bottom: 20px;" @click="markAllAsRead">全部已读</button>
        <div v-if="notifications.length === 0" class="empty-state">
          <div class="empty-state-icon">🔔</div>
          <p>暂无通知</p>
        </div>
        <div v-else>
          <div v-for="notif in notifications" :key="notif.id" class="card" :style="{background: notif.isRead ? '#f9f9f9' : '#e3f2fd'}">
            <h4>{{ notif.title }}</h4>
            <p>{{ notif.content }}</p>
            <small style="color: #999;">{{ formatDate(notif.createTime) }}</small>
          </div>
        </div>
        <button class="btn btn-secondary" @click="currentView = 'menu'" style="margin-top: 20px;">返回</button>
      </div>

      <!-- 修改密码 -->
      <div v-if="currentView === 'profile'">
        <h3 style="margin-bottom: 20px;">修改密码</h3>
        <div class="form-group">
          <label>原密码</label>
          <input type="password" v-model="passwordForm.oldPassword" placeholder="请输入原密码">
        </div>
        <div class="form-group">
          <label>新密码</label>
          <input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码">
        </div>
        <div class="form-group">
          <label>确认新密码</label>
          <input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码">
        </div>
        <button class="btn btn-primary" @click="updatePassword">保存</button>
        <button class="btn btn-secondary" @click="currentView = 'menu'">返回</button>
      </div>
    </div>

    <!-- 报修单详情弹窗 -->
    <OrderDetail 
      v-if="showOrderDetail" 
      :order="selectedOrder" 
      @close="showOrderDetail = false" 
    />

    <!-- 评价弹窗 -->
    <div v-if="showEvaluationModal" class="modal-overlay" @click.self="showEvaluationModal = false">
      <div class="modal">
        <h3 style="margin-bottom: 20px;">评价维修服务</h3>
        <div class="form-group">
          <label>评分</label>
          <div class="rating">
            <span v-for="n in 5" :key="n" class="star" :class="{active: n <= evaluationForm.rating}" @click="evaluationForm.rating = n">★</span>
          </div>
        </div>
        <div class="form-group">
          <label>评价内容</label>
          <textarea v-model="evaluationForm.content" placeholder="请输入您的评价..."></textarea>
        </div>
        <button class="btn btn-primary" @click="submitEvaluation">提交评价</button>
        <button class="btn btn-secondary" @click="showEvaluationModal = false">取消</button>
      </div>
    </div>

    <!-- 消息通知弹窗 -->
    <div v-if="showNotifications" class="modal-overlay" @click.self="showNotifications = false">
      <div class="modal">
        <h3 style="margin-bottom: 20px;">消息通知</h3>
        <button class="btn btn-secondary" style="margin-bottom: 20px;" @click="markAllAsRead">全部已读</button>
        <div v-if="notifications.length === 0" class="empty-state">
          <div class="empty-state-icon">🔔</div>
          <p>暂无通知</p>
        </div>
        <div v-else style="max-height: 400px; overflow-y: auto;">
          <div v-for="notif in notifications" :key="notif.id" class="card" :style="{background: notif.isRead ? '#f9f9f9' : '#e3f2fd', marginBottom: '10px'}">
            <h4>{{ notif.title }}</h4>
            <p>{{ notif.content }}</p>
            <small style="color: #999;">{{ formatDate(notif.createTime) }}</small>
          </div>
        </div>
        <button class="btn btn-secondary" @click="closeNotificationModal" style="margin-top: 20px;">关闭</button>
      </div>
    </div>

    <!-- 提示弹窗 -->
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
import OrderDetail from '../components/OrderDetail.vue';
import { userApi, orderApi, evaluationApi, notificationApi, fileApi } from '../api';
import { DEVICE_TYPES, PRIORITY_TYPES, ORDER_STATUS, PRIORITY_CLASSES, STATUS_CLASSES } from '../utils/constants';

const router = useRouter();

// 状态管理
const userInfo = ref({});
const currentView = ref('menu');
const unreadCount = ref(0);

// 表单数据
const dormForm = ref({
  building: '',
  room: ''
});

const orderForm = ref({
  building: '',
  room: '',
  deviceType: 1,
  priority: 2,
  description: '',
  images: ''
});

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const evaluationForm = ref({
  orderId: null,
  rating: 5,
  content: ''
});

// 图片相关
const previewImages = ref([]);
const selectedFiles = ref([]);
const uploadedImageCount = ref(0);

// 数据列表
const myOrders = ref([]);
const notifications = ref([]);

// 筛选条件
const orderFilter = ref(null);

// 弹窗状态
const showOrderDetail = ref(false);
const showEvaluationModal = ref(false);
const showNotifications = ref(false);
const selectedOrder = ref({});

// 提示信息
const toast = ref({
  show: false,
  message: '',
  type: 'success'
});

// 加载用户信息
const loadUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo');
  if (userInfoStr) {
    userInfo.value = JSON.parse(userInfoStr);
  } else {
    router.push('/login');
  }
};

// 验证会话
const verifySession = async () => {
  try {
    await userApi.getInfo();
    getUnreadCount();
  } catch (error) {
    if (error.response && error.response.status === 401) {
      handleSessionExpired();
    }
  }
};

// 处理会话过期
const handleSessionExpired = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userInfo');
  localStorage.removeItem('userRole');
  showToast('登录已过期，请重新登录', 'error');
  setTimeout(() => {
    router.push('/login');
  }, 1000);
};

// 退出登录
const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userInfo');
  localStorage.removeItem('userRole');
  router.push('/login');
};

// 绑定宿舍
const bindDormitory = async () => {
  try {
    await userApi.bindDormitory(dormForm.value);
    showToast('绑定成功');
    userInfo.value.isBound = 1;
    userInfo.value.building = dormForm.value.building;
    userInfo.value.room = dormForm.value.room;
    // 更新localStorage中的用户信息
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value));
    currentView.value = 'menu';
  } catch (error) {
    console.error('绑定宿舍失败:', error);
    showToast('绑定失败，请重试', 'error');
  }
};

// 显示创建报修单
const showCreateOrder = () => {
  if (!userInfo.value.isBound) {
    showToast('请先绑定宿舍信息', 'error');
    currentView.value = 'bindDormitory';
    return;
  }
  orderForm.value.building = userInfo.value.building;
  orderForm.value.room = userInfo.value.room;
  currentView.value = 'createOrder';
  resetImages();
};

// 重置图片
const resetImages = () => {
  previewImages.value = [];
  selectedFiles.value = [];
  uploadedImageCount.value = 0;
  orderForm.value.images = '';
};

// 处理文件选择
const handleFileChange = (event) => {
  const files = event.target.files;
  if (!files || files.length === 0) return;

  const maxFiles = 5;
  if (selectedFiles.value.length + files.length > maxFiles) {
    showToast(`最多只能上传${maxFiles}张图片`, 'error');
    return;
  }

  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    if (!file.type.startsWith('image/')) {
      showToast('只能上传图片文件', 'error');
      continue;
    }

    if (file.size > 10 * 1024 * 1024) {
      showToast('图片大小不能超过10MB', 'error');
      continue;
    }

    selectedFiles.value.push(file);
    const reader = new FileReader();
    reader.onload = (e) => {
      previewImages.value.push(e.target.result);
    };
    reader.readAsDataURL(file);
  }

  event.target.value = '';
};

// 移除图片
const removeImage = (index) => {
  previewImages.value.splice(index, 1);
  selectedFiles.value.splice(index, 1);
};

// 上传图片
const uploadImages = async () => {
  if (selectedFiles.value.length === 0) {
    return '';
  }

  const imageUrls = [];
  for (let i = 0; i < selectedFiles.value.length; i++) {
    const file = selectedFiles.value[i];
    const formData = new FormData();
    formData.append('file', file);

    try {
      const res = await fileApi.upload(formData);
      imageUrls.push(res.data.url);
    } catch (error) {
      console.error('上传图片失败:', error);
      showToast('上传图片失败: ' + (error.message || '未知错误'), 'error');
      throw error;
    }
  }

  return imageUrls.join(',');
};

// 创建报修单
const createOrder = async () => {
  if (!orderForm.value.description) {
    showToast('请填写问题描述', 'error');
    return;
  }

  try {
    if (selectedFiles.value.length > 0) {
      showToast('正在上传图片...', 'info');
      const imageUrls = await uploadImages();
      orderForm.value.images = imageUrls;
      uploadedImageCount.value = selectedFiles.value.length;
    }

    await orderApi.create(orderForm.value);
    showToast('报修单创建成功');
    orderForm.value = {
      building: '',
      room: '',
      deviceType: 1,
      priority: 2,
      description: '',
      images: ''
    };
    resetImages();
    currentView.value = 'menu';
  } catch (error) {
    console.error('创建报修单失败:', error);
    showToast('创建报修单失败: ' + (error.message || '未知错误'), 'error');
  }
};

// 加载我的报修记录
const loadMyOrders = async () => {
  try {
    const res = await orderApi.getMyOrders(orderFilter.value);
    myOrders.value = res.data;
  } catch (error) {
    console.error('加载报修记录失败:', error);
  }
};

// 查看报修单详情
const viewOrderDetail = (order) => {
  selectedOrder.value = order;
  showOrderDetail.value = true;
};

// 取消报修单
const cancelOrder = async (orderId) => {
  if (confirm('确定要取消这个报修单吗？')) {
    try {
      await orderApi.cancelOrder(orderId);
      showToast('报修单已取消');
      loadMyOrders();
    } catch (error) {
      console.error('取消报修单失败:', error);
      showToast('取消失败，请重试', 'error');
    }
  }
};

// 打开评价弹窗
const openEvaluation = (order) => {
  evaluationForm.value.orderId = order.id;
  showEvaluationModal.value = true;
};

// 提交评价
const submitEvaluation = async () => {
  if (!evaluationForm.value.content) {
    showToast('请输入评价内容', 'error');
    return;
  }
  try {
    await evaluationApi.create(evaluationForm.value);
    showToast('评价提交成功');
    showEvaluationModal.value = false;
    loadMyOrders();
  } catch (error) {
    console.error('提交评价失败:', error);
    showToast('提交评价失败，请重试', 'error');
  }
};

// 加载通知
const loadNotifications = async () => {
  try {
    const res = await notificationApi.getMyNotifications();
    notifications.value = res.data;
  } catch (error) {
    console.error('加载通知失败:', error);
  }
};

// 显示通知弹窗
const showNotificationModal = () => {
  loadNotifications();
  showNotifications.value = true;
};

// 关闭通知弹窗
const closeNotificationModal = () => {
  showNotifications.value = false;
  getUnreadCount();
};

// 标记全部已读
const markAllAsRead = async () => {
  try {
    await notificationApi.markAllAsRead();
    showToast('已标记为已读');
    loadNotifications();
    getUnreadCount();
  } catch (error) {
    console.error('标记已读失败:', error);
    showToast('标记失败，请重试', 'error');
  }
};

// 获取未读通知数量
const getUnreadCount = async () => {
  try {
    const res = await notificationApi.getUnreadCount();
    unreadCount.value = res.data;
  } catch (error) {
    console.error('获取未读通知数量失败:', error);
  }
};

// 修改密码
const updatePassword = async () => {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    showToast('请填写完整信息', 'error');
    return;
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    showToast('两次密码输入不一致', 'error');
    return;
  }
  try {
    await userApi.updatePassword(passwordForm.value);
    showToast('密码修改成功');
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
  } catch (error) {
    console.error('修改密码失败:', error);
    showToast('修改密码失败，请重试', 'error');
  }
};

// 工具函数
const getDeviceTypeText = (type) => {
  return DEVICE_TYPES[type] || '未知';
};

const getPriorityText = (priority) => {
  return PRIORITY_TYPES[priority] || '未知';
};

const getPriorityClass = (priority) => {
  return PRIORITY_CLASSES[priority] || 'normal';
};

const getStatusText = (status) => {
  return ORDER_STATUS[status] || '未知';
};

const getStatusClass = (status) => {
  return STATUS_CLASSES[status] || 'pending';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN');
};

const showToast = (message, type = 'success') => {
  toast.value.message = message;
  toast.value.type = type;
  toast.value.show = true;
};

// 组件挂载
onMounted(() => {
  loadUserInfo();
  verifySession();
});
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
  display: flex;
  justify-content: center;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  width: 100%;
  max-width: 800px;
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.notification-badge {
  position: relative;
  font-size: 20px;
}

.notification-count {
  position: absolute;
  top: -10px;
  right: -10px;
  background: #f56c6c;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.menu-item {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.menu-item:hover {
  background: #e6f7ff;
  transform: translateY(-2px);
}

.menu-item span {
  font-size: 32px;
  display: block;
  margin-bottom: 10px;
}

.menu-item div {
  color: #666;
  font-size: 14px;
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
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #409eff;
}

.image-preview {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image-preview-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.image-preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}

.remove-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #f56c6c;
  color: white;
  border: none;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-count {
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}

.filter-bar {
  margin-bottom: 20px;
}

.filter-bar select {
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.table th,
.table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e8e8e8;
}

.table th {
  background-color: #f5f7fa;
  font-weight: 500;
  color: #666;
}

.badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.badge-high {
  background-color: #f56c6c;
  color: white;
}

.badge-normal {
  background-color: #1890ff;
  color: white;
}

.badge-low {
  background-color: #52c41a;
  color: white;
}

.badge-pending {
  background-color: #faad14;
  color: white;
}

.badge-processing {
  background-color: #1890ff;
  color: white;
}

.badge-completed {
  background-color: #52c41a;
  color: white;
}

.badge-cancelled {
  background-color: #8c8c8c;
  color: white;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  margin-right: 10px;
}

.btn-primary {
  background-color: #409eff;
  color: white;
}

.btn-primary:hover {
  background-color: #66b1ff;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

.btn-danger {
  background-color: #f56c6c;
  color: white;
}

.btn-danger:hover {
  background-color: #f78989;
}

.btn-success {
  background-color: #67c23a;
  color: white;
}

.btn-success:hover {
  background-color: #85ce61;
}

.btn-warning {
  background-color: #e6a23c;
  color: white;
}

.btn-warning:hover {
  background-color: #ebb563;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 20px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.rating {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.star {
  font-size: 24px;
  color: #dcdfe6;
  cursor: pointer;
  transition: color 0.3s;
}

.star.active {
  color: #fadb14;
}

@media (max-width: 768px) {
  .container {
    padding: 10px;
  }
  
  .card {
    padding: 20px;
  }
  
  .menu-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .table {
    font-size: 14px;
  }
  
  .table th,
  .table td {
    padding: 8px;
  }
}
</style>