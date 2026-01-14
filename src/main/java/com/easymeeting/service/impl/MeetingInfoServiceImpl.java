package com.easymeeting.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.easymeeting.entity.dto.*;
import com.easymeeting.entity.enums.*;
import com.easymeeting.entity.po.*;
import com.easymeeting.entity.query.*;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.mappers.*;
import com.easymeeting.redis.RedisComponent;
import com.easymeeting.utils.JsonUtils;
import com.easymeeting.websocket.ChannelContextUtils;
import com.easymeeting.websocket.message.MessageHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.easymeeting.entity.vo.PaginationResultVO;
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

    @Resource
    private MeetingReserveMapper<MeetingReserve, MeetingReserveQuery> meetingReserveMapper;

    @Resource
    private MeetingReserveMemberMapper<MeetingReserveMember, MeetingReserveMemberQuery> meetingReserveMemberMapper;
    @Autowired
    private UserContactMapper userContactMapper;

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
        meetingMemberDto.setUserId(userId);
        meetingMemberDto.setNickName(nickName);
        meetingMemberDto.setStatus(MeetingMemberStatusEnum.NORMAL.getStatus());
        meetingMemberDto.setJoinTime(System.currentTimeMillis());
        meetingMemberDto.setOpenVideo(videoOpen);
        meetingMemberDto.setSex(sex);
        meetingMemberDto.setMemberType(meetingType);
        redisComponent.add2Meeting(meetingId, meetingMemberDto);
    }

    /**
     * 加入会议处理
     * 将用户添加到指定会议中，包括验证会议状态、检查加入权限、添加成员信息、发送加入消息等
     *
     * @param meetingId 会议ID
     * @param userId    用户ID
     * @param nickName  用户昵称
     * @param sex       用户性别
     * @param videoOpen 视频是否开启
     * @throws BusinessException 业务异常，如会议ID为空、会议不存在或已结束、用户被禁止加入会议等
     */
    @Override
    public void joinMeeting(String meetingId, String userId, String nickName, Integer sex, Boolean videoOpen) {
        // 验证会议ID是否为空
        if (StringTools.isEmpty(meetingId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        MeetingInfo meetingInfo = this.meetingInfoMapper.selectByMeetingId(meetingId);
        // 验证会议是否存在或是否已结束
        if (meetingInfo == null || MeetingStatusEnum.FINISHEN.getStatus().equals(meetingInfo.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 检查用户是否被禁止加入会议
        this.checkMeetingJoin(meetingId, userId);
        // 判断用户角色（主持人或普通成员）
        MemberTypeEnum memberTypeEnum = meetingInfo.getCreateUserId().equals(userId) ? MemberTypeEnum.COMPERE : MemberTypeEnum.NORMAL;
        // 添加成员到数据库
        this.addMeetingMember(meetingId, userId, nickName, memberTypeEnum.getType());
        // 添加成员到Redis
        this.add2Meeting(meetingId, userId, nickName, sex, videoOpen, memberTypeEnum.getType());
        // 将用户添加到会议房间
        channelContextUtils.addMeetingRoom(meetingId, userId);

        MeetingJoinDto meetingJoinDto = new MeetingJoinDto();
        meetingJoinDto.setNewMember(redisComponent.getMeetingMember(meetingId, userId));
        meetingJoinDto.setMeetingMemberList(redisComponent.getMeetingMemberList(meetingId));

        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.ADD_MEETING_ROOM.getType());
        messageSendDto.setMessageContent(meetingJoinDto);
        messageSendDto.setMeetingId(meetingId);
        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.GROUP.getType());
        // 发送用户加入会议的消息
        messageHandler.sendMessage(messageSendDto);
    }

    /**
     * 预加入会议处理
     * 验证用户是否可以加入指定会议，包括会议状态、用户当前会议状态、加入密码等验证
     *
     * @param meetingNo        会议号
     * @param tokenUserInfoDto 用户信息，包含用户ID和当前会议ID等
     * @param joinPassword     加入会议的密码
     * @return 会议ID
     * @throws BusinessException 业务异常，如会议不存在、会议已结束、用户已有未结束会议、密码错误等
     */
    @Override
    public String preJoinMeeting(String meetingNo, TokenUserInfoDto tokenUserInfoDto, String joinPassword) {
        String userId = tokenUserInfoDto.getUserId();
        MeetingInfoQuery meetingInfoQuery = new MeetingInfoQuery();
        meetingInfoQuery.setMeetingNo(meetingNo);
        meetingInfoQuery.setStatus(MeetingStatusEnum.RUNING.getStatus());
        meetingInfoQuery.setOrderBy("create_time desc");
        List<MeetingInfo> meetingInfoList = meetingInfoMapper.selectList(meetingInfoQuery);
        // 验证会议是否存在
        if (meetingInfoList.isEmpty()) {
            throw new BusinessException("404");
        }
        MeetingInfo meetingInfo = meetingInfoList.get(0);
        // 验证会议是否仍在进行中
        if (!MeetingStatusEnum.RUNING.getStatus().equals(meetingInfo.getStatus())) {
            throw new BusinessException("会议已结束");
        }
        // 验证用户是否已在其他会议中
        if (!StringTools.isEmpty(tokenUserInfoDto.getCurrentMeetingId()) && !meetingInfo.getMeetingId().equals(tokenUserInfoDto.getCurrentMeetingId())) {
            throw new BusinessException("你有未结束的会议,无法加入其他会议");
        }
        // 检查用户是否被禁止加入会议
        checkMeetingJoin(meetingInfo.getMeetingId(), userId);

        // 验证会议加入密码
        if (MeetingJoinTypeEnum.PASSWORD.getType().equals(meetingInfo.getJoinType()) && !meetingInfo.getJoinPassword().equals(joinPassword)) {
            throw new BusinessException("密码错误");
        }
        // 更新用户当前会议ID并保存到Redis
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

    /**
     * 退出会议室处理
     * 处理用户退出会议室的逻辑，包括从Redis中移除用户、发送退出消息、检查是否需要结束会议等
     *
     * @param tokenUserInfoDto 包含用户信息和当前会议ID的DTO
     * @param statusEnum       退出会议的状态枚举（正常退出、被踢出、黑名单等）
     */
    @Override
    public void exitMeetingRoom(TokenUserInfoDto tokenUserInfoDto, MeetingMemberStatusEnum statusEnum) {
        // 获取当前会议ID
        String meetingId = tokenUserInfoDto.getCurrentMeetingId();
        if (StringTools.isEmpty(meetingId)) return;
        // 获取用户ID
        String userId = tokenUserInfoDto.getUserId();
        // 从会议中退出用户
        Boolean exit = redisComponent.exitMeeting(meetingId, userId, statusEnum);
        if (!exit) {
            tokenUserInfoDto.setCurrentMeetingId(null);
            redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
            return;
        }
        // 创建退出消息发送DTO
        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.EXIT_MEETING_ROOM.getType());

        // 获取会议成员列表
        List<MeetingMemberDto> meetingMemberDtoList = redisComponent.getMeetingMemberList(meetingId);
        MeetingExitDto exitDto = new MeetingExitDto();
        exitDto.setMeetingMemberList(meetingMemberDtoList);
        exitDto.setExitUserId(userId);
        exitDto.setExitStatus(statusEnum.getStatus());

        messageSendDto.setMessageContent(JsonUtils.convertObj2Json(exitDto));
        messageSendDto.setMeetingId(meetingId);
        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.GROUP.getType());
        // 发送退出会议的消息
        messageHandler.sendMessage(messageSendDto);

        // 过滤出在线成员列表
        List<MeetingMemberDto> onLineMemberList = meetingMemberDtoList.stream().filter(item -> MeetingMemberStatusEnum.NORMAL.getStatus().equals(item.getStatus())).collect(Collectors.toList());

        if (onLineMemberList.isEmpty()) {
            MeetingReserve meetingReserve = meetingReserveMapper.selectByMeetingId(meetingId);
            if (meetingReserve == null) {
                finishMeeting(meetingId, null);
                return;
            }
            if (System.currentTimeMillis() > meetingReserve.getStartTime().getTime() + meetingReserve.getDuration() * 60 * 1000) {
                finishMeeting(meetingId, null);
                return;
            }
        }

        // 如果退出状态是被踢出或黑名单，则更新数据库中的成员状态
        if (ArrayUtils.contains(new Integer[]{MeetingMemberStatusEnum.KICK_OUT.getStatus(), MeetingMemberStatusEnum.BLACKLIST.getStatus()}, statusEnum.getStatus())) {
            MeetingMember meetingMember = new MeetingMember();
            meetingMember.setStatus(statusEnum.getStatus());
            meetingMemberMapper.updateByMeetingIdAndUserId(meetingMember, meetingId, userId);
        }
    }

    @Override
    public void forceExitMeeting(TokenUserInfoDto tokenUserInfoDto, String userId, MeetingMemberStatusEnum statusEnum) {
        MeetingInfo meetingInfo = this.meetingInfoMapper.selectByMeetingId(tokenUserInfoDto.getCurrentMeetingId());
        if (!meetingInfo.getCreateUserId().equals(tokenUserInfoDto.getUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        TokenUserInfoDto userInfoDto = this.redisComponent.getTokenUserInfoDtoByUserId(userId);
        exitMeetingRoom(userInfoDto, statusEnum);
    }

    /**
     * 结束会议
     * 完成会议的结束流程，包括验证会议创建者权限、更新会议状态、发送结束消息、
     * 更新会议成员状态、更新预约会议状态以及清理会议相关的用户状态
     *
     * @param meetingId 会议ID
     * @param userId    用户ID，用于验证是否为会议创建者，如果为null则不验证
     * @throws BusinessException 当用户不是会议创建者时抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishMeeting(String meetingId, String userId) {
        MeetingInfo meetingInfo = this.meetingInfoMapper.selectByMeetingId(meetingId);
        // 验证用户是否有权限结束会议（只有会议创建者才能结束会议）
        if (userId != null && !userId.equals(meetingInfo.getCreateUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        MeetingInfo updateInfo = new MeetingInfo();
        updateInfo.setStatus(MeetingStatusEnum.FINISHEN.getStatus());
        updateInfo.setEndTime(new Date());
        meetingInfoMapper.updateByMeetingId(updateInfo, meetingId);

        // 发送会议结束的消息通知
        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.GROUP.getType());
        messageSendDto.setStatus(MeetingStatusEnum.FINISHEN.getStatus());
        messageSendDto.setMeetingId(meetingId);
        messageHandler.sendMessage(messageSendDto);

        // 更新会议成员表中的会议状态为结束
        MeetingMember meetingMember = new MeetingMember();
        meetingMember.setMeetingStatus(MeetingStatusEnum.FINISHEN.getStatus());
        MeetingMemberQuery meetingMemberQuery = new MeetingMemberQuery();
        meetingMemberQuery.setMeetingId(meetingId);
        meetingMemberMapper.updateByParam(meetingMember, meetingMemberQuery);

        // 更新预约会议的状态为已结束
        MeetingReserve updataMeetingReserve = new MeetingReserve();
        updataMeetingReserve.setStatus(MeetingReserveStatusEnum.FINISHED.getStatus());
        updataMeetingReserve.setMeetingId(meetingId);
        meetingReserveMapper.updateByMeetingId(updataMeetingReserve, meetingId);

        // 清理会议成员的当前会议状态
        List<MeetingMemberDto> meetingMemberList = redisComponent.getMeetingMemberList(meetingId);
        for (MeetingMemberDto meetingMemberDto : meetingMemberList) {
            TokenUserInfoDto userInfoDto = this.redisComponent.getTokenUserInfoDtoByUserId(meetingMemberDto.getUserId());
            userInfoDto.setCurrentMeetingId(null);
            redisComponent.saveTokenUserInfoDto(userInfoDto);
        }
        // 从Redis中移除所有会议成员
        redisComponent.removeAllMeetingMember(meetingId);
    }

    /**
     * 预定会议加入处理
     * 验证用户是否有权限加入预定的会议，包括检查用户当前会议状态、会议有效性、用户邀请状态以及密码验证，
     * 如会议信息不存在则创建会议记录并更新用户会议状态
     */
    @Override
    public void reserveJoinMeeting(String meetingId, TokenUserInfoDto tokenUserInfoDto, String joinPassword) {
        String userId = tokenUserInfoDto.getUserId();
        // 检查用户是否已在其他会议中
        if (!StringTools.isEmpty(tokenUserInfoDto.getCurrentMeetingId()) && !meetingId.equals(tokenUserInfoDto.getCurrentMeetingId())) {
            throw new BusinessException("你有未结束的会议无法加入其他会议");
        }
        // 检查用户是否可以加入会议（如是否被拉黑）
        checkMeetingJoin(meetingId, userId);
        MeetingReserve meetingReserve = meetingReserveMapper.selectByMeetingId(meetingId);
        // 验证会议预定信息是否存在
        if (meetingReserve == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        MeetingReserveMember member = meetingReserveMemberMapper.selectByMeetingIdAndInviteUserId(meetingId, tokenUserInfoDto.getUserId());
        // 验证用户是否被邀请参加此会议
        if (member == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 验证会议密码（如果需要）
        if (MeetingJoinTypeEnum.PASSWORD.getType().equals(meetingReserve.getJoinType()) && !meetingReserve.getJoinPassword().equals(joinPassword)) {
            throw new BusinessException("密码错误");
        }
        MeetingInfo meetingInfo = meetingInfoMapper.selectByMeetingId(meetingId);
        // 如果会议信息不存在，则根据预定信息创建新的会议记录
        if (meetingInfo == null) {
            meetingInfo = new MeetingInfo();
            meetingInfo.setMeetingName(meetingReserve.getMeetingName());
            meetingInfo.setMeetingNo(StringTools.getMeetingNoOrMeetingId());
            meetingInfo.setJoinType(meetingReserve.getJoinType());
            meetingInfo.setJoinPassword(meetingReserve.getJoinPassword());
            Date curDate = new Date();
            meetingInfo.setCreateTime(curDate);
            meetingInfo.setMeetingId(meetingId);
            meetingInfo.setStartTime(curDate);
            meetingInfo.setCreateUserId(meetingReserve.getCreateUserId());
            meetingInfo.setStatus(MeetingStatusEnum.RUNING.getStatus());
            meetingInfoMapper.insert(meetingInfo);
        }
        tokenUserInfoDto.setCurrentMeetingId(meetingId);
        redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
    }

    /*
     * 邀请成员加入会议
     * 验证用户是否有权限邀请指定联系人加入当前会议，并向被邀请的联系人发送邀请消息
     */
    @Override
    public void inviteMember(TokenUserInfoDto tokenUserInfo, String selectContactIds) {
        String[] contactIds = selectContactIds.split(",");
        UserContactQuery userContactQuery = new UserContactQuery();
        userContactQuery.setUserId(tokenUserInfo.getUserId());
        //待邀请联系人列表
        userContactQuery.setStatus(MeetingMemberStatusEnum.NORMAL.getStatus());
        List<UserContact> userContacts = userContactMapper.selectList(userContactQuery);
        //所以有效联系人
        List<String> contactList = userContacts.stream().map(item -> item.getContactId()).collect(Collectors.toList());
        // 验证当前用户的有效联系人列表是否包含所有待邀请的联系人ID
        if (!contactList.containsAll(Arrays.asList(contactIds))) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        MeetingInfo meetingInfo = meetingInfoMapper.selectByMeetingId(tokenUserInfo.getCurrentMeetingId());
        // 遍历所有待邀请的联系人ID，向每个不在会议中的联系人发送邀请消息
        for (String contactId : contactIds) {
            MeetingMemberDto meetingMemberDto = redisComponent.getMeetingMember(tokenUserInfo.getCurrentMeetingId(), contactId);
            // 如果联系人已在会议中且状态正常，则跳过邀请
            if (meetingMemberDto != null && MeetingMemberStatusEnum.NORMAL.getStatus().equals(meetingMemberDto.getStatus())) {
                continue;
            }
            redisComponent.addInviteInfo(contactId, tokenUserInfo.getCurrentMeetingId());
            MessageSendDto messageSendDto = new MessageSendDto();
            messageSendDto.setMessageType(MessageTypeEnum.INVITE_MEMBER_MEETING.getType());
            messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.USER.getType());
            messageSendDto.setReceiveUserId(contactId);
            MeetingInviteDto meetingInviteDto = new MeetingInviteDto();
            meetingInviteDto.setInviteUserName(tokenUserInfo.getNickName());
            meetingInviteDto.setMeetingName(meetingInfo.getMeetingName());
            meetingInviteDto.setMeetingId(tokenUserInfo.getCurrentMeetingId());
            messageSendDto.setMessageContent(JsonUtils.convertObj2Json(meetingInviteDto));
            messageHandler.sendMessage(messageSendDto);
        }
    }

    @Override
    public void acceptInvite(TokenUserInfoDto tokenUserInfoDto, String meetingId) {
        String redisMeetingId = redisComponent.getInviteInfo(tokenUserInfoDto.getUserId(), meetingId);
        if (null == redisMeetingId) {
            throw new BusinessException("邀请信息过期");
        }
        tokenUserInfoDto.setCurrentMeetingId(meetingId);
        tokenUserInfoDto.setCurrentNickName(tokenUserInfoDto.getNickName());
        redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
    }

    @Override
    public void updateMemberOpenVideo(String meetingId, String userId, Boolean openVideo) {
        MeetingMemberDto meetingMemberDto = redisComponent.getMeetingMember(meetingId, userId);
        meetingMemberDto.setOpenVideo(openVideo);
        this.redisComponent.add2Meeting(meetingId, meetingMemberDto);

        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.MEETING_USER_VIDEO_CHANGE.getType());
        messageSendDto.setMessageContent(openVideo);
        messageSendDto.setSendUserId(userId);
        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.GROUP.getType());
        messageSendDto.setMeetingId(meetingId);
        messageHandler.sendMessage(messageSendDto);
    }
}