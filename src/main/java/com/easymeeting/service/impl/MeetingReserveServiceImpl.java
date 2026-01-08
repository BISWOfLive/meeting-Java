package com.easymeeting.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.easymeeting.entity.enums.MeetingReserveStatusEnum;
import com.easymeeting.entity.enums.ResponseCodeEnum;
import com.easymeeting.entity.po.MeetingReserveMember;
import com.easymeeting.entity.query.MeetingReserveMemberQuery;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.mappers.MeetingReserveMemberMapper;
import org.springframework.stereotype.Service;

import com.easymeeting.entity.enums.PageSize;
import com.easymeeting.entity.query.MeetingReserveQuery;
import com.easymeeting.entity.po.MeetingReserve;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.query.SimplePage;
import com.easymeeting.mappers.MeetingReserveMapper;
import com.easymeeting.service.MeetingReserveService;
import com.easymeeting.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 *  业务接口实现
 */
@Service("meetingReserveService")
public class MeetingReserveServiceImpl implements MeetingReserveService {

	@Resource
	private MeetingReserveMapper<MeetingReserve, MeetingReserveQuery> meetingReserveMapper;

	@Resource
	private MeetingReserveMemberMapper<MeetingReserveMember, MeetingReserveMemberQuery> meetingReserveMemberMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<MeetingReserve> findListByParam(MeetingReserveQuery param) {
		return this.meetingReserveMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(MeetingReserveQuery param) {
		return this.meetingReserveMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<MeetingReserve> findListByPage(MeetingReserveQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<MeetingReserve> list = this.findListByParam(param);
		PaginationResultVO<MeetingReserve> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(MeetingReserve bean) {
		return this.meetingReserveMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<MeetingReserve> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.meetingReserveMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<MeetingReserve> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.meetingReserveMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(MeetingReserve bean, MeetingReserveQuery param) {
		StringTools.checkParam(param);
		return this.meetingReserveMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(MeetingReserveQuery param) {
		StringTools.checkParam(param);
		return this.meetingReserveMapper.deleteByParam(param);
	}

	/**
	 * 根据MeetingId获取对象
	 */
	@Override
	public MeetingReserve getMeetingReserveByMeetingId(String meetingId) {
		return this.meetingReserveMapper.selectByMeetingId(meetingId);
	}

	/**
	 * 根据MeetingId修改
	 */
	@Override
	public Integer updateMeetingReserveByMeetingId(MeetingReserve bean, String meetingId) {
		return this.meetingReserveMapper.updateByMeetingId(bean, meetingId);
	}

	/**
	 * 根据MeetingId删除
	 */
	@Override
	public Integer deleteMeetingReserveByMeetingId(String meetingId) {
		return this.meetingReserveMapper.deleteByMeetingId(meetingId);
	}

	@Override
	/**
	 * 创建会议预约
	 * 设置会议ID、创建时间和状态，保存会议预约信息，并为邀请的用户和创建用户创建会议预约成员记录
	 *
	 * @param bean 会议预约实体对象，包含会议预约的详细信息，如邀请用户ID、创建用户ID等
	 */
	public void createMeetingReserve(MeetingReserve bean) {
		// 设置会议ID、创建时间和状态
		bean.setMeetingId(StringTools.getMeetingNoOrMeetingId());
		bean.setCreateTime(new Date());
		bean.setStatus(MeetingReserveStatusEnum.NO_START.getStatus());
		this.meetingReserveMapper.insert(bean);
		
		// 初始化会议预约成员列表
		List<MeetingReserveMember> meetingReserveMemberList = new ArrayList<>();
		// 如果存在邀请用户ID，则解析并添加到成员列表
		if (!StringTools.isEmpty(bean.getInviteUserIds())){
			String[] inviteUserIdArray = bean.getInviteUserIds().split(",");
			for (String userId : inviteUserIdArray) {
				MeetingReserveMember member = new MeetingReserveMember();
				member.setMeetingId(bean.getMeetingId());
				member.setInviteUserId(userId);
				meetingReserveMemberList.add(member);
			}
		}
		
		// 将会议创建者也添加到成员列表中
		MeetingReserveMember member = new MeetingReserveMember();
		member.setMeetingId(bean.getMeetingId());
		member.setInviteUserId(bean.getCreateUserId());
		meetingReserveMemberList.add(member);
		
		// 批量插入会议预约成员记录
		meetingReserveMemberMapper.insertBatch(meetingReserveMemberList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	/**
	 * 删除会议预约
	 * 根据会议ID和创建用户ID删除会议预约记录，并同时删除相关的会议成员记录
	 *
	 * @param meetingId 会议ID，用于标识特定的会议预约
	 * @param userId 创建用户ID，用于验证删除权限
	 */
	public void delMeetingReserve(String meetingId, String userId) {
		// 构建查询条件，根据会议ID和创建用户ID删除会议预约
		MeetingReserveQuery meetingReserveQuery = new MeetingReserveQuery();
		meetingReserveQuery.setMeetingId(meetingId);
		meetingReserveQuery.setCreateUserId(userId);
		Integer count = this.meetingReserveMapper.deleteByParam(meetingReserveQuery);
		
		// 如果会议预约删除成功，则删除相关的会议成员记录
		if (count > 0){
			MeetingReserveMemberQuery memberQuery = new MeetingReserveMemberQuery();
			memberQuery.setMeetingId(meetingId);
			this.meetingReserveMemberMapper.deleteByParam(memberQuery);
		}
	}

	/**
	 * 根据用户删除会议预约
	 * 如果是创建者，则删除整个会议预约；如果是被邀请用户，则只删除该用户的成员记录
	 *
	 * @param meetingId 会议ID，用于标识特定的会议预约
	 * @param userId 用户ID，用于确定用户角色和删除权限
	 * @throws BusinessException 当会议预约不存在时抛出异常
	 */
	@Override
	public void delMeetingReserveByUser(String meetingId, String userId) {
		// 查询会议预约信息
		MeetingReserve meetingReserve = this.meetingReserveMapper.selectByMeetingId(meetingId);
		if (meetingReserve == null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		// 判断用户是否为创建者，决定删除方式
		if (userId.equals(meetingReserve.getCreateUserId())){
			delMeetingReserve(meetingId,userId);
		}else {
			// 非创建者用户，只删除其成员记录
			MeetingReserveMemberQuery memberQuery = new MeetingReserveMemberQuery();
			memberQuery.setMeetingId(meetingId);
			memberQuery.setInviteUserId(userId);
			this.meetingReserveMemberMapper.deleteByParam(memberQuery);
		}
	}
}