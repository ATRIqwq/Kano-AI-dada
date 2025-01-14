package com.kano.springbootinit.scoring;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kano.springbootinit.common.ErrorCode;
import com.kano.springbootinit.exception.ThrowUtils;
import com.kano.springbootinit.manager.AiManager;
import com.kano.springbootinit.model.dto.ai.QuestionAnswerDTO;
import com.kano.springbootinit.model.dto.question.QuestionContentDTO;
import com.kano.springbootinit.model.entity.App;
import com.kano.springbootinit.model.entity.Question;
import com.kano.springbootinit.model.entity.ScoringResult;
import com.kano.springbootinit.model.entity.UserAnswer;
import com.kano.springbootinit.model.enums.AppTypeEnum;
import com.kano.springbootinit.model.vo.QuestionVO;
import com.kano.springbootinit.service.AppService;
import com.kano.springbootinit.service.QuestionService;
import com.kano.springbootinit.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@ScoringStrategyConfig(appType = 1,scoringStrategy = 1)
public class AITestScoringStrategy implements ScoringStrategy {

    @Resource
    private AppService appService;

    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    /**
     * AI 评分系统消息
     */
    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";

    @Override
    public UserAnswer doScoring(List<String> choices, App app) {
        Long appId = app.getId();

        // 获取应用信息
        App appInfo = appService.getById(appId);
        ThrowUtils.throwIf(ObjUtil.isEmpty(appInfo), ErrorCode.PARAMS_ERROR);

        // 获取题目信息
        Question question = questionService.getOne(Wrappers.<Question>lambdaQuery().eq(Question::getAppId, appId));
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        // 封装prompt
        String userMessage = getAiTestScoringUserMessage(app,choices,questionContent);

        // 调用AI生成问题
        String result = aiManager.doSynStableRequest(userMessage, AI_TEST_SCORING_SYSTEM_MESSAGE);

        // 生成结果处理
        int start = result.indexOf("{");
        int end = result.lastIndexOf("}");
        String json = result.substring(start, end + 1);

        //构造返回值，填充对象属性
        UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        return userAnswer;
    }

    /**
     * 拼接用户消息
     * @param app
     * @param choices
     * @param questionContentDTOList
     * @return
     */
    private String getAiTestScoringUserMessage(App app,List<String> choices,List<QuestionContentDTO> questionContentDTOList){
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            String title = questionContentDTOList.get(i).getTitle();
            String choice = choices.get(i);
            //根据选项值设置答案
            List<QuestionContentDTO.Option> options = questionContentDTOList.get(i).getOptions();
            for (QuestionContentDTO.Option option : options) {
                if (option.getKey().equals(choice)) {
                    questionAnswerDTO.setAnswer(option.getValue());
                    break;
                }
            }
            questionAnswerDTO.setTitle(title);
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }



}
