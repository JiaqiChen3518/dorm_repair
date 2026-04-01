package com.qg.dorm.controller;

import com.qg.dorm.common.Result;
import com.qg.dorm.dto.CompleteOrderDTO;
import com.qg.dorm.dto.CreateOrderDTO;
import com.qg.dorm.dto.UpdateStatusDTO;
import com.qg.dorm.entity.RepairOrder;
import com.qg.dorm.entity.Statistics;
import com.qg.dorm.service.RepairOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class RepairOrderController {

    @Autowired
    private RepairOrderService repairOrderService;

    @PostMapping("/create")
    public Result<RepairOrder> createOrder(@RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return repairOrderService.createOrder(userId, dto.getBuilding(), dto.getRoom(), dto.getDeviceType(), 
                                             dto.getPriority(), dto.getDescription(), dto.getImages());
    }

    @GetMapping("/myOrders")
    public Result<List<RepairOrder>> getMyOrders(@RequestParam(required = false) Integer status, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return repairOrderService.getMyOrders(userId, status);
    }

    @GetMapping("/detail/{orderId}")
    public Result<RepairOrder> getOrderDetail(@PathVariable Long orderId, HttpServletRequest request) {
        return repairOrderService.getOrderDetail(orderId);
    }

    @PostMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return repairOrderService.cancelOrder(orderId, userId);
    }

    @GetMapping("/all")
    public Result<List<RepairOrder>> getAllOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String building,
            HttpServletRequest request) {
        return repairOrderService.getAllOrders(status, building);
    }

    @PostMapping("/updateStatus")
    public Result<Void> updateOrderStatus(@RequestBody UpdateStatusDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return repairOrderService.updateOrderStatus(dto.getOrderId(), dto.getStatus(), userId);
    }

    @PostMapping("/complete")
    public Result<Void> completeOrder(@RequestBody CompleteOrderDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return repairOrderService.completeOrder(dto.getOrderId(), dto.getNote(), userId);
    }

    @PostMapping("/delete/{orderId}")
    public Result<Void> deleteOrder(@PathVariable Long orderId, HttpServletRequest request) {
        return repairOrderService.deleteOrder(orderId);
    }

    @GetMapping("/statistics")
    public Result<Statistics> getStatistics(HttpServletRequest request) {
        return repairOrderService.getStatistics();
    }
}
