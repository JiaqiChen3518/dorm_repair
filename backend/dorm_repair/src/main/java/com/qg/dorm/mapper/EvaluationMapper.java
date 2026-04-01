package com.qg.dorm.mapper;

import com.qg.dorm.entity.Evaluation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EvaluationMapper {

    /**
     * 插入评价
     * @param evaluation
     * @return
     */
    @Insert("INSERT INTO evaluation (order_id, user_id, rating, content, create_time) " +
            "VALUES (#{orderId}, #{userId}, #{rating}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Evaluation evaluation);


    /**
     * 根据订单ID查询评价
     * @param orderId
     * @return
     */
    @Select("SELECT e.*, u.name as user_name, o.order_no " +
            "FROM evaluation e " +
            "LEFT JOIN user u ON e.user_id = u.id " +
            "LEFT JOIN repair_order o ON e.order_id = o.id " +
            "WHERE e.order_id = #{orderId}")
    Evaluation selectByOrderId(Long orderId);

    /**
     * 查询所有评价
     * @return
     */
    @Select("SELECT e.*, u.name as user_name, o.order_no " +
            "FROM evaluation e " +
            "LEFT JOIN user u ON e.user_id = u.id " +
            "LEFT JOIN repair_order o ON e.order_id = o.id " +
            "ORDER BY e.create_time DESC")
    List<Evaluation> selectAll();

    /**
     * 查询平均评分
     * @return
     */
    @Select("SELECT AVG(rating) FROM evaluation")
    Double selectAverageRating();
}
