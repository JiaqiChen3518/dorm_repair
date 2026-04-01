<template>
  <div class="container">
    <div class="card">
      <!-- 用户信息 -->
      <div class="user-info">
        <div>
          <strong>👤 {{ userInfo.account }}</strong>
          <span style="margin-left: 10px; color: #666;">管理员</span>
        </div>
        <div>
          <button class="btn btn-secondary" @click="logout">退出</button>
        </div>
      </div>

      <!-- 管理员菜单 -->
      <div v-if="currentView === 'menu'" class="menu-grid">
        <div class="menu-item" @click="currentView = 'allOrders'; loadAllOrders()">
          <span>📋</span>
          <div>查看所有报修单</div>
        </div>
        <div class="menu-item" @click="currentView = 'statistics'; loadStatistics()">
          <span>📊</span>
          <div>报修统计</div>
        </div>
        <div class="menu-item" @click="currentView = 'evaluations'; loadEvaluations()">
          <span>⭐</span>
          <div>评价管理</div>
        </div>
        <div class="menu-item" @click="currentView = 'profile'">
          <span>⚙️</span>
          <div>修改密码</div>
        </div>
      </div>

      <!-- 所有报修单列表 -->
      <div v-if="currentView === 'allOrders'">
        <h3 style="margin-bottom: 20px;">所有报修单</h3>
        <div class="filter-bar">
          <select v-model="adminOrderFilter.status" @change="loadAllOrders">
            <option :value="null">全部状态</option>
            <option :value="0">待处理</option>
            <option :value="1">处理中</option>
            <option :value="2">已完成</option>
            <option :value="3">已取消</option>
          </select>
          <input type="text" v-model="adminOrderFilter.building" placeholder="楼栋筛选" @input="loadAllOrders">
        </div>
        <div v-if="allOrders.length === 0" class="empty-state">
          <div class="empty-state-icon">📋</div>
          <p>暂无报修记录</p>
        </div>
        <table v-else class="table">
          <thead>
            <tr>
              <th>单号</th>
              <th>报修人</th>
              <th>宿舍</th>
              <th>设备</th>
              <th>优先级</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in allOrders" :key="order.id">
              <td>{{ order.orderNo }}</td>
              <td>{{ order.userName }}</td>
              <td>{{ order.building }}-{{ order.room }}</td>
              <td>{{ getDeviceTypeText(order.deviceType) }}</td>
              <td><span class="badge" :class="'badge-' + getPriorityClass(order.priority)">{{ getPriorityText(order.priority) }}</span></td>
              <td><span class="badge" :class="'badge-' + getStatusClass(order.status)">{{ getStatusText(order.status) }}</span></td>
              <td>{{ formatDate(order.createTime) }}</td>
              <td>
                <button class="btn btn-secondary" style="padding: 6px 12px;" @click="viewOrderDetail(order)">详情</button>
                <button v-if="order.status === 0" class="btn btn-primary" style="padding: 6px 12px;" @click="processOrder(order.id)">处理</button>
                <button v-if="order.status === 1" class="btn btn-success" style="padding: 6px 12px;" @click="openCompleteModal(order)">完成</button>
                <button class="btn btn-danger" style="padding: 6px 12px;" @click="deleteOrder(order.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
        <button class="btn btn-secondary" @click="currentView = 'menu'" style="margin-top: 20px;">返回</button>
      </div>

      <!-- 报修统计 -->
      <div v-if="currentView === 'statistics'">
        <h3 style="margin-bottom: 20px;">报修统计</h3>
        <div class="stats-grid">
          <div class="stat-card">
            <h3>{{ statistics.total }}</h3>
            <p>总报修数</p>
          </div>
          <div class="stat-card">
            <h3>{{ statistics.pending }}</h3>
            <p>待处理</p>
          </div>
          <div class="stat-card">
            <h3>{{ statistics.processing }}</h3>
            <p>处理中</p>
          </div>
          <div class="stat-card">
            <h3>{{ statistics.completed }}</h3>
            <p>已完成</p>
          </div>
        </div>
        <button class="btn btn-secondary" @click="currentView = 'menu'">返回</button>
      </div>

      <!-- 评价管理 -->
      <div v-if="currentView === 'evaluations'">
        <h3 style="margin-bottom: 20px;">评价管理</h3>
        <div v-if="evaluations.length === 0" class="empty-state">
          <div class="empty-state-icon">⭐</div>
          <p>暂无评价</p>
        </div>
        <div v-else>
          <div v-for="evaluation in evaluations" :key="evaluation.id" class="card">
            <div style="display: flex; justify-content: space-between;">
              <div>
                <strong>报修单：{{ evaluation.orderNo }}</strong>
                <div class="rating">
                  <span v-for="n in 5" :key="n" class="star" :class="{active: n <= evaluation.rating}">★</span>
                </div>
              </div>
              <small style="color: #999;">{{ formatDate(evaluation.createTime) }}</small>
            </div>
            <p style="margin-top: 10px;">{{ evaluation.content }}</p>
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

    <!-- 完成报修单弹窗 -->
    <div v-if="showCompleteModal" class="modal-overlay" @click.self="showCompleteModal = false">
      <div class="modal">
        <h3 style="margin-bottom: 20px;">完成报修单</h3>
        <div class="form-group">
          <label>处理备注</label>
          <textarea v-model="completeForm.note" placeholder="请输入处理备注..."></textarea>
        </div>
        <button class="btn btn-primary" @click="completeOrder">确认完成</button>
        <button class="btn btn-secondary" @click="showCompleteModal = false">取消</button>
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
import { userApi, orderApi, evaluationApi } from '../api';
import { DEVICE_TYPES, PRIORITY_TYPES, PRIORITY_CLASSES, STATUS_CLASSES, ORDER_STATUS } from '../utils/constants';

