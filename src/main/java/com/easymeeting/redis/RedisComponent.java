package com.easymeeting.redis;

import com.easymeeting.entity.constants.Constants;
import com.easymeeting.entity.dto.MeetingMemberDto;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.MeetingMemberStatusEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

    public String saveCheckCode(String code) {
        String checkCodeKey = UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey, code, 60 * 10);
        return checkCodeKey;
    }

    public String getCheckCode(String checkCodeKey) {
        return (String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public void cleanCheckCode(String checkCodeKey) {
        redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public void saveTokenUserInfoDto(TokenUserInfoDto tokenUserInfoDto) {
        // 保存 token -> TokenUserInfoDto
        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN + tokenUserInfoDto.getToken(),
                tokenUserInfoDto,
                Constants.REDIS_KEY_EXPIRES_DAY);
        // 保存 userId -> TokenUserInfoDto
        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN_USERID + tokenUserInfoDto.getUserId(),
                tokenUserInfoDto,
                Constants.REDIS_KEY_EXPIRES_DAY);
    }

    public TokenUserInfoDto getTokenUserInfoDto(String token) {
        return (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN + token);
    }

    public TokenUserInfoDto getTokenUserInfoDtoByUserId(String userId) {
        return (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN_USERID + userId);
    }

    public void add2Meeting(String meetingId, MeetingMemberDto meetingMemberDto) {
        redisUtils.hset(Constants.REDIS_KEY_MEETING_ROOM + meetingId, meetingMemberDto.getUserId(), meetingMemberDto);
    }

    public List<MeetingMemberDto> getMeetingMemberList(String meetingId) {
        List<MeetingMemberDto> meetingMemberDtoList = redisUtils.hvals(Constants.REDIS_KEY_MEETING_ROOM + meetingId);
        meetingMemberDtoList = meetingMemberDtoList.stream()
                .sorted(Comparator.comparing(MeetingMemberDto::getJoinTime))
                .collect(Collectors.toList());
        return meetingMemberDtoList;
    }

    public MeetingMemberDto getMeetingMember(String meetingId, String userId) {
        return (MeetingMemberDto) redisUtils.hget(Constants.REDIS_KEY_MEETING_ROOM + meetingId, userId);
    }

    public Boolean exitMeeting(String meetingId,String userId, MeetingMemberStatusEnum statusEnum) {
        MeetingMemberDto meetingMemberDto = getMeetingMember(meetingId, userId);
        if (meetingMemberDto == null){
            return false;
        }
        meetingMemberDto.setStatus(statusEnum.getStatus());
        add2Meeting(meetingId,meetingMemberDto);
        return true;
    }

    public void removeAllMeetingMember(String meetingId){
        List<MeetingMemberDto> meetingMemberList = getMeetingMemberList(meetingId);
        List<String> userIdList = meetingMemberList.stream().map(MeetingMemberDto::getUserId).collect(Collectors.toList());
        if (userIdList.isEmpty()){
            return;
        }
        redisUtils.hdel(Constants.REDIS_KEY_MEETING_ROOM+meetingId,userIdList.toArray(new String[userIdList.size()]));
    }
}
