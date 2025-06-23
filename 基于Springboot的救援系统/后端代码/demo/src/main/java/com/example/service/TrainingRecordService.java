package com.example.service;

import com.example.entity.TrainingRecord;
import com.example.mapper.TrainingRecordMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingRecordService {
    @Autowired
    private TrainingRecordMapper trainingRecordMapper;
    /**
     * 新增
     */
    @Transactional
    public void add(TrainingRecord trainingRecord) {
        trainingRecordMapper.insert(trainingRecord);
    }
    /**
     * 删除
     */
    public void deleteById(Integer id) {
        trainingRecordMapper.deleteById(id);
    }
    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            trainingRecordMapper.deleteById(id);
        }
    }
    /**
     * 修改
     */
    public void updateById(TrainingRecord trainingRecord) {
        trainingRecordMapper.updateById(trainingRecord);
    }
    /**
     * 根据 ID 查询
     */
    public TrainingRecord selectById(Integer id) {
        return trainingRecordMapper.selectById(id);
    }
    /**
     * 分页查询
     */
    public PageInfo<TrainingRecord> selectPage(Integer pageNum, Integer pageSize, String trainingTopic) {
        PageHelper.startPage(pageNum, pageSize);
        List<TrainingRecord> list = trainingRecordMapper.selectPage(trainingTopic);
        return PageInfo.of(list);
    }

}