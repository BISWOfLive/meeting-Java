package com.easymeeting.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import com.easymeeting.entity.dto.MeetingJoinDto;
import com.easymeeting.entity.dto.MeetingMemberDto;
import com.easymeeting.entity.dto.MessageSendDto;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.*;
import com.easymeeting.entity.po.MeetingMember;
import com.easymeeting.entity.query.MeetingMemberQuery;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.mappers.MeetingMemberMapper;
import com.easymeeting.redis.RedisComponent;
import com.easymeeting.websocket.ChannelContextUtils;
import org.springframework.stereotype.Service;
import com.easymeeting.entity.query.MeetingInfoQuery;
import com.easymeeting.entity.po.MeetingInfo;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.query.SimplePage;
import com.easymeeting.mappers.MeetingInfoMapper;
import com.easymeeting.service.MeetingInfoService;
import com.easymeeting.utils.StringTools;


/**
 *  业务接口实现
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


	private void addMeetingMember(String meetingId, String userId, String nickName, Integer meetingType){
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

	private void add2Meeting(String meetingId, String userId, String nickName, Integer sex,Boolean videoOpen,Integer meetingType){

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
		if (StringTools.isEmpty(meetingId)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		MeetingInfo meetingInfo = this.meetingInfoMapper.selectByMeetingId(meetingId);
		if (meetingInfo == null || MeetingStatusEnum.FINISHEN.getStatus().equals(meetingInfo.getStatus())) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		this.checkMeetingJoin(meetingId, userId);

		MemberTypeEnum memberTypeEnum = meetingInfo.getCreateUserId().equals(userId) ? MemberTypeEnum.COMPERE : MemberTypeEnum.NORMAL;
		this.addMeetingMember(meetingId, userId, nickName, memberTypeEnum.getType());
		this.add2Meeting(meetingId, userId, nickName, sex,videoOpen,memberTypeEnum.getType());

		channelContextUtils.addMeetingRoom(meetingId, userId);

		MeetingJoinDto meetingJoinDto = new MeetingJoinDto();
		meetingJoinDto.setNewMember(redisComponent.getMeetingMember(meetingId, userId));
		meetingJoinDto.setMeetingMemberList(redisComponent.getMeetingMemberList(meetingId));

		MessageSendDto messageSendDto = new MessageSendDto();
		messageSendDto.setMessageType(MessageTypeEnum.ADD_MEETING_ROOM.getType());
		messageSendDto.setMessageContent(meetingJoinDto);
		messageSendDto.setMeetingId(meetingId);
		messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.GROUP.getType());
		channelContextUtils.sendMessage(messageSendDto);
	}

	@Override
	public String preJoinMeeting(String meetingNo, TokenUserInfoDto tokenUserInfoDto, String joinPassword) {
		String userId = tokenUserInfoDto.getUserId();
		MeetingInfoQuery meetingInfoQuery = new MeetingInfoQuery();
		meetingInfoQuery.setMeetingNo(meetingNo);
		meetingInfoQuery.setStatus(MeetingStatusEnum.RUNING.getStatus());
		meetingInfoQuery.setOrderBy("create_time desc");
		List<MeetingInfo> meetingInfoList = meetingInfoMapper.selectList(meetingInfoQuery);
		if (meetingInfoList.isEmpty()){
			throw new BusinessException("404");
		}
		MeetingInfo meetingInfo = meetingInfoList.get(0);
		if (!MeetingStatusEnum.RUNING.getStatus().equals(meetingInfo.getStatus())){
			throw new BusinessException("会议已结束");
		}
		if (!StringTools.isEmpty(tokenUserInfoDto.getCurrentMeetingId()) && !meetingInfo.getMeetingId().equals(tokenUserInfoDto.getCurrentMeetingId())){
			throw new BusinessException("你有未结束的会议,无法加入其他会议");
		}
		checkMeetingJoin(meetingInfo.getMeetingId(),userId);

		if (MeetingJoinTypeEnum.PASSWORD.getType().equals(meetingInfo.getJoinType()) && !meetingInfo.getJoinPassword().equals(joinPassword)){
			throw new BusinessException("密码错误");
		}
		tokenUserInfoDto.setCurrentMeetingId(meetingInfo.getMeetingId());
		redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
		return meetingInfo.getMeetingId();
	}

	private void checkMeetingJoin(String meetingId, String userId) {
		MeetingMemberDto meetingMemberDto = redisComponent.getMeetingMember(meetingId, userId);
		if (meetingMemberDto != null && MeetingMemberStatusEnum.BLACKLIST.getStatus().equals(meetingMemberDto.getStatus())){
			throw new BusinessException("你已经被拉黑无法进入会议");
		}
	}


}