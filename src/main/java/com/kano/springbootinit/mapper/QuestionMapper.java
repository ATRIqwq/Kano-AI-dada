package com.kano.springbootinit.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kano.springbootinit.model.entity.Question;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86136
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2025-01-06 11:04:07
* @Entity generator.domain.Question
*/
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

}




