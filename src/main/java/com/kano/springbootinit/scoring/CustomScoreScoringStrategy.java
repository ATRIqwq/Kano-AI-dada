package com.kano.springbootinit.scoring;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ScoringStrategyConfig(appType = 0,scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;
    @Override
    public UserAnswer doScoring(List<String> choice, App app) {
        // 1. 根据 id 查询到题目和题目结果信息
        Long appId = app.getId();
        // 1. 根据 id 查询到题目和题目结果信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, appId)
        );
        // 2. 统计用户每个选择对应的分数，得出总分
        String questionContent = question.getQuestionContent();
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContentDTOS = questionVO.getQuestionContent();

        int totalScore = 0;

//        for (QuestionContentDTO questionContentDTO : questionContentDTOS) {
//            for (String answer : choice) {
//                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
//                    if (option.getKey().equals(answer)) {
//                        totalScore = totalScore + option.getScore();
//                    }
//                }
//            }
//        }

        for (int i = 0; i < questionContentDTOS.size(); i++) {
            Map<String, Integer> resultMap = questionContentDTOS.get(i).getOptions().stream().collect(Collectors.toMap(QuestionContentDTO.Option::getKey, QuestionContentDTO.Option::getScore));
            Integer score = Optional.ofNullable(resultMap.get(choice.get(i))).orElse(0);
            totalScore =  score + totalScore;
        }

        // 3. 遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        ScoringResult maxScoringResult = scoringResultList.get(0);

        for (ScoringResult scoringResult : scoringResultList) {
            Integer resultScoreRange = scoringResult.getResultScoreRange();
            if (totalScore >= resultScoreRange) {
                maxScoringResult = scoringResult;
                break;
            }
        }

        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choice));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());

        return userAnswer;


    }
}
