package com.qg.dorm.service.impl;

import com.qg.dorm.common.Result;
import com.qg.dorm.entity.Evaluation;
import com.qg.dorm.entity.RepairOrder;
import com.qg.dorm.mapper.EvaluationMapper;
import com.qg.dorm.mapper.RepairOrderMapper;
import com.qg.dorm.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private RepairOrderMapper repairOrderMapper;

    @Override
    @Transactional
    public Result<Void> createEvaluation(Long orderId, Long userId, Integer rating, String content) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("报修单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error("无权评价");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setOrderId(orderId);
        evaluation.setUserId(userId);
        evaluation.setRating(rating);
        evaluation.setContent(content);

        evaluationMapper.insert(evaluation);
        return Result.success();
    }

    @Override
    public Result<Evaluation> getEvaluationByOrderId(Long orderId) {
        Evaluation evaluation = evaluationMapper.selectByOrderId(orderId);
        return Result.success(evaluation);
    }

    @Override
    public Result<List<Evaluation>> getAllEvaluations() {
        List<Evaluation> evaluations = evaluationMapper.selectAll();
        return Result.success(evaluations);
    }
}
