package com.easymeeting.aspect;

import com.easymeeting.annotition.GlobalInterceptor;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.ResponseCodeEnum;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.redis.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


@Component
@Aspect
@Slf4j
public class GlobalOperationAspect {

    @Resource
    RedisComponent redisComponent;

    @Before("@annotation(com.easymeeting.annotition.GlobalInterceptor)")
    public void interceptorDo(JoinPoint point) {
        try {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            if (interceptor == null) return;
            if (interceptor.checkAdmin() || interceptor.checkLogin()){
                checkLogin(interceptor.checkAdmin());
            }
        }catch (BusinessException e){
            log.error("全局拦截器异常",e);
            throw e;
        }catch (Exception e){
            log.error("全局拦截器异常",e);
            throw  new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }


    private void checkLogin(boolean checkAdmin) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        TokenUserInfoDto userTokenInfoDTO = redisComponent.getTokenUserInfoDto(token);
        if (userTokenInfoDTO == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        if (checkAdmin && !userTokenInfoDTO.getAdmin()){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
}
