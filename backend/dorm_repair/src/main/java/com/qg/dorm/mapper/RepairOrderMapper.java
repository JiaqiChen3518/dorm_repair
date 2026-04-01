package com.qg.dorm.mapper;

import com.qg.dorm.entity.RepairOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepairOrderMapper {

    int insert(RepairOrder order);

    RepairOrder selectById(Long id);

    RepairOrder selectByOrderNo(String orderNo);

    List<RepairOrder> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    List<RepairOrder> selectAll(@Param("status") Integer status, @Param("building") String building);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("handlerId") Long handlerId);

    int cancelOrder(Long id);

    int deleteById(Long id);

    int completeOrder(@Param("id") Long id, @Param("note") String note);

    int countByStatus(Integer status);

    int countAll();
}
