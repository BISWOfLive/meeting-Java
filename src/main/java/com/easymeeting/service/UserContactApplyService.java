package com.easymeeting.service;

import java.util.List;

import com.easymeeting.entity.query.UserContactApplyQuery;
import com.easymeeting.entity.po.UserContactApply;
import com.easymeeting.entity.vo.PaginationResultVO;


/**
 * 用户联系人申请表 业务接口
 */
public interface UserContactApplyService {

	/**
	 * 根据条件查询列表
	 */
	List<UserContactApply> findListByParam(UserContactApplyQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserContactApplyQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserContactApply> findListByPage(UserContactApplyQuery param);

	/**
	 * 新增
	 */
	Integer add(UserContactApply bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserContactApply> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserContactApply> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserContactApply bean,UserContactApplyQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserContactApplyQuery param);

	/**
	 * 根据ApplyId查询对象
	 */
	UserContactApply getUserContactApplyByApplyId(Integer applyId);


	/**
	 * 根据ApplyId修改
	 */
	Integer updateUserContactApplyByApplyId(UserContactApply bean,Integer applyId);


	/**
	 * 根据ApplyId删除
	 */
	Integer deleteUserContactApplyByApplyId(Integer applyId);


	/**
	 * 根据ApplyUserIdAndReceiveUserId查询对象
	 */
	UserContactApply getUserContactApplyByApplyUserIdAndReceiveUserId(String applyUserId,String receiveUserId);


	/**
	 * 根据ApplyUserIdAndReceiveUserId修改
	 */
	Integer updateUserContactApplyByApplyUserIdAndReceiveUserId(UserContactApply bean,String applyUserId,String receiveUserId);


	/**
	 * 根据ApplyUserIdAndReceiveUserId删除
	 */
	Integer deleteUserContactApplyByApplyUserIdAndReceiveUserId(String applyUserId,String receiveUserId);

    Integer saveUserContactApply(UserContactApply userContactApply);

	void dealWithApply(String applyUserId,String userId,String nickName,Integer status);
}