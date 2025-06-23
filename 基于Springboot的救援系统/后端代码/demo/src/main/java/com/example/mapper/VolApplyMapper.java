package com.example.mapper;

import com.example.entity.VolApply;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作vol_apply相关数据接口
 */
public interface VolApplyMapper {

    /**
     * 新增
     */
    int insert(VolApply volApply);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(VolApply volApply);

    /**
     * 根据ID查询
     */
    VolApply selectById(Integer id);

    /**
     * 查询所有
     */
    List<VolApply> selectAll(VolApply volApply);


    @Select("select * from vol_apply where username = #{username}")
    VolApply selectByUsername(String username);

    @Select("select * from vol_apply where user_id = #{userId}")
    VolApply selectByUserId(Integer userId);
}