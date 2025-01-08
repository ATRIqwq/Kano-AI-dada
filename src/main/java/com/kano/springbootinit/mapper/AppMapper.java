package com.kano.springbootinit.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kano.springbootinit.model.entity.App;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86136
* @description 针对表【app(应用)】的数据库操作Mapper
* @createDate 2025-01-06 11:04:07
* @Entity generator.domain.App
*/
@Mapper
public interface AppMapper extends BaseMapper<App> {

}




