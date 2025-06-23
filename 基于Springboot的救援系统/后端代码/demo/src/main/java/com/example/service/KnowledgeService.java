package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Knowledge;
import com.example.mapper.KnowledgeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * null业务处理
 **/
@Service
public class KnowledgeService {

    @Resource
    private KnowledgeMapper knowledgeMapper;

    /**
     * 新增
     */
    public void add(Knowledge knowledge) {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 使用指定的格式解析日期字符串
        LocalDate date = LocalDate.parse(LocalDate.now().toString(), formatter);

        knowledge.setDate(date);
        knowledgeMapper.insert(knowledge);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        knowledgeMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            knowledgeMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Knowledge knowledge) {
        knowledgeMapper.updateById(knowledge);
    }

    /**
     * 根据ID查询
     */
    public Knowledge selectById(Integer id) {
        return knowledgeMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Knowledge> selectAll(Knowledge knowledge) {
        return knowledgeMapper.selectAll(knowledge);
    }

    /**
     * 分页查询
     */
    public PageInfo<Knowledge> selectPage(Knowledge knowledge, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Knowledge> list = knowledgeMapper.selectAll(knowledge);
        return PageInfo.of(list);
    }

    /**
     * 阅读次数
     */
    public Knowledge updateCount(Integer id) {
        return knowledgeMapper.updateCount(id);
    }
}