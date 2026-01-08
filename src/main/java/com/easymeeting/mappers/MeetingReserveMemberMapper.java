package com.easymeeting.mappers;

import org.apache.ibatis.annotations.Param;

/**
 *  数据库操作接口
 */
public interface MeetingReserveMemberMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据MeetingIdAndInviteUserId更新
	 */
	 Integer updateByMeetingIdAndInviteUserId(@Param("bean") T t,@Param("meetingId") String meetingId,@Param("inviteUserId") String inviteUserId);


	/**
	 * 根据MeetingIdAndInviteUserId删除
	 */
	 Integer deleteByMeetingIdAndInviteUserId(@Param("meetingId") String meetingId,@Param("inviteUserId") String inviteUserId);


	/**
	 * 根据MeetingIdAndInviteUserId获取对象
	 */
	 T selectByMeetingIdAndInviteUserId(@Param("meetingId") String meetingId,@Param("inviteUserId") String inviteUserId);


}
