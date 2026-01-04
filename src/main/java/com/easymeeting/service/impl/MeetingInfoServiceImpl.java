package com.easymeeting.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import com.easymeeting.entity.dto.*;
import com.easymeeting.entity.enums.*;
import com.easymeeting.entity.po.MeetingMember;
import com.easymeeting.entity.query.MeetingMemberQuery;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.mappers.MeetingMemberMapper;
import com.easymeeting.redis.RedisComponent;
import com.easymeeting.utils.JsonUtils;
import com.easymeeting.websocket.ChannelContextUtils;
import com.easymeeting.websocket.message.MessageHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import com.easymeeting.entity.query.MeetingInfoQuery;
import com.easymeeting.entity.po.MeetingInfo;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.query.SimplePage;
import com.easymeeting.mappers.MeetingInfoMapper;
import com.easymeeting.service.MeetingInfoService;
import com.easymeeting.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 业务接口实现
 */
@Service("meetingInfoService")
public class MeetingInfoServiceImpl implements MeetingInfoService {

    @Resource
    private ChannelContextUtils channelContextUtils;

    @Resource
    private MeetingInfoMapper<MeetingInfo, MeetingInfoQuery> meetingInfoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private MessageHandler messageHandler;

    @Resource
    private MeetingMemberMapper<MeetingMember, MeetingMemberQuery> meetingMemberMapper;


