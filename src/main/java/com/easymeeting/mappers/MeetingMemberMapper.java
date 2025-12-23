package com.easymeeting.mappers;

import org.apache.ibatis.annotations.Param;

/**
 *  数据库操作接口
 */
public interface MeetingMemberMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据MeetingIdAndUserId更新
	 */
	 Integer updateByMeetingIdAndUserId(@Param("bean") T t,@Param("meetingId") String meetingId,@Param("userId") String userId);


	/**
	 * 根据MeetingIdAndUserId删除
	 */
	 Integer deleteByMeetingIdAndUserId(@Param("meetingId") String meetingId,@Param("userId") String userId);


	/**
	 * 根据MeetingIdAndUserId获取对象
	 */
	 T selectByMeetingIdAndUserId(@Param("meetingId") String meetingId,@Param("userId") String userId);


}
