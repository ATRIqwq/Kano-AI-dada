package com.kano.springbootinit.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kano.springbootinit.model.entity.UserAnswer;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86136
* @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
* @createDate 2025-01-06 11:04:07
* @Entity generator.domain.UserAnswer
*/
@Mapper
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {

}




