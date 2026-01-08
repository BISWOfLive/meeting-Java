package com.easymeeting.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.easymeeting.entity.enums.PageSize;
import com.easymeeting.entity.query.MeetingReserveMemberQuery;
import com.easymeeting.entity.po.MeetingReserveMember;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.query.SimplePage;
import com.easymeeting.mappers.MeetingReserveMemberMapper;
import com.easymeeting.service.MeetingReserveMemberService;
import com.easymeeting.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("meetingReserveMemberService")
public class MeetingReserveMemberServiceImpl implements MeetingReserveMemberService {

	@Resource
	private MeetingReserveMemberMapper<MeetingReserveMember, MeetingReserveMemberQuery> meetingReserveMemberMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<MeetingReserveMember> findListByParam(MeetingReserveMemberQuery param) {
		return this.meetingReserveMemberMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(MeetingReserveMemberQuery param) {
		return this.meetingReserveMemberMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<MeetingReserveMember> findListByPage(MeetingReserveMemberQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<MeetingReserveMember> list = this.findListByParam(param);
		PaginationResultVO<MeetingReserveMember> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(MeetingReserveMember bean) {
		return this.meetingReserveMemberMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<MeetingReserveMember> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.meetingReserveMemberMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<MeetingReserveMember> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.meetingReserveMemberMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(MeetingReserveMember bean, MeetingReserveMemberQuery param) {
		StringTools.checkParam(param);
		return this.meetingReserveMemberMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(MeetingReserveMemberQuery param) {
		StringTools.checkParam(param);
		return this.meetingReserveMemberMapper.deleteByParam(param);
	}

	/**
	 * 根据MeetingIdAndInviteUserId获取对象
	 */
	@Override
	public MeetingReserveMember getMeetingReserveMemberByMeetingIdAndInviteUserId(String meetingId, String inviteUserId) {
		return this.meetingReserveMemberMapper.selectByMeetingIdAndInviteUserId(meetingId, inviteUserId);
	}

	/**
	 * 根据MeetingIdAndInviteUserId修改
	 */
	@Override
	public Integer updateMeetingReserveMemberByMeetingIdAndInviteUserId(MeetingReserveMember bean, String meetingId, String inviteUserId) {
		return this.meetingReserveMemberMapper.updateByMeetingIdAndInviteUserId(bean, meetingId, inviteUserId);
	}

	/**
	 * 根据MeetingIdAndInviteUserId删除
	 */
	@Override
	public Integer deleteMeetingReserveMemberByMeetingIdAndInviteUserId(String meetingId, String inviteUserId) {
		return this.meetingReserveMemberMapper.deleteByMeetingIdAndInviteUserId(meetingId, inviteUserId);
	}
}