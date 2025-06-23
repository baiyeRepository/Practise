package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Donate;
import com.example.mapper.DonateMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 物资捐赠业务处理
 **/
@Service
public class DonateService {

    @Resource
    private DonateMapper donateMapper;

    /**
     * 新增
     */
    public void add(Donate donate) {
        Account currentUser = TokenUtils.getCurrentUser();
        donate.setUserId(currentUser.getId());
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 使用指定的格式解析日期字符串
        LocalDate date = LocalDate.parse(DateUtil.today(), formatter);
        donate.setTime(date);
        donate.setStatus("待审核");

        donateMapper.insert(donate);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        donateMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            donateMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Donate donate) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            donate.setStatus("待审核");
        }
        donateMapper.updateById(donate);
    }

    /**
     * 根据ID查询
     */
    public Donate selectById(Integer id) {
        return donateMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Donate> selectAll(Donate donate) {
        return donateMapper.selectAll(donate);
    }

    /**
     * 分页查询
     */
    public PageInfo<Donate> selectPage(Donate donate, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            donate.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Donate> list = donateMapper.selectAll(donate);
        return PageInfo.of(list);
    }
    /**
     * 根据物资类型查询
     */
    public List<Donate> selectByType(String type) {
        return donateMapper.selectByType(type);
    }
}