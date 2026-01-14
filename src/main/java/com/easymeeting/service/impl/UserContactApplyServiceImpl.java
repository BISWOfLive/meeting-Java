package com.easymeeting.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.easymeeting.entity.dto.MessageSendDto;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.*;
import com.easymeeting.entity.po.UserContact;
import com.easymeeting.entity.query.UserContactQuery;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.mappers.UserContactMapper;
import com.easymeeting.websocket.message.MessageHandler;
import jodd.util.ArraysUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easymeeting.entity.query.UserContactApplyQuery;
import com.easymeeting.entity.po.UserContactApply;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.query.SimplePage;
import com.easymeeting.mappers.UserContactApplyMapper;
import com.easymeeting.service.UserContactApplyService;
import com.easymeeting.utils.StringTools;


/**
 * 用户联系人申请表 业务接口实现
 */
@Service("userContactApplyService")
public class UserContactApplyServiceImpl implements UserContactApplyService {

	@Resource
	private UserContactApplyMapper<UserContactApply, UserContactApplyQuery> userContactApplyMapper;

	@Resource
	private UserContactMapper<UserContact, UserContactQuery> userContactMapper;

    @Resource
    private MessageHandler messageHandler;


	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserContactApply> findListByParam(UserContactApplyQuery param) {
		return this.userContactApplyMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserContactApplyQuery param) {
		return this.userContactApplyMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserContactApply> findListByPage(UserContactApplyQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserContactApply> list = this.findListByParam(param);
		PaginationResultVO<UserContactApply> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserContactApply bean) {
		return this.userContactApplyMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserContactApply> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userContactApplyMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserContactApply> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userContactApplyMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserContactApply bean, UserContactApplyQuery param) {
		StringTools.checkParam(param);
		return this.userContactApplyMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserContactApplyQuery param) {
		StringTools.checkParam(param);
		return this.userContactApplyMapper.deleteByParam(param);
	}

	/**
	 * 根据ApplyId获取对象
	 */
	@Override
	public UserContactApply getUserContactApplyByApplyId(Integer applyId) {
		return this.userContactApplyMapper.selectByApplyId(applyId);
	}

	/**
	 * 根据ApplyId修改
	 */
	@Override
	public Integer updateUserContactApplyByApplyId(UserContactApply bean, Integer applyId) {
		return this.userContactApplyMapper.updateByApplyId(bean, applyId);
	}

	/**
	 * 根据ApplyId删除
	 */
	@Override
	public Integer deleteUserContactApplyByApplyId(Integer applyId) {
		return this.userContactApplyMapper.deleteByApplyId(applyId);
	}

	/**
	 * 根据ApplyUserIdAndReceiveUserId获取对象
	 */
	@Override
	public UserContactApply getUserContactApplyByApplyUserIdAndReceiveUserId(String applyUserId, String receiveUserId) {
		return this.userContactApplyMapper.selectByApplyUserIdAndReceiveUserId(applyUserId, receiveUserId);
	}

	/**
	 * 根据ApplyUserIdAndReceiveUserId修改
	 */
	@Override
	public Integer updateUserContactApplyByApplyUserIdAndReceiveUserId(UserContactApply bean, String applyUserId, String receiveUserId) {
		return this.userContactApplyMapper.updateByApplyUserIdAndReceiveUserId(bean, applyUserId, receiveUserId);
	}

	/**
	 * 根据ApplyUserIdAndReceiveUserId删除
	 */
	@Override
	public Integer deleteUserContactApplyByApplyUserIdAndReceiveUserId(String applyUserId, String receiveUserId) {
		return this.userContactApplyMapper.deleteByApplyUserIdAndReceiveUserId(applyUserId, receiveUserId);
	}

	/**
	 * 保存用户联系人申请
	 * @param bean 用户联系人申请实体，包含申请人ID、接收人ID等信息
	 */
	@Override
	public Integer saveUserContactApply(UserContactApply bean) {
		// 检查接收方是否已将申请人拉黑
		UserContact userContact = userContactMapper.selectByUserIdAndContactId(bean.getReceiveUserId(), bean.getApplyUserId());
		if (userContact != null && UserContactStatusEnum.BLACKLIST.getStatus().equals(userContact.getStatus())){
			throw new BusinessException("对方已将你拉黑");
		}
		
		// 检查双方是否已是好友关系，若是则更新最后更新时间并返回好友状态
		if (userContact != null && UserContactStatusEnum.FRIEND.getStatus().equals(userContact.getStatus())){
			UserContact updateInfo = new UserContact();
			updateInfo.setLastUpdateTime(new Date());
			updateInfo.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			userContactMapper.updateByUserIdAndContactId(updateInfo, bean.getApplyUserId(),bean.getReceiveUserId());
			return UserContactStatusEnum.FRIEND.getStatus();
		}
		
		// 查询是否存在历史申请记录，无记录则插入新申请，有记录则更新申请时间
		UserContactApply apply = userContactApplyMapper.selectByApplyUserIdAndReceiveUserId(bean.getApplyUserId(), bean.getReceiveUserId());
		if (apply == null){
			bean.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
			bean.setLastApplyTime(System.currentTimeMillis());
			this.userContactApplyMapper.insert(bean);
		}else {
			UserContactApply updateInfo = new UserContactApply();
			updateInfo.setLastApplyTime(System.currentTimeMillis());
			updateInfo.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
			userContactApplyMapper.updateByApplyId(updateInfo,apply.getApplyId());
		}
		
		// 发送好友申请消息通知接收方
		MessageSendDto messageSendDto = new MessageSendDto();
		messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.USER.getType());
		messageSendDto.setMessageType(MessageTypeEnum.USER_CONTACT_APPLY.getType());
		messageSendDto.setReceiveUserId(bean.getReceiveUserId());
		messageHandler.sendMessage(messageSendDto);
		return UserContactApplyStatusEnum.INIT.getStatus();
	}

