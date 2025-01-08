package com.kano.springbootinit.scoring;
import java.util.Date;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kano.springbootinit.model.dto.question.QuestionContentDTO;
import com.kano.springbootinit.model.entity.App;
import com.kano.springbootinit.model.entity.Question;
import com.kano.springbootinit.model.entity.ScoringResult;
import com.kano.springbootinit.model.entity.UserAnswer;
import com.kano.springbootinit.model.vo.QuestionVO;
import com.kano.springbootinit.service.QuestionService;
import com.kano.springbootinit.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@ScoringStrategyConfig(appType = 1,scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScoring(List<String> choice, App app) {
        Long appId = app.getId();
        // 1. 根据 id 查询到题目和题目结果信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, appId)
        );

        // 2. 统计用户每个选择对应的属性个数，如 I = 10 个，E = 5 个
        HashMap<String, Integer> optionCount = new HashMap<>();

        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        // 遍历questionContent列表中的每一个QuestionContentDTO对象
        for (QuestionContentDTO questionContentDTO : questionContent) {
            // 遍历choice列表中的每一个答案字符串
            for (String answer : choice) {
                // 获取当前QuestionContentDTO对象的所有选项
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    // 检查当前选项的键是否与答案匹配
                    if (option.getKey().equals(answer)) {
                        // 获取当前选项的结果
                        String result = option.getResult();
                        // 如果choiceCountMap中不包含当前结果，则初始化为0
                        if (!optionCount.containsKey(result)){
                            optionCount.put(result, 0);
                        }
                        // 将当前结果的计数加1
                        optionCount.put(result, optionCount.get(result) + 1);
                    }
                }
            }
        }

        // 3. 遍历每种评分结果，计算哪个结果的得分更高
        int maxScore = 0;
        ScoringResult maxScoreResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            int score = resultProp.stream().mapToInt(prop -> optionCount.getOrDefault(prop, 0)).sum();
            if (score > maxScore) {
                maxScore = score;
                maxScoreResult = scoringResult;
            }
        }

        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choice));
        userAnswer.setResultId(maxScoreResult.getId());
        userAnswer.setResultName(maxScoreResult.getResultName());
        userAnswer.setResultDesc(maxScoreResult.getResultDesc());
        userAnswer.setResultPicture(maxScoreResult.getResultPicture());

        return userAnswer;

    }
}
