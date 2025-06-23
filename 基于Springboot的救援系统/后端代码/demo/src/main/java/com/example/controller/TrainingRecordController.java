package com.example.controller;

import com.example.common.Result;
import com.example.entity.TrainingRecord;
import com.example.service.TrainingRecordService;
import com.example.service.VolunteerService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName com.example.controller.TrainingRecordController
 * @Date: 2025/2/13 0:43
 * @User 17900
 */
@RestController
@RequestMapping("/trainingrecords")
public class TrainingRecordController {
    @Autowired
    private TrainingRecordService trainingRecordService;
    @Autowired
    private VolunteerService volunteerService;

    /**
     * 根据用户名查询志愿者 ID
     * @param username 用户名
     * @return 包含查询结果的 Result
     */
    @GetMapping("/getIdByUsername/{username}")
    public Result getIdByUsername(@PathVariable String username) {
        Integer volunteerId = volunteerService.getIdByUsername(username);
        if (volunteerId != null) {
            return Result.success(volunteerId);
        }
        return Result.error("1","未找到");
    }

    /**
     * 新增培训记录
     */
    @PostMapping("/add")
    public Result add(@RequestBody TrainingRecord trainingRecord) {
        trainingRecordService.add(trainingRecord);
        return Result.success();
    }

    /**
     * 删除培训记录
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        trainingRecordService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除培训记录
     */
    @PostMapping("/delete-batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        trainingRecordService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改培训记录
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody TrainingRecord trainingRecord) {
        trainingRecordService.updateById(trainingRecord);
        return Result.success();
    }

    /**
     * 根据 ID 查询培训记录
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        TrainingRecord trainingRecord = trainingRecordService.selectById(id);
        return Result.success(trainingRecord);
    }

    /**
     * 分页查询培训记录
     */
    @GetMapping("/page")
    public Result selectPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(required = false)  String trainingTopic) {
        PageInfo<TrainingRecord> pageInfo = trainingRecordService.selectPage(pageNum, pageSize,  trainingTopic);
        return Result.success(pageInfo);
    }
}