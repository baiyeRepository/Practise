package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.entity.VolApply;
import com.example.entity.Volunteer;
import com.example.exception.CustomException;
import com.example.mapper.VolApplyMapper;
import com.example.mapper.VolunteerMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 救助员申请业务处理
 **/
@Service
public class VolApplyService {

    @Resource
    private VolApplyMapper volApplyMapper;
    @Resource
    VolunteerMapper volunteerMapper;
    @Resource
    UserService userService;

    /**
     * 新增
     */
    public void add(VolApply volApply) {
        Account currentUser = TokenUtils.getCurrentUser();
        VolApply dbVolApply = volApplyMapper.selectByUserId(currentUser.getId());
        if (ObjectUtil.isNotEmpty(dbVolApply)) {
            if ("通过".equals(dbVolApply.getStatus()) || "待审核".equals(dbVolApply.getStatus())) {
                throw new CustomException(ResultCodeEnum.VOL_APPLY_EXIST_ERROR);
            } else {
                this.deleteById(dbVolApply.getId());  // 先删除拒绝的提交申请
            }
        }
        volApply.setUserId(currentUser.getId());
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 使用指定的格式解析日期时间字符串
        LocalDateTime dateTime = LocalDateTime.parse(DateUtil.now(), formatter);
        volApply.setTime(dateTime);
        volApply.setStatus("待审核");
        volApplyMapper.insert(volApply);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        volApplyMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            volApplyMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(VolApply volApply) {
        volApplyMapper.updateById(volApply);
    }

    /**
     * 根据ID查询
     */
    public VolApply selectById(Integer id) {
        return volApplyMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<VolApply> selectAll(VolApply volApply) {
        return volApplyMapper.selectAll(volApply);
    }

    /**
     * 分页查询
     */
    public PageInfo<VolApply> selectPage(VolApply volApply, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<VolApply> list = volApplyMapper.selectAll(volApply);
        return PageInfo.of(list);
    }

    public void audit(VolApply volApply) {
        String status = volApply.getStatus();
        if ("通过".equals(status)) {
            // 复制一分信息出来创建一个救助员的角色
            User user = userService.selectById(volApply.getUserId());
            if (ObjectUtil.isEmpty(user)) {
                return;
            }
            String username = user.getUsername();
            // 通过用户名查询 救助员是否存在 如果存在那就不创建
            Volunteer volunteer = volunteerMapper.selectByUsername(username);
            if (ObjectUtil.isNotEmpty(volunteer)) {
                throw new CustomException(ResultCodeEnum.VOL_EXIST_ERROR);
            }

            volunteer = new Volunteer();
            volunteer.setUsername(username);
            volunteer.setPassword(user.getPassword());
            volunteer.setRole(RoleEnum.VOLUNTEER.name());
            volunteer.setName(user.getName());
            volunteer.setAvatar(user.getAvatar());
            volunteer.setInfo(volApply.getInfo());
            volunteer.setBirth(volApply.getBirth());
            volunteer.setSex(volApply.getSex());
            volunteer.setCardImg(volApply.getCardImg());
            volunteer.setCardNo(volApply.getCardNo());
            volunteer.setPhone(volApply.getPhone());
            volunteer.setFree("是");
            volunteerMapper.insert(volunteer);
        }
        this.updateById(volApply);
    }

    public VolApply selectByUserId(Integer userId) {
        return volApplyMapper.selectByUserId(userId);
    }

}