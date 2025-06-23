package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Comment;
import com.example.mapper.CommentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.tools.javac.parser.Tokens;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 评论表业务处理
 **/
@Service
public class CommentService {

    @Resource
    private CommentMapper commentMapper;

    /**
     * 新增
     */
    public void add(Comment comment) {
        // 根据role值处理userId和adminId，不区分大小写
        if ("admin".equalsIgnoreCase(comment.getRole())) {
            comment.setAdminId(comment.getUserId());
            comment.setUserId(null);
        } else if ("user".equalsIgnoreCase(comment.getRole())) {
            comment.setAdminId(null);
        }

        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 使用指定的格式解析日期时间字符串
        LocalDateTime dateTime = LocalDateTime.parse(DateUtil.now(), formatter);
        comment.setTime(dateTime);
        commentMapper.insert(comment);

        Integer id = comment.getId();
        // 更新root_id
        if (comment.getPid() == null) {
            comment.setRootId(id);
        } else {
            Comment parentComment = commentMapper.selectById(comment.getPid());
            comment.setRootId(parentComment.getRootId());
        }
        this.updateById(comment);
    }
    /**
     * 根据id清空评论
     */
    public void deleteById(Integer id) {
    /*    this.deleteDeep(id);*/
        commentMapper.deleteById(id);
    }
    /**
     * 彻底删除
     */
    public void CompleteDeleteById(Integer id) {
        /*    this.deleteDeep(id);*/
        commentMapper.CompleteDeleteById(id);
    }


    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentMapper.deleteById(id);
        }
    }
    /**
     * 批量彻底删除
     */
    public void CompleteDeleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentMapper.CompleteDeleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }

    /**
     * 根据ID查询
     */
    public Comment selectById(Integer id) {
        return commentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    /**
     * 分页查询
     */
    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = commentMapper.selectAll(comment);
        return PageInfo.of(list);
    }

    public PageInfo<Comment> selectTree(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> rootList = commentMapper.selectRoot(); // 直接查询根评论
        PageInfo<Comment> pageInfo = PageInfo.of(rootList);
        for (Comment root : rootList) {
            List<Comment> children = commentMapper.selectByRootId(root.getRootId());
            root.setChildren(children);
        }
        return pageInfo;
    }

    public Integer selectCount() {
        return commentMapper.selectCount(); // 统计所有根评论数量
    }
    /**
     * 递归删除
     */
    public void deleteDeep(Integer id) {
        this.deepDelete(id);
    }

    private void deepDelete(Integer pid) {
        List<Comment> children = commentMapper.selectByPid(pid);
        commentMapper.deleteById(pid);
        for (Comment child : children) {
            this.deepDelete(child.getId());
        }
    }

}