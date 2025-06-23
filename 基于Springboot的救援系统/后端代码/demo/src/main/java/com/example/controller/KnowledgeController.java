package com.example.controller;

import com.example.common.Result;
import com.example.entity.Knowledge;
import com.example.service.KnowledgeService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * null前端操作接口
 **/
@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    @Resource
    private KnowledgeService knowledgeService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Knowledge knowledge) {
        knowledgeService.add(knowledge);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        knowledgeService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        knowledgeService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Knowledge knowledge) {
        knowledgeService.updateById(knowledge);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @PutMapping("/updateCount/{id}")
    public Result updateCount(@PathVariable Integer id) {
        Knowledge knowledge = knowledgeService.updateCount(id);
        return Result.success(knowledge);
    }


    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Knowledge knowledge = knowledgeService.selectById(id);
        return Result.success(knowledge);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Knowledge knowledge) {
        List<Knowledge> list = knowledgeService.selectAll(knowledge);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Knowledge knowledge,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Knowledge> page = knowledgeService.selectPage(knowledge, pageNum, pageSize);
        return Result.success(page);
    }

}