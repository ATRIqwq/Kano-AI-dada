package com.kano.springbootinit;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kano.springbootinit.model.entity.UserAnswer;
import com.kano.springbootinit.service.UserAnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;


@SpringBootTest
public class UserAnswerShardingTest {

    @Resource
    private UserAnswerService userAnswerService;

    @Test
    public void shardingTest(){

        UserAnswer userAnswer1 = new UserAnswer();

        userAnswer1.setAppId(1L);
        userAnswer1.setUserId(1L);
        userAnswer1.setChoices("1");
        userAnswerService.save(userAnswer1);

        UserAnswer userAnswer2 = new UserAnswer();
        userAnswer2.setAppId(2L);
        userAnswer2.setUserId(1L);
        userAnswer2.setChoices("2");
        userAnswerService.save(userAnswer2);

        List<UserAnswer> list = userAnswerService.list(Wrappers.lambdaQuery(UserAnswer.class).eq(UserAnswer::getAppId, 1L));
        System.out.println(JSONUtil.toJsonStr(list));

//        UserAnswer two = userAnswerService.getOne(Wrappers.lambdaQuery(UserAnswer.class).eq(UserAnswer::getAppId, 2L));
//        System.out.println(JSONUtil.toJsonStr(two));


    }

}
