package com.easymeeting.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 用户联系人申请表 数据库操作接口
 */
public interface UserContactApplyMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据ApplyId更新
	 */
	 Integer updateByApplyId(@Param("bean") T t,@Param("applyId") Integer applyId);


	/**
	 * 根据ApplyId删除
	 */
	 Integer deleteByApplyId(@Param("applyId") Integer applyId);


	/**
	 * 根据ApplyId获取对象
	 */
	 T selectByApplyId(@Param("applyId") Integer applyId);


	/**
	 * 根据ApplyUserIdAndReceiveUserId更新
	 */
	 Integer updateByApplyUserIdAndReceiveUserId(@Param("bean") T t,@Param("applyUserId") String applyUserId,@Param("receiveUserId") String receiveUserId);


	/**
	 * 根据ApplyUserIdAndReceiveUserId删除
	 */
	 Integer deleteByApplyUserIdAndReceiveUserId(@Param("applyUserId") String applyUserId,@Param("receiveUserId") String receiveUserId);


	/**
	 * 根据ApplyUserIdAndReceiveUserId获取对象
	 */
	 T selectByApplyUserIdAndReceiveUserId(@Param("applyUserId") String applyUserId,@Param("receiveUserId") String receiveUserId);


}
