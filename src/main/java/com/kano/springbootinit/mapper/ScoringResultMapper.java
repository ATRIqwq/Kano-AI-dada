package com.kano.springbootinit.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kano.springbootinit.model.entity.ScoringResult;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86136
* @description 针对表【scoring_result(评分结果)】的数据库操作Mapper
* @createDate 2025-01-06 11:04:07
* @Entity generator.domain.ScoringResult
*/
@Mapper
public interface ScoringResultMapper extends BaseMapper<ScoringResult> {

}




