package com.example.controller;

import com.example.common.Result;
import com.example.entity.Help;
import com.example.service.HelpService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 救援信息前端操作接口
 **/
@RestController
@RequestMapping("/help")
public class HelpController {

    @Resource
    private HelpService helpService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Help help) {
        helpService.add(help);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        helpService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        helpService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Help help) {
        helpService.updateById(help);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Help help = helpService.selectById(id);
        return Result.success(help);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Help help) {
        List<Help> list = helpService.selectAll(help);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Help help,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(value = " timeParam", required = false) String timeStr,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        // 手动处理日期转换
        if (timeStr != null && !timeStr.isEmpty()) {
            try {
                // 将 "yyyy-MM-dd" 格式的字符串转为 LocalDateTime（时间部分设为 00:00:00）
                LocalDate date = LocalDate.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                help.setTime(date.atStartOfDay());
            } catch (DateTimeParseException e) {
                // 可返回明确错误信息，例如：
                return Result.error();
            }
        }

        PageInfo<Help> page = helpService.selectPage(help, pageNum, pageSize);
        return Result.success(page);
    }

}