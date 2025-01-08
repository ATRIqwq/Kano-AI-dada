package com.kano.springbootinit.scoring;

import com.kano.springbootinit.common.ErrorCode;
import com.kano.springbootinit.exception.BusinessException;
import com.kano.springbootinit.model.entity.App;
import com.kano.springbootinit.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
/**
 * 评分策略执行器
 */
@Service
public class ScoringStrategyExecutor {

    @Resource
    private List<ScoringStrategy> scoringStrategyList;

    /**
     * 评分
     * @param choice
     * @param app
     * @return
     * @throws Exception
     */
    public UserAnswer doScoring(List<String> choice, App app)  throws Exception{
        Integer appType = app.getAppType();
        Integer appScoringStrategy = app.getScoringStrategy();

        if (appType == null || appScoringStrategy == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }

        // 遍历策略列表，找到匹配的策略
        for (ScoringStrategy scoringStrategy : scoringStrategyList) {
            if (scoringStrategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)) {
                ScoringStrategyConfig scoringStrategyConfig = scoringStrategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                if (scoringStrategyConfig.appType() == appType && scoringStrategyConfig.scoringStrategy() == appScoringStrategy) {
                    return scoringStrategy.doScoring(choice, app);
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
    }
}
