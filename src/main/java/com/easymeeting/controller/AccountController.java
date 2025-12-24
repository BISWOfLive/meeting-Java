package com.easymeeting.controller;

import com.easymeeting.entity.vo.ResponseVO;
import com.easymeeting.entity.vo.checkCodeVO;
import com.easymeeting.entity.vo.UserInfoVo;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.redis.RedisComponent;
import com.easymeeting.service.UserInfoService;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Controller
 */
@RestController
@RequestMapping("/account")
@Validated
@Slf4j
public class AccountController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/checkCode")
    public ResponseVO checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String base64 = captcha.toBase64();
        log.info("code:{}", code);
        String checkCodeKey = redisComponent.saveCheckCode(code);
        checkCodeVO checkCodeVO = new checkCodeVO();
        checkCodeVO.setCheckCode(base64);
        checkCodeVO.setCheckCodeKey(checkCodeKey);
        return getSuccessResponseVO(checkCodeVO);
    }

    @RequestMapping("/register")
    public ResponseVO register(@NotEmpty String checkCodeKey,
                               @NotEmpty @Email String email,
                               @NotEmpty @Size(max = 20) String password,
                               @NotEmpty @Size(max = 20) String nickName,
                               @NotEmpty String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
                throw new BusinessException("图片验证码不正确");
            }
            this.userInfoService.register(email, nickName, password);
            return getSuccessResponseVO(null);
        } finally{
            redisComponent.cleanCheckCode(checkCodeKey);
        }
    }

    @RequestMapping("/login")
    public ResponseVO login(@NotEmpty String checkCodeKey,
                            @NotEmpty @Email String email,
                            @NotEmpty @Size(max = 32) String password,
                            @NotEmpty String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
                throw new BusinessException("图片验证码不正确");
            }
            UserInfoVo userInfoVo = this.userInfoService.login(email, password);
            return getSuccessResponseVO(userInfoVo);
        } finally {
            redisComponent.cleanCheckCode(checkCodeKey);
        }
    }
}