package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Help;
import com.example.entity.Records;
import com.example.mapper.HelpMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 救援信息业务处理
 **/
@Service
public class HelpService {

    @Resource
    private HelpMapper helpMapper;

    @Resource
    private RecordsService recordsService;

    /**
     * 新增
     */
    public void add(Help help) {
        Account currentUser = TokenUtils.getCurrentUser();
        help.setUserId(currentUser.getId());
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 使用指定的格式解析日期时间字符串
        LocalDateTime dateTime = LocalDateTime.parse(DateUtil.now(), formatter);
        help.setTime(dateTime);
        help.setStatus("待处理");
        helpMapper.insert(help);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        helpMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            helpMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Help help) {
        helpMapper.updateById(help);
    }

    /**
     * 根据ID查询
     */
    public Help selectById(Integer id) {
        return helpMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Help> selectAll(Help help) {
        return helpMapper.selectAll(help);
    }

    /**
     * 分页查询
     */
    public PageInfo<Help> selectPage(Help help, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            help.setUserId(currentUser.getId());
        }
        LocalDateTime time = help.getTime();
        if (time != null) {
            // 格式化为 "yyyy-MM-dd 00:00:00" 字符串
            LocalDateTime dateTimeStr = LocalDateTime.parse(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00")));
            // 设置模糊查询条件（假设Help类有一个timePattern字段）
            help.setTime(dateTimeStr );
        }


        PageHelper.startPage(pageNum, pageSize);
        List<Help> list = helpMapper.selectAll(help);
        for (Help h : list) {
            Records records = recordsService.selectById(h.getRecordId());
            h.setRecords(records);
            // 根据记录状态加载出勤记录信息
            if (records != null) {
                if ("救援中".equals(records.getStatus()) || "已救援".equals(records.getStatus())) {
                    // 当状态为救援中或已救援时，显示出勤时间和电话
                    h.setRecords(records);
                } else if ("待救援".equals(records.getStatus())) {
                    // 当状态为待救援时，只显示出勤人员
                    Records partialRecords = new Records();
                    partialRecords.setVolName(records.getVolName());
                    h.setRecords(partialRecords);
                }
            }
        }
        return PageInfo.of(list);
    }



}