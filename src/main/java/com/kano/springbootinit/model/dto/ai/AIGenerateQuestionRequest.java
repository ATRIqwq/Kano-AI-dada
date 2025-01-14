package com.kano.springbootinit.model.dto.ai;

import lombok.Data;

import java.io.Serializable;

/**
 * AI生成题目请求
 */
@Data
public class AIGenerateQuestionRequest implements Serializable {

    private Long appId;

    /**
     * 生产题目数
     */
    int questionNumber = 10;

    /**
     * 每题选项数
     */
    int optionNumber = 2;


    private static final long serialVersionUID = 1L;

}
