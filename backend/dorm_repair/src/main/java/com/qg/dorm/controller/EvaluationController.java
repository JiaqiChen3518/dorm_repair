package com.qg.dorm.controller;

import com.qg.dorm.common.Result;
import com.qg.dorm.entity.Evaluation;
import com.qg.dorm.service.EvaluationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @PostMapping("/create")
    public Result<Void> createEvaluation(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Integer rating = (Integer) params.get("rating");
        String content = (String) params.get("content");

        return evaluationService.createEvaluation(orderId, userId, rating, content);
    }

    @GetMapping("/getByOrder/{orderId}")
    public Result<Evaluation> getEvaluationByOrderId(@PathVariable Long orderId) {
        return evaluationService.getEvaluationByOrderId(orderId);
    }

    @GetMapping("/all")
    public Result<List<Evaluation>> getAllEvaluations(HttpServletRequest request) {
        return evaluationService.getAllEvaluations();
    }
}
