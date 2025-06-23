package com.example.mapper;

import com.example.entity.Knowledge;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作knowledge相关数据接口
 */
public interface KnowledgeMapper {

    /**
     * 新增
     */
    int insert(Knowledge knowledge);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Knowledge knowledge);

    /**
     * 根据ID查询
     */
    Knowledge selectById(Integer id);

    /**
     * 查询所有
     */
    List<Knowledge> selectAll(Knowledge knowledge);

    @Select("update knowledge set read_count = read_count + 1 where id = #{id} ")
    Knowledge updateCount(Integer id);
}