package com.example.controller;

import com.example.common.Result;
import com.example.entity.VolApply;
import com.example.service.VolApplyService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 救助员申请前端操作接口
 **/
@RestController
@RequestMapping("/volApply")
public class VolApplyController {

    @Resource
    private VolApplyService volApplyService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody VolApply volApply) {
        volApplyService.add(volApply);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        volApplyService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        volApplyService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody VolApply volApply) {
        volApplyService.updateById(volApply);
        return Result.success();
    }


    /**
     * 审核
     */
    @PutMapping("/audit")
    public Result audit(@RequestBody VolApply volApply) {
        volApplyService.audit(volApply);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        VolApply volApply = volApplyService.selectById(id);
        return Result.success(volApply);
    }


    /**
     * 根据UserID查询
     */
    @GetMapping("/selectByUserId/{userId}")
    public Result selectByUserId(@PathVariable Integer userId) {
        VolApply volApply = volApplyService.selectByUserId(userId);
        return Result.success(volApply);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(VolApply volApply) {
        List<VolApply> list = volApplyService.selectAll(volApply);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(VolApply volApply,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<VolApply> page = volApplyService.selectPage(volApply, pageNum, pageSize);

        return Result.success(page);
    }

}