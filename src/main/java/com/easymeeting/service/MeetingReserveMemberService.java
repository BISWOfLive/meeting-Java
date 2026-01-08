package com.easymeeting.service;

import java.util.List;

import com.easymeeting.entity.query.MeetingReserveMemberQuery;
import com.easymeeting.entity.po.MeetingReserveMember;
import com.easymeeting.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface MeetingReserveMemberService {

	/**
	 * 根据条件查询列表
	 */
	List<MeetingReserveMember> findListByParam(MeetingReserveMemberQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(MeetingReserveMemberQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<MeetingReserveMember> findListByPage(MeetingReserveMemberQuery param);

	/**
	 * 新增
	 */
	Integer add(MeetingReserveMember bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<MeetingReserveMember> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<MeetingReserveMember> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(MeetingReserveMember bean,MeetingReserveMemberQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(MeetingReserveMemberQuery param);

	/**
	 * 根据MeetingIdAndInviteUserId查询对象
	 */
	MeetingReserveMember getMeetingReserveMemberByMeetingIdAndInviteUserId(String meetingId,String inviteUserId);


	/**
	 * 根据MeetingIdAndInviteUserId修改
	 */
	Integer updateMeetingReserveMemberByMeetingIdAndInviteUserId(MeetingReserveMember bean,String meetingId,String inviteUserId);


	/**
	 * 根据MeetingIdAndInviteUserId删除
	 */
	Integer deleteMeetingReserveMemberByMeetingIdAndInviteUserId(String meetingId,String inviteUserId);

}