package com.example.controller;

import com.example.common.Result;
import com.example.entity.Donate;
import com.example.service.DonateService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 物资捐赠前端操作接口
 **/
@RestController
@RequestMapping("/donate")
public class DonateController {

    @Resource
    private DonateService donateService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Donate donate) {
        System.out.println(donate);
        donateService.add(donate);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        donateService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        donateService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Donate donate) {
        donateService.updateById(donate);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Donate donate = donateService.selectById(id);
        return Result.success(donate);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Donate donate) {
        List<Donate> list = donateService.selectAll(donate);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Donate donate,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Donate> page = donateService.selectPage(donate, pageNum, pageSize);
        return Result.success(page);
    }
    /**
     * 根据物资类型查询
     */
    @GetMapping("/selectByType")
    public Result selectByType(@RequestParam String type) {
        List<Donate> list = donateService.selectByType(type);
        return Result.success(list);
    }
}