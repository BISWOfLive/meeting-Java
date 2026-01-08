package com.easymeeting.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.MeetingMemberStatusEnum;
import com.easymeeting.entity.enums.MeetingStatusEnum;
import com.easymeeting.entity.query.MeetingInfoQuery;
import com.easymeeting.entity.po.MeetingInfo;
import com.easymeeting.entity.vo.PaginationResultVO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 *  业务接口
 */
public interface MeetingInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<MeetingInfo> findListByParam(MeetingInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(MeetingInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<MeetingInfo> findListByPage(MeetingInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(MeetingInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<MeetingInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<MeetingInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(MeetingInfo bean,MeetingInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(MeetingInfoQuery param);

	/**
	 * 根据MeetingId查询对象
	 */
	MeetingInfo getMeetingInfoByMeetingId(String meetingId);


	/**
	 * 根据MeetingId修改
	 */
	Integer updateMeetingInfoByMeetingId(MeetingInfo bean,String meetingId);


	/**
	 * 根据MeetingId删除
	 */
	Integer deleteMeetingInfoByMeetingId(String meetingId);

	void quickMeeting(MeetingInfo meetingInfo ,String nickName);

	void joinMeeting(String meetingId,String userId,String nickName,Integer sex,Boolean videoOpen);

	String preJoinMeeting(String meetingNo, TokenUserInfoDto tokenUserInfoDto, String joinPassword);

	void exitMeetingRoom(TokenUserInfoDto tokenUserInfoDto, MeetingMemberStatusEnum statusEnum);

	void forceExitMeeting(TokenUserInfoDto tokenUserInfoDto,String userId , MeetingMemberStatusEnum statusEnum);

	void finishMeeting(String meetingId, String userId);

    void reserveJoinMeeting(@NotEmpty String meetingId, TokenUserInfoDto tokenUserInfoDto, String joinPassword);
}