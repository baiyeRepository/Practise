package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Help;
import com.example.entity.Records;
import com.example.entity.Volunteer;
import com.example.mapper.HelpMapper;
import com.example.mapper.RecordsMapper;
import com.example.mapper.VolunteerMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * 出警信息业务处理
 **/
@Service
public class RecordsService {
    private static final Logger logger = Logger.getLogger(RecordsService.class.getName());

    @Resource
    private RecordsMapper recordsMapper;

    @Resource
    private VolunteerMapper volunteerMapper;

    @Resource
    private HelpMapper helpMapper;

    /**
     * 新增
     */
    @Transactional
    public void add(Records records) {
        logger.log(Level.INFO, "开始新增出警记录，记录信息: {}", records);
        //修改状态
        records.setStatus("待救援");
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 使用指定的格式解析日期时间字符串
        LocalDateTime dateTime = LocalDateTime.parse(DateUtil.now(), formatter);
        records.setTime(dateTime);
        recordsMapper.insert(records);

        //更新救助员的状态
        Volunteer volunteer = volunteerMapper.selectById(records.getVolId());
        volunteer.setFree("否");
        volunteerMapper.updateById(volunteer);

        //更新救援信息
        Help help = helpMapper.selectById(records.getHelpId());
        help.setStatus("待出勤");


        help.setRecordId(records.getId());
        helpMapper.updateById(help);
        helpMapper.updateStatus("待出勤",records.getHelpId());
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        recordsMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            recordsMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Records records) {
        // 判断救援事件是否存在
        Help help = helpMapper.selectById(records.getHelpId());
        if (help == null) {
            throw new IllegalArgumentException("未找到对应的救援信息，事件ID: " + records.getHelpId());
        }

        if ("救援中".equals(records.getStatus())) {
            // 定义日期时间格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 使用指定的格式解析日期时间字符串
            LocalDateTime dateTime = LocalDateTime.parse(DateUtil.now(), formatter);

            records.setTime(dateTime);
            // 更新Help信息的状态为进行中
            helpMapper.updateStatus("进行中", records.getHelpId());
        }
        if ("已救援".equals(records.getStatus())) {
            Integer volId = records.getVolId();
            Volunteer volunteer = volunteerMapper.selectById(volId);
            volunteer.setFree("是");
            volunteerMapper.updateById(volunteer);

            helpMapper.updateStatus("已解决", records.getHelpId());
        }

        recordsMapper.updateById(records);
    }

    /**
     * 根据ID查询
     */
    public Records selectById(Integer id) {
        return recordsMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Records> selectAll(Records records) {
        return recordsMapper.selectAll(records);
    }

    /**
     * 分页查询
     */
    public PageInfo<Records> selectPage(Records records, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.VOLUNTEER.name().equals(currentUser.getRole())) {
            records.setVolId(currentUser.getId());
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Records> list = recordsMapper.selectAll(records);
        return PageInfo.of(list);
    }
}