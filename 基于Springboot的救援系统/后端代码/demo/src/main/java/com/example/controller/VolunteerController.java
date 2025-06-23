package com.example.controller;

import com.example.common.Result;
import com.example.entity.Volunteer;
import com.example.service.VolunteerService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 志愿者信息前端操作接口
 **/
@RestController
@RequestMapping("/volunteer")
public class VolunteerController {

    @Resource
    private VolunteerService volunteerService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Volunteer volunteer) {
        volunteerService.add(volunteer);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        volunteerService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        volunteerService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Volunteer volunteer) {
        volunteerService.updateById(volunteer);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Volunteer volunteer = volunteerService.selectById(id);
        return Result.success(volunteer);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Volunteer volunteer) {
        List<Volunteer> list = volunteerService.selectAll(volunteer);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Volunteer volunteer,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Volunteer> page = volunteerService.selectPage(volunteer, pageNum, pageSize);
        return Result.success(page);
    }

}