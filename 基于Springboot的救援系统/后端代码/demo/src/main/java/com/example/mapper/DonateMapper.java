package com.example.mapper;

import com.example.entity.Donate;

import java.util.List;

/**
 * 操作donate相关数据接口
 */
public interface DonateMapper {

    /**
     * 新增
     */
    int insert(Donate donate);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Donate donate);

    /**
     * 根据ID查询
     */
    Donate selectById(Integer id);

    /**
     * 查询所有
     */
    List<Donate> selectAll(Donate donate);

    /**
     * 根据物资类型查询
     */
    List<Donate> selectByType(String type);
}