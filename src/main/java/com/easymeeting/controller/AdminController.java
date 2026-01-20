package com.easymeeting.controller;

import com.easymeeting.annotition.GlobalInterceptor;
import com.easymeeting.entity.query.UserInfoQuery;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.vo.ResponseVO;
import com.easymeeting.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin")
@Validated
@Slf4j
public class AdminController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;

    @RequestMapping("/loadUserList")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadUserList(String nickNameFuzzy, Integer pageNo, Integer pageSize) {
        UserInfoQuery userInfoQuery = new UserInfoQuery();
        userInfoQuery.setNickNameFuzzy(nickNameFuzzy);
        userInfoQuery.setPageNo(pageNo);
        userInfoQuery.setPageSize(pageSize);
        PaginationResultVO resultvo = this.userInfoService.findListByPage(userInfoQuery);
        return getSuccessResponseVO(resultvo);
    }


    @RequestMapping("/updateUserStatus")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO updateUserStatus(@NotNull Integer status, @NotEmpty String userId) {
        this.userInfoService.updateUserStatus(status, userId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/forceOffLine")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO forceOffLine(@NotEmpty String userId) {
        this.userInfoService.forceOffLine(userId);
        return getSuccessResponseVO(null);
    }
}
