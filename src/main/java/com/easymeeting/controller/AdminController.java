package com.easymeeting.controller;

import com.easymeeting.annotition.GlobalInterceptor;
import com.easymeeting.entity.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Validated
@Slf4j
public class AdminController extends ABaseController{

    @RequestMapping("/loadUser")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadUser(Integer pageNo){
        return getSuccessResponseVO(null);
    }
}
