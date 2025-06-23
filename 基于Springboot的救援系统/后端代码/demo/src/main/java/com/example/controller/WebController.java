package com.example.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.common.ValidateCodeProperties;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.service.*;
import com.example.utils.ValidateCodeCache;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础前端接口
 */
@RestController
public class WebController {

    @Resource
    private AdminService adminService;

    @Resource
    private UserService userService;

    @Resource
    private VolunteerService volunteerService;


    @Resource
    private HelpService helpService;

    @Resource
    private DonateService donateService;

    @Resource
    private CommentService commentService;

    @GetMapping("/")
    public Result hello() {
        return Result.success("访问成功");
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {

        if (ObjectUtil.isEmpty(account.getUsername()) || ObjectUtil.isEmpty(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }

        boolean validate = ValidateCodeCache.validateCode(account.getKey(), account.getCode());
        if (!validate) {
            return Result.error("500", "验证码错误");
        }
        ValidateCodeCache.removeCache(account.getKey(), account.getCode());

        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            account = adminService.login(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            account = userService.login(account);
        } else if (RoleEnum.VOLUNTEER.name().equals(account.getRole())) {
            account = volunteerService.login(account);
        } else {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        return Result.success(account);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.register(account);
        } else {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR);
        }
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getNewPassword())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        } else if (RoleEnum.VOLUNTEER.name().equals(account.getRole())) {
            volunteerService.updatePassword(account);
        }
        return Result.success();
    }


    /****
     * 获取图形验证码
     * @param request
     * @param response
     */
    @GetMapping("/validateCode")
    public void getValidateCode(HttpServletRequest request, HttpServletResponse response) {
        // 生成验证码
        ValidateCodeProperties code = new ValidateCodeProperties();
        setHeader(response, code.getType());
        Captcha captcha = createCaptcha(code);
        // 存储验证码到缓存
        ValidateCodeCache.setCache(request.getParameter("key"), captcha.text().toLowerCase());
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("500", "验证码验证错误");
        }
    }

    /**
     * 创建验证码
     */
    private Captcha createCaptcha(ValidateCodeProperties code) {
        Captcha captcha = null;
        if ("gif".equalsIgnoreCase(code.getType())) {
            captcha = new GifCaptcha(code.getWidth(), code.getHeight(), code.getLength());
        } else {
            captcha = new SpecCaptcha(code.getWidth(), code.getHeight(), code.getLength());
        }
        captcha.setCharType(code.getCharType());

        return captcha;
    }

    /**
     * 设置验证码返回头
     */
    private void setHeader(HttpServletResponse response, String type) {
        if ("gif".equalsIgnoreCase(type)) {
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
        } else {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
    }


    @GetMapping("/count")
    public Result getCount() {
        List<Help> helps = helpService.selectAll(null);//救援事件
        List<Volunteer> volunteers = volunteerService.selectAll(null);//救助员
        List<Donate> donates = donateService.selectAll(null);//物资数量
        List<Comment> comments = commentService.selectAll(null);//评论
//        Dict dict = Dict.create().set("helpCount", helps.size())
//                .set("volCount", volunteers.size())
//                .set("donateCount", donates.size())
//                .set("commentCount", comments.size());
//        return Result.success(dict);

        Dict dict = Dict.create().set("helpCount", helps.size())
                .set("volCount", volunteers.size())
                .set("donateCount", donates.size())
                .set("commentCount", comments.size());

        return Result.success(dict);
    }

    @GetMapping("/pieData")
    public Result pieData() {   // 数据格式 [{"name": "wwuw", "value": 123}, {"name": "wwuw", "value": 123}]
//        List<Dict> list = new ArrayList<>();
//        List<Donate> donateList = donateService.selectAll(null);
//        List<String> typeList = donateList.stream().map(Donate::getType).distinct().collect(Collectors.toList());
//        for (String type : typeList) {
//            long count = donateList.stream().filter(donate -> donate.getType().equals(type)).count();
//            Dict dict = Dict.create().set("name", type).set("value", count);
//            list.add(dict);
//        }
//        return Result.success(list);

        List<Dict> list = new ArrayList<>();
        List<Donate> donateList = donateService.selectAll(null);
        List<String> typeList = donateList.stream().map(Donate::getType).distinct().collect(Collectors.toList());
        for (String type : typeList) {
            long count = donateList.stream().filter(v -> v.getType().equals(type)).count();
            Dict dict = Dict.create().set("name", type).set("value", count);
            list.add(dict);
        }
        return Result.success(list);
    }

    @GetMapping("/barData")
    public Result barData() {   // 数据格式 [{"name": "wwuw", "value": 123}, {"name": "wwuw", "value": 123}]
//        List<Dict> list = new ArrayList<>();
//        List<Help> helpList = helpService.selectAll(null);
//        List<String> typeList = helpList.stream().map(Help::getType).distinct().collect(Collectors.toList());
//        for (String type : typeList) {
//            long count = helpList.stream().filter(help -> help.getType().equals(type)).count();
//            Dict dict = Dict.create().set("name", type).set("value", count);
//            list.add(dict);
//        }
//        return Result.success(list);

        List<Object> list = new ArrayList<>();
        List<Help> helpList = helpService.selectAll(null);
        List<String> collectList = helpList.stream().map(Help::getType).distinct().collect(Collectors.toList());
        for (String type : collectList) {
            long count = helpList.stream().filter(v -> v.getType().equals(type)).count();
            Dict dict = Dict.create().set("name", type).set("value", count);
            list.add(dict);
        }
        return Result.success(list);
    }
}
