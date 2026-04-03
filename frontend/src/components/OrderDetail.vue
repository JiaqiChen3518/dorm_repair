<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal">
      <div class="modal-header">
        <h3>📋 报修单详情</h3>
        <button class="modal-close" @click="$emit('close')">×</button>
      </div>
      <div class="modal-body">
        <div class="order-detail-item">
          <span class="order-detail-label">单号</span>
          <span class="order-detail-value">{{ order.orderNo }}</span>
        </div>
        <div class="order-detail-item">
          <span class="order-detail-label">报修人</span>
          <span class="order-detail-value">{{ order.account || order.userId || '未知' }}</span>
        </div>
        <div class="order-detail-item">
          <span class="order-detail-label">宿舍</span>
          <span class="order-detail-value">{{ order.building }}-{{ order.room }}</span>
        </div>
        <div class="order-detail-item">
          <span class="order-detail-label">设备类型</span>
          <span class="order-detail-value">{{ getDeviceTypeText(order.deviceType) }}</span>
        </div>
        <div class="order-detail-item">
          <span class="order-detail-label">优先级</span>
          <span class="order-detail-value">{{ getPriorityText(order.priority) }}</span>
        </div>
        <div class="order-detail-item">
          <span class="order-detail-label">状态</span>
          <span class="order-detail-value">
            <span class="order-status-badge" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </span>
          </span>
        </div>
        <div class="order-detail-item" style="flex-direction: column;">
          <span class="order-detail-label" style="margin-bottom: 8px;">问题描述</span>
          <span class="order-detail-value description">{{ order.description }}</span>
        </div>
        <div v-if="order.images && order.images.trim()" class="order-detail-item" style="flex-direction: column;">
          <span class="order-detail-label" style="margin-bottom: 8px;">报修图片</span>
          <div class="image-gallery">
            <div class="gallery-images">
              <img 
                v-for="(img, index) in order.images.split(',')" 
                :key="index" 
                :src="img.trim()" 
                @click="handleImageClick(img.trim())"
                @error="console.error('图片加载失败:', img)"
              >
            </div>
          </div>
        </div>
        <div v-if="order.handleNote" class="order-detail-item" style="flex-direction: column;">
          <span class="order-detail-label" style="margin-bottom: 8px;">处理备注</span>
          <span class="order-detail-value description">{{ order.handleNote }}</span>
        </div>
        <div class="order-detail-item">
          <span class="order-detail-label">创建时间</span>
          <span class="order-detail-value" style="color: #999; font-weight: 400;">{{ formatDate(order.createTime) }}</span>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="$emit('close')">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { DEVICE_TYPES, PRIORITY_TYPES, ORDER_STATUS, STATUS_CLASSES } from '../utils/constants';

const props = defineProps({
  order: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['close']);

const getDeviceTypeText = (type) => {
  return DEVICE_TYPES[type] || '未知';
};

const getPriorityText = (priority) => {
  return PRIORITY_TYPES[priority] || '未知';
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

const handleImageClick = (imgUrl) => {
  console.log('图片点击事件触发:', imgUrl);
  try {
    window.open(imgUrl, '_blank');
    console.log('图片已在新窗口打开');
  } catch (error) {
    console.error('打开图片失败:', error);
  }
};
</script>

<style scoped>
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
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
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

.modal-body {
  padding: 20px;
}

.order-detail-item {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}

.order-detail-label {
  width: 100px;
  font-weight: 500;
  color: #666;
}

.order-detail-value {
  flex: 1;
  color: #333;
}

.order-detail-value.description {
  white-space: pre-wrap;
  line-height: 1.5;
}

.image-gallery {
  margin-top: 8px;
}

.gallery-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.gallery-images img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  cursor: pointer;
  transition: all 0.2s;
}

.gallery-images img:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.order-status-badge.pending {
  background-color: #faad14;
  color: white;
}

.order-status-badge.processing {
  background-color: #1890ff;
  color: white;
}

.order-status-badge.completed {
  background-color: #52c41a;
  color: white;
}

.order-status-badge.cancelled {
  background-color: #8c8c8c;
  color: white;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #e8e8e8;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}
</style>