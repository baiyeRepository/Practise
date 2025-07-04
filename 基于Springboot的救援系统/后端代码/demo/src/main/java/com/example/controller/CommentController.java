package com.example.controller;

import com.example.common.Result;
import com.example.entity.Comment;
import com.example.service.CommentService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论表前端操作接口
 **/
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Comment comment) {
        commentService.add(comment);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        commentService.deleteById(id);
        return Result.success();
    }

    /**
     * 彻底删除**/
    @DeleteMapping("/CompleteDelete/{id}")
    public Result  CompleteDeleteById(@PathVariable Integer id) {
        commentService.CompleteDeleteById(id);
        return Result.success();
    }


    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        commentService.deleteBatch(ids);
        return Result.success();
    }
    /**
     * 批量删除
     */
    @DeleteMapping("/CompleteDelete/batch")
    public Result CompleteDeleteBatch(@RequestBody List<Integer> ids) {
        commentService.CompleteDeleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Comment comment) {
        commentService.updateById(comment);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Comment comment = commentService.selectById(id);
        return Result.success(comment);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Comment comment) {
        List<Comment> list = commentService.selectAll(comment);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Comment comment,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Comment> page = commentService.selectPage(comment, pageNum, pageSize);
        return Result.success(page);
    }


    @GetMapping("/selectTree")
    public Result selectTree(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<Comment> pageInfo = commentService.selectTree(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/selectCount")
    public Result selectCount() {
        Integer count = commentService.selectCount();
        return Result.success(count);
    }
  /**递归删除*/
    @DeleteMapping("/deleteDeep/{id}")
    public Result deleteDepp(@PathVariable Integer id) {
        commentService.deleteDeep(id);
        return Result.success();
    }

}