const router = useRouter();

// 状态管理
const userInfo = ref({});
const currentView = ref('menu');

// 表单数据
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const completeForm = ref({
  orderId: null,
  note: ''
});

// 数据列表
const allOrders = ref([]);
const evaluations = ref([]);
const statistics = ref({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0
});

// 筛选条件
const adminOrderFilter = ref({
  status: null,
  building: ''
});

// 弹窗状态
const showOrderDetail = ref(false);
const showCompleteModal = ref(false);
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
    // 登录成功，加载统计
    loadStatistics();
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

// 加载所有报修单
const loadAllOrders = async () => {
  try {
    const res = await orderApi.getAllOrders(adminOrderFilter.value.status, adminOrderFilter.value.building);
    allOrders.value = res.data;
  } catch (error) {
    console.error('加载报修单失败:', error);
  }
};

// 查看报修单详情
const viewOrderDetail = (order) => {
  selectedOrder.value = order;
  showOrderDetail.value = true;
};

// 处理报修单
const processOrder = async (orderId) => {
  try {
    await orderApi.updateStatus({ orderId: orderId, status: 1 });
    showToast('报修单已开始处理');
    loadAllOrders();
  } catch (error) {
    console.error('处理报修单失败:', error);
    showToast('处理失败，请重试', 'error');
  }
};

// 打开完成弹窗
const openCompleteModal = (order) => {
  completeForm.value.orderId = order.id;
  showCompleteModal.value = true;
};

// 完成报修单
const completeOrder = async () => {
  if (!completeForm.value.note) {
    showToast('请输入处理备注', 'error');
    return;
  }
  try {
    await orderApi.completeOrder(completeForm.value);
    showToast('报修单已完成');
    showCompleteModal.value = false;
    loadAllOrders();
  } catch (error) {
    console.error('完成报修单失败:', error);
    showToast('完成失败，请重试', 'error');
  }
};

// 删除报修单
const deleteOrder = async (orderId) => {
  if (confirm('确定要删除这个报修单吗？')) {
    try {
      await orderApi.deleteOrder(orderId);
      showToast('报修单已删除');
      loadAllOrders();
    } catch (error) {
      console.error('删除报修单失败:', error);
      showToast('删除失败，请重试', 'error');
    }
  }
};

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await orderApi.getStatistics();
    statistics.value = res.data;
  } catch (error) {
    console.error('加载统计数据失败:', error);
  }
};

// 加载评价
const loadEvaluations = async () => {
  try {
    const res = await evaluationApi.getAll();
    evaluations.value = res.data;
  } catch (error) {
    console.error('加载评价失败:', error);
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

// 使用默认参数语法显示提示信息组件
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
  max-width: 1000px;
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

.filter-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-bar select,
.filter-bar input {
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.stat-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}

.stat-card h3 {
  font-size: 32px;
  margin-bottom: 10px;
  color: #409eff;
}

.stat-card p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.rating {
  display: flex;
  gap: 5px;
  margin-top: 10px;
}

.star {
  font-size: 16px;
  color: #dcdfe6;
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
  
  .filter-bar {
    flex-direction: column;
    gap: 10px;
  }
  
  .table {
    font-size: 14px;
  }
  
  .table th,
  .table td {
    padding: 8px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>