package com.qg.dorm.service;

import com.qg.dorm.common.Result;
import com.qg.dorm.entity.Evaluation;

import java.util.List;

public interface EvaluationService {

    /**
     * 创建评价
     * @param orderId
     * @param userId
     * @param rating
     * @param content
     * @return
     */
    Result<Void> createEvaluation(Long orderId, Long userId, Integer rating, String content);

    /**
     * 根据订单ID查询评价
     * @param orderId
     * @return
     */
    Result<Evaluation> getEvaluationByOrderId(Long orderId);

    /**
     * 查询所有评价
     * @return
     */
    Result<List<Evaluation>> getAllEvaluations();
}
