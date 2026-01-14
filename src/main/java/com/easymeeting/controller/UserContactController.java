package com.easymeeting.controller;


import com.easymeeting.annotition.GlobalInterceptor;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.UserContactApplyStatusEnum;
import com.easymeeting.entity.enums.UserContactStatusEnum;
import com.easymeeting.entity.po.UserContact;
import com.easymeeting.entity.po.UserContactApply;
import com.easymeeting.entity.query.UserContactApplyQuery;
import com.easymeeting.entity.query.UserContactQuery;
import com.easymeeting.entity.vo.ResponseVO;
import com.easymeeting.entity.vo.UserInfoVO4Search;
import com.easymeeting.service.MeetingInfoService;
import com.easymeeting.service.UserContactApplyService;
import com.easymeeting.service.UserContactService;
import com.easymeeting.service.impl.MeetingInfoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/userContact")
@Validated
@Slf4j
public class UserContactController extends ABaseController{

    @Resource
    private UserContactService userContactService;

    @Resource
    private UserContactApplyService userContactApplyService;
    @Autowired
    private MeetingInfoServiceImpl meetingInfoService;


    @RequestMapping("/searchContact")
    @GlobalInterceptor
    public ResponseVO searchContact(@NotEmpty String userId) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        UserInfoVO4Search userInfoVO4Search = userContactService.searchContact(tokenUserInfo.getUserId(), userId);
        return getSuccessResponseVO(userInfoVO4Search);
    }

    @RequestMapping("/contactApply")
    @GlobalInterceptor
    public ResponseVO contactApply(@NotEmpty String receiveUserId) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        UserContactApply userContactApply = new UserContactApply();
        userContactApply.setApplyUserId(tokenUserInfo.getUserId());
        userContactApply.setReceiveUserId(receiveUserId);
        Integer status = userContactApplyService.saveUserContactApply(userContactApply);
        return getSuccessResponseVO(status);
    }

    @RequestMapping("/dealWithApply")
    @GlobalInterceptor
    public ResponseVO dealWithApply(@NotEmpty String applyUserId, @NotNull Integer status) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        userContactApplyService.dealWithApply(applyUserId,tokenUserInfo.getUserId(),tokenUserInfo.getNickName(),status);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadContactUser")
    @GlobalInterceptor
    public ResponseVO loadContactUser() {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        UserContactQuery contactQuery = new UserContactQuery();
        contactQuery.setUserId(tokenUserInfo.getUserId());
        contactQuery.setQueryUserInfo(true);
        contactQuery.setOrderBy("last_update_time desc");
        contactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
        List<UserContact> userContactList = userContactService.findListByParam(contactQuery);
        return getSuccessResponseVO(userContactList);
    }

    @RequestMapping("/loadContactApply")
    @GlobalInterceptor
    public ResponseVO loadContactApply() {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        UserContactApplyQuery applyQuery = new UserContactApplyQuery();
        applyQuery.setReceiveUserId(tokenUserInfo.getUserId());
        applyQuery.setQueryUserInfo(true);
        applyQuery.setOrderBy("last_apply_time desc");
        List<UserContactApply> applyList = userContactApplyService.findListByParam(applyQuery);
        return getSuccessResponseVO(applyList);
    }

    @RequestMapping("/delContact")
    @GlobalInterceptor
    public ResponseVO delContact(@NotEmpty String contactId, @NotNull Integer status) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        userContactApplyService.delCount(tokenUserInfo.getUserId(),contactId,status);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadContactApplyDealWithCount")
    @GlobalInterceptor
    public ResponseVO loadContactApplyDealWithCount() {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        UserContactApplyQuery applyQuery = new UserContactApplyQuery();
        applyQuery.setReceiveUserId(tokenUserInfo.getUserId());
        applyQuery.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
        Integer count = userContactApplyService.findCountByParam(applyQuery);
        return getSuccessResponseVO(count);
    }
}
