package com.easymeeting.service;

import java.io.IOException;
import java.util.List;

import com.easymeeting.entity.query.MeetingChatMessageQuery;
import com.easymeeting.entity.po.MeetingChatMessage;
import com.easymeeting.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;


/**
 * 会议聊天消息表 业务接口
 */
public interface MeetingChatMessageService {

	/**
	 * 根据条件查询列表
	 */
	List<MeetingChatMessage> findListByParam(String tableName,MeetingChatMessageQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(String tableName,MeetingChatMessageQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<MeetingChatMessage> findListByPage(String tableName,MeetingChatMessageQuery param);

	/**
	 * 新增
	 */
	Integer add(String tableName,MeetingChatMessage bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(String tableName,List<MeetingChatMessage> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(String tableName,List<MeetingChatMessage> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(String tableName,MeetingChatMessage bean,MeetingChatMessageQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(String tableName,MeetingChatMessageQuery param);

	/**
	 * 根据MessageId查询对象
	 */
	MeetingChatMessage getMeetingChatMessageByMessageId(String tableName,Long messageId);


	/**
	 * 根据MessageId修改
	 */
	Integer updateMeetingChatMessageByMessageId(String tableName,MeetingChatMessage bean,Long messageId);


	/**
	 * 根据MessageId删除
	 */
	Integer deleteMeetingChatMessageByMessageId(String tableName,Long messageId);

    void saveChatMessage(MeetingChatMessage chatMessage);

	void uploadFile(MultipartFile file,String meetingId,Long messageId,Long sendTime) throws IOException;
}