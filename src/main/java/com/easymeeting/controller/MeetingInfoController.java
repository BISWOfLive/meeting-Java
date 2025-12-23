package com.easymeeting.controller;


import com.easymeeting.annotition.GlobalInterceptor;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.po.MeetingInfo;
import com.easymeeting.entity.query.MeetingInfoQuery;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.vo.ResponseVO;
import com.easymeeting.service.MeetingInfoService;
import com.easymeeting.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@RestController
@RequestMapping("/meeting")
@Validated
@Slf4j
public class MeetingInfoController extends ABaseController {
    @Resource
    private MeetingInfoService meetingInfoService;


    @RequestMapping("/getCurrentMeeting")
    @GlobalInterceptor
    public ResponseVO getCurrentMeeting() {
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadMeeting")
    @GlobalInterceptor
    public ResponseVO loadMeeting(Integer pageNo) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        MeetingInfoQuery meetingInfoQuery = new MeetingInfoQuery();
        meetingInfoQuery.setCreateUserId(tokenUserInfo.getUserId());
        meetingInfoQuery.setPageNo(pageNo);
        meetingInfoQuery.setOrderBy("m.create_time desc");
        meetingInfoQuery.setQueryMemberCount(true);
        PaginationResultVO resultVO = this.meetingInfoService.findListByPage(meetingInfoQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/quickMeeting")
    @GlobalInterceptor
    public ResponseVO quickMeeting(@NotNull Integer meetingNoType,
                                   @NotEmpty @Size(max = 100) String meetingName,
                                   @NotNull Integer joinType,
                                   @Size(max = 5) String joinPassword) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        if (tokenUserInfoDto.getCurrentMeetingId() != null){
            throw new RuntimeException("你有未结束的会议,无法创建新的会议");
        }
        MeetingInfo meetingInfo = new MeetingInfo();
        meetingInfo.setMeetingName(meetingName);
        meetingInfo.setMeetingNo(meetingNoType == 0 ? tokenUserInfoDto.getMyMeetingNo() : StringTools.getMeetingNoOrMeetingId());
        meetingInfo.setJoinType(joinType);
        meetingInfo.setJoinPassword(joinPassword);
        meetingInfo.setCreateUserId(tokenUserInfoDto.getUserId());
        meetingInfoService.quickMeeting(meetingInfo,tokenUserInfoDto.getNickName());

        tokenUserInfoDto.setCurrentMeetingId(meetingInfo.getMeetingId());
        tokenUserInfoDto.setCurrentNickName(tokenUserInfoDto.getNickName());
        resetTokenUserInfo(tokenUserInfoDto);
        return getSuccessResponseVO(meetingInfo.getMeetingId());
    }


    @RequestMapping("/preJoinMeeting")
    @GlobalInterceptor
    public ResponseVO preJoinMeeting(@NotNull String meetingNo, @NotEmpty  String nickName, String joinPassword) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        meetingNo = meetingNo.replace(" ", "");
        tokenUserInfoDto.setCurrentNickName(nickName);
        String meetingId = meetingInfoService.preJoinMeeting(meetingNo, tokenUserInfoDto, joinPassword);
        return getSuccessResponseVO(meetingId);
    }



    @RequestMapping("/joinMeeting")
    @GlobalInterceptor
    public ResponseVO joinMeeting(@NotNull Boolean videoOpen) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        meetingInfoService.joinMeeting(tokenUserInfoDto.getCurrentMeetingId(),tokenUserInfoDto.getUserId(),tokenUserInfoDto.getNickName(),tokenUserInfoDto.getSex(),videoOpen);
        return getSuccessResponseVO(tokenUserInfoDto);
    }
}