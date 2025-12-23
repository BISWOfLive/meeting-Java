package com.easymeeting.service;

import java.util.List;

import com.easymeeting.entity.query.MeetingMemberQuery;
import com.easymeeting.entity.po.MeetingMember;
import com.easymeeting.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface MeetingMemberService {

	/**
	 * 根据条件查询列表
	 */
	List<MeetingMember> findListByParam(MeetingMemberQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(MeetingMemberQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<MeetingMember> findListByPage(MeetingMemberQuery param);

	/**
	 * 新增
	 */
	Integer add(MeetingMember bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<MeetingMember> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<MeetingMember> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(MeetingMember bean,MeetingMemberQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(MeetingMemberQuery param);

	/**
	 * 根据MeetingIdAndUserId查询对象
	 */
	MeetingMember getMeetingMemberByMeetingIdAndUserId(String meetingId,String userId);


	/**
	 * 根据MeetingIdAndUserId修改
	 */
	Integer updateMeetingMemberByMeetingIdAndUserId(MeetingMember bean,String meetingId,String userId);


	/**
	 * 根据MeetingIdAndUserId删除
	 */
	Integer deleteMeetingMemberByMeetingIdAndUserId(String meetingId,String userId);

}