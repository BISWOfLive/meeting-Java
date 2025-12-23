package com.easymeeting.mappers;

import org.apache.ibatis.annotations.Param;

/**
 *  数据库操作接口
 */
public interface MeetingInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据MeetingId更新
	 */
	 Integer updateByMeetingId(@Param("bean") T t,@Param("meetingId") String meetingId);


	/**
	 * 根据MeetingId删除
	 */
	 Integer deleteByMeetingId(@Param("meetingId") String meetingId);


	/**
	 * 根据MeetingId获取对象
	 */
	 T selectByMeetingId(@Param("meetingId") String meetingId);


}
