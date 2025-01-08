package com.kano.springbootinit.scoring;

import com.kano.springbootinit.model.entity.App;
import com.kano.springbootinit.model.entity.UserAnswer;

import java.util.List;

/**
 * 评分策略
 */
public interface ScoringStrategy {

    /**
     * 执行评分
     * @param choice
     * @param app
     * @return
     */
    UserAnswer doScoring(List<String> choice, App app);
}
