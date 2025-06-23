package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Volunteer;
import com.example.exception.CustomException;
import com.example.mapper.VolunteerMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 志愿者信息业务处理
 **/
@Service
public class VolunteerService {

    @Resource
    private VolunteerMapper volunteerMapper;


    /**
     * 新增
     */
    public void add(Volunteer volunteer) {
        Volunteer dbVolunteer = volunteerMapper.selectByUsername(volunteer.getUsername());
        if (ObjectUtil.isNotEmpty(dbVolunteer)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(volunteer.getName())) {
            volunteer.setName(volunteer.getUsername());
        }
        volunteer.setFree("是");
        volunteer.setRole(RoleEnum.VOLUNTEER.name());
        volunteerMapper.insert(volunteer);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        volunteerMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            volunteerMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Volunteer volunteer) {
        volunteerMapper.updateById(volunteer);
    }

    /**
     * 根据ID查询
     */
    public Volunteer selectById(Integer id) {
        return volunteerMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Volunteer> selectAll(Volunteer volunteer) {
        return volunteerMapper.selectAll(volunteer);
    }

    /**
     * 分页查询
     */
    public PageInfo<Volunteer> selectPage(Volunteer volunteer, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Volunteer> list = volunteerMapper.selectAll(volunteer);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Volunteer dbvolunteer = volunteerMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbvolunteer)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbvolunteer.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbvolunteer.getId() + "-" + RoleEnum.VOLUNTEER.name();
        String token = TokenUtils.createToken(tokenData, dbvolunteer.getPassword());
        dbvolunteer.setToken(token);
        return dbvolunteer;
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Volunteer dbvolunteer = volunteerMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbvolunteer)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbvolunteer.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbvolunteer.setPassword(account.getNewPassword());
        volunteerMapper.updateById(dbvolunteer);
    }
    /**
     * 根据用户名查询志愿者 ID
     * @param username 用户名
     * @return 志愿者 ID
     */
    public Integer getIdByUsername(String username) {
        return volunteerMapper.getIdByUsername(username);
    }

}