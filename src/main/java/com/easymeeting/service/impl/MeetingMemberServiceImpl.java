package com.easymeeting.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.easymeeting.entity.enums.PageSize;
import com.easymeeting.entity.query.MeetingMemberQuery;
import com.easymeeting.entity.po.MeetingMember;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.query.SimplePage;
import com.easymeeting.mappers.MeetingMemberMapper;
import com.easymeeting.service.MeetingMemberService;
import com.easymeeting.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("meetingMemberService")
public class MeetingMemberServiceImpl implements MeetingMemberService {

	@Resource
	private MeetingMemberMapper<MeetingMember, MeetingMemberQuery> meetingMemberMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<MeetingMember> findListByParam(MeetingMemberQuery param) {
		return this.meetingMemberMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(MeetingMemberQuery param) {
		return this.meetingMemberMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<MeetingMember> findListByPage(MeetingMemberQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<MeetingMember> list = this.findListByParam(param);
		PaginationResultVO<MeetingMember> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(MeetingMember bean) {
		return this.meetingMemberMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<MeetingMember> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.meetingMemberMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<MeetingMember> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.meetingMemberMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(MeetingMember bean, MeetingMemberQuery param) {
		StringTools.checkParam(param);
		return this.meetingMemberMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(MeetingMemberQuery param) {
		StringTools.checkParam(param);
		return this.meetingMemberMapper.deleteByParam(param);
	}

	/**
	 * 根据MeetingIdAndUserId获取对象
	 */
	@Override
	public MeetingMember getMeetingMemberByMeetingIdAndUserId(String meetingId, String userId) {
		return this.meetingMemberMapper.selectByMeetingIdAndUserId(meetingId, userId);
	}

	/**
	 * 根据MeetingIdAndUserId修改
	 */
	@Override
	public Integer updateMeetingMemberByMeetingIdAndUserId(MeetingMember bean, String meetingId, String userId) {
		return this.meetingMemberMapper.updateByMeetingIdAndUserId(bean, meetingId, userId);
	}

	/**
	 * 根据MeetingIdAndUserId删除
	 */
	@Override
	public Integer deleteMeetingMemberByMeetingIdAndUserId(String meetingId, String userId) {
		return this.meetingMemberMapper.deleteByMeetingIdAndUserId(meetingId, userId);
	}
}