	/**
	 * 处理好友申请
	 */
	@Override
	public void dealWithApply(String applyUserId, String userId, String nickName, Integer status) {
		// 验证处理状态的有效性
		UserContactApplyStatusEnum statusEnum = UserContactApplyStatusEnum.getByStatus(status);
		if (statusEnum == null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		
		// 查询好友申请记录是否存在
		UserContactApply userContactApply = userContactApplyMapper.selectByApplyUserIdAndReceiveUserId(applyUserId, userId);
		if (userContactApply == null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		
		// 如果是通过申请，则建立双向好友关系
		if (UserContactApplyStatusEnum.PASS.getStatus().equals(status)){
			UserContact userContact = new UserContact();
			userContact.setUserId(applyUserId);
			userContact.setContactId(userId);
			userContact.setLastUpdateTime(new Date());
			userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			userContactMapper.insertOrUpdate(userContact);

			userContact.setUserId(userId);
			userContact.setContactId(applyUserId);
			userContactMapper.insertOrUpdate(userContact);
		}
		
		// 更新申请记录的状态
		UserContactApply updateApply = new UserContactApply();
		updateApply.setStatus(status);
		userContactApplyMapper.updateByApplyId(updateApply,userContactApply.getApplyId());

		// 发送处理结果通知给申请人
		MessageSendDto messageSendDto = new MessageSendDto();
		messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.USER.getType());
		messageSendDto.setMessageType(MessageTypeEnum.USER_CONTACT_DEAL_WITH.getType());
		messageSendDto.setReceiveUserId(applyUserId);
		messageSendDto.setSendUserNickName(nickName);
		messageSendDto.setMessageContent(status);
		messageHandler.sendMessage(messageSendDto);
	}

	@Override
	public void delCount(String userId, String contactId, Integer status) {
		if (!ArrayUtils.contains(new Integer[]{UserContactStatusEnum.BLACKLIST.getStatus(),UserContactStatusEnum.DEL.getStatus()},status)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserContact userContact = new UserContact();
		userContact.setLastUpdateTime(new Date());
		userContact.setStatus(status);
		this.userContactMapper.updateByUserIdAndContactId(userContact,userId,contactId);
	}


}