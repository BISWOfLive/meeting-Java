package com.easymeeting.controller;

import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.DateTimePatternEnum;
import com.easymeeting.entity.enums.MeetingReserveStatusEnum;
import com.easymeeting.entity.po.MeetingReserve;
import com.easymeeting.entity.query.MeetingReserveQuery;
import com.easymeeting.entity.vo.ResponseVO;
import com.easymeeting.mappers.MeetingReserveMapper;
import com.easymeeting.service.MeetingReserveService;
import com.easymeeting.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/meetingReserve")
@Validated
@Slf4j
public class MeetingReserveController extends ABaseController {

    @Resource
    private MeetingReserveService meetingReserveService;
    @Autowired
    private MeetingReserveMapper meetingReserveMapper;



    @RequestMapping("/createMeetingReserve")
    public ResponseVO createMeetingReserve(MeetingReserve meetingReserve) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        meetingReserve.setCreateUserId(tokenUserInfo.getUserId());
        meetingReserveService.createMeetingReserve(meetingReserve);
        // 返回预约对象（含 meetingNo），前端拿到会议号后可通过 preJoinMeeting 加入会议
        return getSuccessResponseVO(meetingReserve);
    }


    //创建人删除预约会议,会议直接消失
    //被邀请人删除预约会议,会议看不到
    @RequestMapping("/delMeetingReserve")
    public ResponseVO delMeetingReserve(@NotEmpty String meetingId) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        meetingReserveService.delMeetingReserve(meetingId, tokenUserInfo.getUserId());
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delMeetingReserveByUser")
    public ResponseVO delMeetingReserveByUser(@NotEmpty String meetingId) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        meetingReserveService.delMeetingReserveByUser(meetingId, tokenUserInfo.getUserId());
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadMeetingReserve")
    public ResponseVO loadMeetingReserve() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        MeetingReserveQuery query = new MeetingReserveQuery();
        query.setUserId(tokenUserInfoDto.getUserId());
        query.setOrderBy("start_time desc");
        query.setStatus(MeetingReserveStatusEnum.NO_START.getStatus());
        query.setQueryUserInfo(true);
        return getSuccessResponseVO(meetingReserveService.findListByPage(query));
    }

    /**
     * 加载今日会议
     * 查询当前用户今天的未开始会议，并按开始时间升序排列返回
     * @return ResponseVO 包含今日会议列表的响应对象
     */
    @RequestMapping("/loadTodayMeeting")
    public ResponseVO loadTodayMeeting() {
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        
        // 构建查询条件
        MeetingReserveQuery meetingReserveQuery = new MeetingReserveQuery();
        String curDate = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        meetingReserveQuery.setUserId(tokenUserInfo.getUserId());
        meetingReserveQuery.setStartTimeStart(curDate);
        meetingReserveQuery.setStartTimeEnd(curDate);
        meetingReserveQuery.setStatus(MeetingReserveStatusEnum.NO_START.getStatus());
        meetingReserveQuery.setOrderBy("start_time asc");
        meetingReserveQuery.setQueryUserInfo(true);

        // 查询今日会议列表
        return getSuccessResponseVO(meetingReserveService.findListByParam(meetingReserveQuery));
    }
}