    /**
     * 根据条件查询列表
     */
    @Override
    public List<MeetingInfo> findListByParam(MeetingInfoQuery param) {
        return this.meetingInfoMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(MeetingInfoQuery param) {
        return this.meetingInfoMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<MeetingInfo> findListByPage(MeetingInfoQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<MeetingInfo> list = this.findListByParam(param);
        PaginationResultVO<MeetingInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(MeetingInfo bean) {
        return this.meetingInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<MeetingInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.meetingInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<MeetingInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.meetingInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(MeetingInfo bean, MeetingInfoQuery param) {
        StringTools.checkParam(param);
        return this.meetingInfoMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(MeetingInfoQuery param) {
        StringTools.checkParam(param);
        return this.meetingInfoMapper.deleteByParam(param);
    }

    /**
     * 根据MeetingId获取对象
     */
    @Override
    public MeetingInfo getMeetingInfoByMeetingId(String meetingId) {
        return this.meetingInfoMapper.selectByMeetingId(meetingId);
    }

    /**
     * 根据MeetingId修改
     */
    @Override
    public Integer updateMeetingInfoByMeetingId(MeetingInfo bean, String meetingId) {
        return this.meetingInfoMapper.updateByMeetingId(bean, meetingId);
    }

    /**
     * 根据MeetingId删除
     */
    @Override
    public Integer deleteMeetingInfoByMeetingId(String meetingId) {
        return this.meetingInfoMapper.deleteByMeetingId(meetingId);
    }

    @Override
    public void quickMeeting(MeetingInfo meetingInfo, String nickName) {
        Date date = new Date();
        meetingInfo.setCreateTime(date);
        meetingInfo.setMeetingId(StringTools.getMeetingNoOrMeetingId());
        meetingInfo.setStartTime(date);
        meetingInfo.setStatus(MeetingStatusEnum.RUNING.getStatus());
        this.meetingInfoMapper.insert(meetingInfo);
    }


    private void addMeetingMember(String meetingId, String userId, String nickName, Integer meetingType) {
        MeetingMember meetingMember = new MeetingMember();
        meetingMember.setMeetingId(meetingId);
        meetingMember.setUserId(userId);
        meetingMember.setNickName(nickName);
        meetingMember.setLastJoinTime(new Date());
        meetingMember.setStatus(MeetingMemberStatusEnum.NORMAL.getStatus());
        meetingMember.setMemberType(meetingType);
        meetingMember.setMeetingStatus(MeetingStatusEnum.RUNING.getStatus());
        meetingMemberMapper.insertOrUpdate(meetingMember);

    }

    private void add2Meeting(String meetingId, String userId, String nickName, Integer sex, Boolean videoOpen, Integer meetingType) {

        MeetingMemberDto meetingMemberDto = new MeetingMemberDto();
        meetingMemberDto.setUserid(userId);
        meetingMemberDto.setNickName(nickName);
        meetingMemberDto.setStatus(MeetingMemberStatusEnum.NORMAL.getStatus());
        meetingMemberDto.setJoinTime(System.currentTimeMillis());
        meetingMemberDto.setOpenVideo(videoOpen);
        meetingMemberDto.setSex(sex);
        meetingMemberDto.setMemberType(meetingType);
        redisComponent.add2Meeting(meetingId, meetingMemberDto);
    }

    @Override
    public void joinMeeting(String meetingId, String userId, String nickName, Integer sex, Boolean videoOpen) {
        if (StringTools.isEmpty(meetingId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        MeetingInfo meetingInfo = this.meetingInfoMapper.selectByMeetingId(meetingId);
        if (meetingInfo == null || MeetingStatusEnum.FINISHEN.getStatus().equals(meetingInfo.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        this.checkMeetingJoin(meetingId, userId);
        MemberTypeEnum memberTypeEnum = meetingInfo.getCreateUserId().equals(userId) ? MemberTypeEnum.COMPERE : MemberTypeEnum.NORMAL;
        this.addMeetingMember(meetingId, userId, nickName, memberTypeEnum.getType());
        this.add2Meeting(meetingId, userId, nickName, sex, videoOpen, memberTypeEnum.getType());
        channelContextUtils.addMeetingRoom(meetingId, userId);

        MeetingJoinDto meetingJoinDto = new MeetingJoinDto();
        meetingJoinDto.setNewMember(redisComponent.getMeetingMember(meetingId, userId));
        // 过滤掉当前用户，避免重复显示
        List<MeetingMemberDto> meetingMemberList = redisComponent.getMeetingMemberList(meetingId);
        List<MeetingMemberDto> filteredMeetingMemberList = meetingMemberList.stream()
                .filter(item -> !userId.equals(item.getUserid()))
                .collect(Collectors.toList());
        meetingJoinDto.setMeetingMemberList(filteredMeetingMemberList);

        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.ADD_MEETING_ROOM.getType());
        messageSendDto.setMessageContent(meetingJoinDto);
        messageSendDto.setMeetingId(meetingId);
        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.GROUP.getType());
        messageHandler.sendMessage(messageSendDto);
    }

    @Override
    public String preJoinMeeting(String meetingNo, TokenUserInfoDto tokenUserInfoDto, String joinPassword) {
        String userId = tokenUserInfoDto.getUserId();
        MeetingInfoQuery meetingInfoQuery = new MeetingInfoQuery();
        meetingInfoQuery.setMeetingNo(meetingNo);
        meetingInfoQuery.setStatus(MeetingStatusEnum.RUNING.getStatus());
        meetingInfoQuery.setOrderBy("create_time desc");
        List<MeetingInfo> meetingInfoList = meetingInfoMapper.selectList(meetingInfoQuery);
        if (meetingInfoList.isEmpty()) {
            throw new BusinessException("404");
        }
        MeetingInfo meetingInfo = meetingInfoList.get(0);
        if (!MeetingStatusEnum.RUNING.getStatus().equals(meetingInfo.getStatus())) {
            throw new BusinessException("会议已结束");
        }
        if (!StringTools.isEmpty(tokenUserInfoDto.getCurrentMeetingId()) && !meetingInfo.getMeetingId().equals(tokenUserInfoDto.getCurrentMeetingId())) {
            throw new BusinessException("你有未结束的会议,无法加入其他会议");
        }
        checkMeetingJoin(meetingInfo.getMeetingId(), userId);

        if (MeetingJoinTypeEnum.PASSWORD.getType().equals(meetingInfo.getJoinType()) && !meetingInfo.getJoinPassword().equals(joinPassword)) {
            throw new BusinessException("密码错误");
        }
        tokenUserInfoDto.setCurrentMeetingId(meetingInfo.getMeetingId());
        redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
        return meetingInfo.getMeetingId();
    }

    private void checkMeetingJoin(String meetingId, String userId) {
        MeetingMemberDto meetingMemberDto = redisComponent.getMeetingMember(meetingId, userId);
        if (meetingMemberDto != null && MeetingMemberStatusEnum.BLACKLIST.getStatus().equals(meetingMemberDto.getStatus())) {
            throw new BusinessException("你已经被拉黑无法进入会议");
        }
    }

    @Override
    public void exitMeetingRoom(TokenUserInfoDto tokenUserInfoDto, MeetingMemberStatusEnum statusEnum) {
        String meetingId = tokenUserInfoDto.getCurrentMeetingId();
        if (StringTools.isEmpty(meetingId)) return;
        String userId = tokenUserInfoDto.getUserId();
        Boolean exit = redisComponent.exitMeeting(meetingId, userId, statusEnum);
        if (!exit) {
            tokenUserInfoDto.setCurrentMeetingId(null);
            redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
            return;
        }
        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.EXIT_MEETING_ROOM.getType());

        List<MeetingMemberDto> meetingMemberDtoList = redisComponent.getMeetingMemberList(meetingId);
        MeetingExitDto exitDto = new MeetingExitDto();
        exitDto.setMeetingMemberList(meetingMemberDtoList);
        exitDto.setExitUserId(userId);
        exitDto.setExitStatus(statusEnum.getStatus());

        messageSendDto.setMessageContent(JsonUtils.convertObj2Json(exitDto));
        messageSendDto.setMeetingId(meetingId);
        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.GROUP.getType());
        messageHandler.sendMessage(messageSendDto);

        List<MeetingMemberDto> onLineMemberList =
                meetingMemberDtoList.stream().filter(item -> MeetingMemberStatusEnum.NORMAL.getStatus().equals(item.getStatus())).collect(Collectors.toList());

        if (onLineMemberList.isEmpty()) {
            finishMeeting(meetingId,tokenUserInfoDto.getUserId());
            return;
        }
        if (ArrayUtils.contains(new Integer[]{MeetingMemberStatusEnum.KICK_OUT.getStatus(), MeetingMemberStatusEnum.BLACKLIST.getStatus()}, statusEnum.getStatus())) {
            MeetingMember meetingMember = new MeetingMember();
            meetingMember.setStatus(statusEnum.getStatus());
            meetingMemberMapper.updateByMeetingIdAndUserId(meetingMember, meetingId, userId);
        }
    }

    @Override
    public void forceExitMeeting(TokenUserInfoDto tokenUserInfoDto, String userId, MeetingMemberStatusEnum statusEnum) {
        MeetingInfo meetingInfo = this.meetingInfoMapper.selectByMeetingId(tokenUserInfoDto.getCurrentMeetingId());
        if (!tokenUserInfoDto.getCurrentMeetingId().equals(meetingInfo.getCreateUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        TokenUserInfoDto userInfoDto = this.redisComponent.getTokenUserInfoDtoByUserId(userId);
        exitMeetingRoom(userInfoDto,statusEnum);
    }

    @Override
    //Spring 的事务管理注解
    @Transactional(rollbackFor = Exception.class)
    //使用 Exception.class 而不是更具体的异常类，是为了确保：
    //不仅运行时异常会触发回滚
    //检查异常（checked exception）也会触发回滚
    //提供更全面的事务保护
    public void finishMeeting(String meetingId, String userId) {
        MeetingInfo meetingInfo = this.meetingInfoMapper.selectByMeetingId(meetingId);
        if (userId != null && !meetingInfo.getCreateUserId().equals(userId)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        MeetingInfo updataInfo = new MeetingInfo();
        updataInfo.setStatus(MeetingStatusEnum.FINISHEN.getStatus());
        updataInfo.setEndTime(new Date());
        meetingInfoMapper.updateByMeetingId(updataInfo,meetingId);

        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.GROUP.getType());
        messageSendDto.setStatus(MeetingStatusEnum.FINISHEN.getStatus());
        messageSendDto.setMessageId(meetingId);
        messageHandler.sendMessage(messageSendDto);

        //TODO预约会议状态

        List<MeetingMemberDto> meetingMemberList = redisComponent.getMeetingMemberList(meetingId);
        for (MeetingMemberDto meetingMemberDto : meetingMemberList) {
            TokenUserInfoDto userInfoDto = this.redisComponent.getTokenUserInfoDtoByUserId(meetingMemberDto.getUserid());
            userInfoDto.setCurrentMeetingId(null);
            redisComponent.saveTokenUserInfoDto(userInfoDto);
        }
        redisComponent.removeAllMeetingMember(meetingId);
    }
}