package com.easymeeting.controller;


import com.easymeeting.annotition.GlobalInterceptor;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.MeetingMemberStatusEnum;
import com.easymeeting.entity.enums.MeetingStatusEnum;
import com.easymeeting.entity.enums.ResponseCodeEnum;
import com.easymeeting.entity.po.MeetingInfo;
import com.easymeeting.entity.po.MeetingMember;
import com.easymeeting.entity.query.MeetingInfoQuery;
import com.easymeeting.entity.query.MeetingMemberQuery;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.vo.ResponseVO;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.service.MeetingInfoService;
import com.easymeeting.service.impl.MeetingMemberServiceImpl;
import com.easymeeting.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;


@RestController
@RequestMapping("/meeting")
@Validated
@Slf4j
public class MeetingInfoController extends ABaseController {
    @Resource
    private MeetingInfoService meetingInfoService;
    @Autowired
    private MeetingMemberServiceImpl meetingMemberService;

    /**
     * 加载会议列表
     * 分页获取当前用户创建的会议列表
     *
     * @param pageNo 页码，用于分页查询
     * @return ResponseVO 返回分页的会议列表结果
     */
    @RequestMapping("/loadMeeting")
    @GlobalInterceptor
    public ResponseVO loadMeeting(Integer pageNo) {
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        // 创建会议查询对象并设置查询条件
        MeetingInfoQuery meetingInfoQuery = new MeetingInfoQuery();
        meetingInfoQuery.setCreateUserId(tokenUserInfo.getUserId());
        meetingInfoQuery.setPageNo(pageNo);
        meetingInfoQuery.setOrderBy("m.create_time desc");
        meetingInfoQuery.setQueryMemberCount(true);
        // 执行分页查询
        PaginationResultVO resultVO = this.meetingInfoService.findListByPage(meetingInfoQuery);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * 快速创建会议
     * 根据指定参数快速创建一个新会议
     *
     * @param meetingNoType 会议号类型，0表示使用用户自己的会议号，其他值表示生成新的会议号
     * @param meetingName 会议名称，不能为空且最大长度为100
     * @param joinType 加入类型，不能为空
     * @param joinPassword 加入密码，最大长度为5
     * @return ResponseVO 返回新创建会议的ID
     * @throws BusinessException 当用户已有未结束的会议时抛出异常
     */
    @RequestMapping("/quickMeeting")
    @GlobalInterceptor
    public ResponseVO quickMeeting(@NotNull Integer meetingNoType,
                                   @NotEmpty @Size(max = 100) String meetingName,
                                   @NotNull Integer joinType,
                                   @Size(max = 5) String joinPassword) {
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 检查用户是否已有正在进行的会议
        if (tokenUserInfoDto.getCurrentMeetingId() != null){
            throw new BusinessException("你有未结束的会议,无法创建新的会议");
        }
        // 创建会议对象并设置会议信息
        MeetingInfo meetingInfo = new MeetingInfo();
        meetingInfo.setMeetingName(meetingName);
        meetingInfo.setMeetingNo(meetingNoType == 0 ? tokenUserInfoDto.getMyMeetingNo() : StringTools.getMeetingNoOrMeetingId());
        meetingInfo.setJoinType(joinType);
        meetingInfo.setJoinPassword(joinPassword);
        meetingInfo.setCreateUserId(tokenUserInfoDto.getUserId());
        // 执行快速会议创建
        meetingInfoService.quickMeeting(meetingInfo,tokenUserInfoDto.getNickName());

        // 更新用户当前会议信息
        tokenUserInfoDto.setCurrentMeetingId(meetingInfo.getMeetingId());
        tokenUserInfoDto.setCurrentNickName(tokenUserInfoDto.getNickName());
        resetTokenUserInfo(tokenUserInfoDto);
        return getSuccessResponseVO(meetingInfo.getMeetingId());
    }


    /**
     * 预加入会议
     * 预先加入指定会议号的会议
     *
     * @param meetingNo 会议号，不能为空
     * @param nickName 昵称，不能为空
     * @param joinPassword 加入密码
     * @return ResponseVO 返回加入会议的会议ID
     */
    @RequestMapping("/preJoinMeeting")
    @GlobalInterceptor
    public ResponseVO preJoinMeeting(@NotNull String meetingNo, @NotEmpty  String nickName, String joinPassword) {
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 清理会议号中的空格字符
        meetingNo = meetingNo.replace(" ", "");
        // 设置用户当前昵称
        tokenUserInfoDto.setCurrentNickName(nickName);
        // 执行预加入会议操作
        String meetingId = meetingInfoService.preJoinMeeting(meetingNo, tokenUserInfoDto, joinPassword);
        return getSuccessResponseVO(meetingId);
    }

    /**
     * 加入会议
     * 正式加入当前用户的会议
     *
     * @param videoOpen 是否开启视频，不能为空
     * @return ResponseVO 返回用户信息
     */
    @RequestMapping("/joinMeeting")
    @GlobalInterceptor
    public ResponseVO joinMeeting(@NotNull Boolean videoOpen){
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 执行加入会议操作
        meetingInfoService.joinMeeting(tokenUserInfoDto.getCurrentMeetingId(),tokenUserInfoDto.getUserId(),tokenUserInfoDto.getNickName(),tokenUserInfoDto.getSex(),videoOpen);
        return getSuccessResponseVO(tokenUserInfoDto);
    }

    /**
     * 退出会议
     * 使当前用户退出会议
     *
     * @return ResponseVO 返回操作结果
     */
    @RequestMapping("/exitMeeting")
    @GlobalInterceptor
    public ResponseVO exitMeeting(){
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 执行退出会议操作
        meetingInfoService.exitMeetingRoom(tokenUserInfoDto,MeetingMemberStatusEnum.EXIT_MEETING);
        return getSuccessResponseVO(null);
    }

    /**
     * 踢出会议
     * 将指定用户强制踢出会议
     *
     * @param userId 用户ID，不能为空
     * @return ResponseVO 返回操作结果
     */
    @RequestMapping("/kickOutMeeting")
    @GlobalInterceptor
    public ResponseVO kickOutMeeting(@NotEmpty String userId){
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 执行强制踢出会议操作
        meetingInfoService.forceExitMeeting(tokenUserInfoDto,userId,MeetingMemberStatusEnum.KICK_OUT);
        return getSuccessResponseVO(null);
    }

    /**
     * 拉黑会议
     * 将指定用户加入黑名单并踢出会议
     *
     * @param userId 用户ID，不能为空
     * @return ResponseVO 返回操作结果
     */
    @RequestMapping("/blackMeeting")
    @GlobalInterceptor
    public ResponseVO blackMeeting(@NotEmpty String userId){
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 执行拉黑并强制退出会议操作
        meetingInfoService.forceExitMeeting(tokenUserInfoDto,userId,MeetingMemberStatusEnum.BLACKLIST);
        return getSuccessResponseVO(null);
    }

    /**
     * 获取当前会议
     * 获取当前用户正在参与的会议信息
     *
     * @return ResponseVO 返回当前会议信息，如果无当前会议或会议已结束则返回null
     */
    @RequestMapping("/getCurrentMeeting")
    @GlobalInterceptor
    public ResponseVO getCurrentMeeting(){
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 检查是否有当前会议
        if (StringTools.isEmpty(tokenUserInfoDto.getCurrentMeetingId())){
            return getSuccessResponseVO(null);
        }
        // 查询会议信息
        MeetingInfo meetingInfo = this.meetingInfoService.getMeetingInfoByMeetingId(tokenUserInfoDto.getCurrentMeetingId());
        // 检查会议是否已结束
        if (MeetingStatusEnum.FINISHEN.getStatus().equals(meetingInfo.getStatus())){
            return getSuccessResponseVO(null);
        }
        return getSuccessResponseVO(meetingInfo);
    }

    /**
     * 结束会议
     * 结束当前用户创建的会议
     *
     * @return ResponseVO 返回操作结果
     */
    @RequestMapping("/finishMeeting")
    @GlobalInterceptor
    public ResponseVO finishMeeting(){
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 执行结束会议操作
        meetingInfoService.finishMeeting(tokenUserInfoDto.getCurrentMeetingId(),tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }

    /**
     * 删除会议记录
     * 该方法将当前用户在指定会议中的状态更新为删除会议状态
     * @param meetingId 会议ID，不能为空
     * @return ResponseVO 返回操作结果
     */
    @RequestMapping("/delMeetingRecord")
    @GlobalInterceptor
    public ResponseVO delMeetingRecord(@NotEmpty String meetingId){
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 创建会议成员对象并设置状态为删除会议
        MeetingMember meetingMember = new MeetingMember();
        meetingMember.setStatus(MeetingMemberStatusEnum.DEL_MEETING.getStatus());
        // 创建查询条件，指定会议ID和用户ID
        MeetingMemberQuery meetingMemberQuery = new MeetingMemberQuery();
        meetingMemberQuery.setMeetingId(meetingId);
        meetingMemberQuery.setUserId(tokenUserInfoDto.getUserId());
        // 根据条件更新会议成员状态
        meetingMemberService.updateByParam(meetingMember,meetingMemberQuery);
        return getSuccessResponseVO(null);
    }

    /**
     * 加载会议成员列表
     * 根据会议ID获取会议成员列表，并验证当前用户是否为会议成员
     * @param meetingId 会议ID，不能为空
     * @return ResponseVO 返回会议成员列表
     * @throws BusinessException 当用户不是会议成员时抛出异常
     */
    @RequestMapping("/loadMeetingMembers")
    @GlobalInterceptor
    public ResponseVO loadMeetingMembers(@NotEmpty String meetingId){
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 创建会议成员查询对象并设置会议ID
        MeetingMemberQuery meetingMemberQuery = new MeetingMemberQuery();
        meetingMemberQuery.setMeetingId(meetingId);
        // 查询会议成员列表
        List<MeetingMember> meetingMemberList = this.meetingMemberService.findListByParam(meetingMemberQuery);
        // 检查当前用户是否在会议成员列表中
        Optional<MeetingMember> first = meetingMemberList.stream().filter(item -> item.getUserId().equals(tokenUserInfoDto.getUserId())).findFirst();

        if (!first.isPresent()){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        return getSuccessResponseVO(meetingMemberList);
    }

    //预约加入会议
    @RequestMapping("/reserveJoinMeeting")
    @GlobalInterceptor
    public ResponseVO reserveJoinMeeting(@NotEmpty String meetingId,@NotEmpty String nickName,String joinPassword){
        // 获取当前用户信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        tokenUserInfoDto.setCurrentNickName(nickName);
        meetingInfoService.reserveJoinMeeting(meetingId,tokenUserInfoDto,joinPassword);
        return getSuccessResponseVO(null);
    }
}