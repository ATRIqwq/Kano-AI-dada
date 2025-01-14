package com.kano.springbootinit.model.dto.ai;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionAnswerDTO implements Serializable {

    /**
     * 题目
     */
    private String title;


    /**
     * 用户答案
     */
    private String answer;

    private static final long serialVersionUID = 1L;
}
