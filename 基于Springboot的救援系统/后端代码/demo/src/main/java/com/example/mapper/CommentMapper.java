package com.example.mapper;

import com.example.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作comment相关数据接口
 */
public interface CommentMapper {

    /**
     * 新增
     */
    int insert(Comment comment);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Comment comment);

    /**
     * 根据ID查询
     */
    Comment selectById(Integer id);

    /**
     * 查询所有
     */
    List<Comment> selectAll(Comment comment);



    List<Comment> selectByRootId(Integer rootId);

    List<Comment> selectRoot();
    Integer selectCount();

    @Select("select * from comment where pid = #{pid}")
    List<Comment> selectByPid(Integer pid);

    int CompleteDeleteById(Integer id);
}