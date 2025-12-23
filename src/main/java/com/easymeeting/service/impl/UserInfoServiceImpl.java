package com.easymeeting.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.easymeeting.entity.config.AppConfig;
import com.easymeeting.entity.constants.Constants;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.UserStatusEnum;
import com.easymeeting.entity.vo.UserInfoVo;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.redis.RedisComponent;
import com.easymeeting.utils.CopyTools;
import org.springframework.stereotype.Service;

import com.easymeeting.entity.enums.PageSize;
import com.easymeeting.entity.query.UserInfoQuery;
import com.easymeeting.entity.po.UserInfo;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.query.SimplePage;
import com.easymeeting.mappers.UserInfoMapper;
import com.easymeeting.service.UserInfoService;
import com.easymeeting.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private AppConfig appConfig;
    @Resource
    private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(param);
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据UserId获取对象
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId修改
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email获取对象
	 */
	@Override
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email修改
	 */
	@Override
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return this.userInfoMapper.updateByEmail(bean, email);
	}

	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	@Override
	public void register(String email, String nickName, String password) {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if (userInfo != null){
			throw new BusinessException("邮箱已存在");
		}
		Date date = new Date();
		String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
		userInfo = new UserInfo();
		userInfo.setUserId(userId);
		userInfo.setNickName(nickName);
		userInfo.setEmail(email);
		userInfo.setPassword(StringTools.encodeByMD5(password));
		userInfo.setCreateTime(date);
		userInfo.setLastOffTime(date.getTime());
		userInfo.setMeetingNo(StringTools.getMeetingNoOrMeetingId());
		userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
		this.userInfoMapper.insert(userInfo);

	}

	@Override
	public UserInfoVo login(String email, String password) {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if (userInfo == null || !userInfo.getPassword().equals(password)){
			throw new BusinessException("账号或密码不正确");
		}
		if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())){
			throw new BusinessException("账号已禁用");
		}
		if (userInfo.getLastLoginTime() !=null && userInfo.getLastOffTime() <= userInfo.getLastLoginTime()){
			throw new BusinessException("此账号已在别处登录，请退出后在登录");
		}
		TokenUserInfoDto tokenUserInfoDto = CopyTools.copy(userInfo,TokenUserInfoDto.class);
		String token = StringTools.encodeByMD5(tokenUserInfoDto.getUserId() + StringTools.getRandomNumber(Constants.LENGTH_20));
		tokenUserInfoDto.setToken(token);
		tokenUserInfoDto.setMyMeetingNo(userInfo.getMeetingNo());
		tokenUserInfoDto.setAdmin(appConfig.getAdminEmails().contains(email));

		redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);

		UserInfoVo userInfoVo = CopyTools.copy(userInfo,UserInfoVo.class);
		userInfoVo.setToken(token);
		userInfoVo.setAdmin(tokenUserInfoDto.getAdmin());
		return userInfoVo;
	}


}