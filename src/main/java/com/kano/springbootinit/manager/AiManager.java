package com.kano.springbootinit.manager;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class AiManager {

    @Resource
    private ClientV4 clientV4;

    private final float STABLE_TEMPERATURE = 0.05f;

    private final float UNSTABLE_TEMPERATURE = 0.99f;

    /**
     *  通用请求方法
     * @param messages 消息列表
     * @param stream 是否流式输出（一个字一个字返回）
     * @param temperature 随机数（控制生成信息的随机性）
     * @return
     */
    public String doRequest(List<ChatMessage> messages,Boolean stream,Float temperature){
        // 构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4Plus)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        // 调用
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        ChatMessage message = invokeModelApiResp.getData().getChoices().get(0).getMessage();
        return message.getContent().toString();
    }

    /**
     * 简化消息参数
     * @param userMessage
     * @param systemMessage
     * @param stream
     * @param temperature
     * @return
     */
    public String doRequest(String userMessage,String systemMessage,Boolean stream,Float temperature){

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(systemChatMessage);
        messages.add(userChatMessage);
        return doRequest(messages,stream,temperature);
    }

    /**
     * 同步调用
     */
    public String doSynRequest(String userMessage,String systemMessage,Float temperature){
        return doRequest(userMessage,systemMessage,Boolean.FALSE,temperature);
    }


    /**
     * 同步调用答案稳定
     */
    public String doSynStableRequest(String userMessage,String systemMessage){
        return doRequest(userMessage,systemMessage,Boolean.FALSE,STABLE_TEMPERATURE);
    }

    /**
     * 同步调用答案比较随机
     */
    public String doSynUnStableRequest(String userMessage,String systemMessage){
        return doRequest(userMessage,systemMessage,Boolean.FALSE,UNSTABLE_TEMPERATURE);
    }


}


