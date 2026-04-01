package com.qg.dorm.service;

import com.qg.dorm.common.Result;
import com.qg.dorm.entity.RepairOrder;
import com.qg.dorm.entity.Statistics;

import java.util.List;

public interface RepairOrderService {
    /**
     * 创建维修订单
     * @param userId
     * @param building
     * @param room
     * @param deviceType
     * @param priority
     * @param description
     * @param images
     * @return
     */
    Result<RepairOrder> createOrder(Long userId, String building, String room, Integer deviceType, Integer priority, String description, String images);

    /**
     * 查询我的维修订单
     * @param userId
     * @param status
     * @return
     */
    Result<List<RepairOrder>> getMyOrders(Long userId, Integer status);

    /**
     * 查询维修订单详情
     * @param orderId
     * @return
     */
    Result<RepairOrder> getOrderDetail(Long orderId);

    /**
     * 取消维修订单
     * @param orderId
     * @param userId
     * @return
     */
    Result<Void> cancelOrder(Long orderId, Long userId);

    /**
     * 查询所有维修订单
     * @param status
     * @param building
     * @return
     */
    Result<List<RepairOrder>> getAllOrders(Integer status, String building);

    /**
     * 更新维修订单状态
     * @param orderId
     * @param status
     * @param handlerId
     * @return
     */
    Result<Void> updateOrderStatus(Long orderId, Integer status, Long handlerId);

    /**
     * 完成维修订单
     * @param orderId
     * @param note
     * @param handlerId
     * @return
     */
    Result<Void> completeOrder(Long orderId, String note, Long handlerId);

    /**
     * 删除维修订单
     * @param orderId
     * @return
     */
    Result<Void> deleteOrder(Long orderId);

    /**
     * 查询维修订单统计信息
     * @return
     */
    Result<Statistics> getStatistics();
}
