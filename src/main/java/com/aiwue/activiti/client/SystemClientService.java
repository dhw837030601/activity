package com.aiwue.activiti.client;

import com.aiwue.common.exception.AiwueException;
import com.aiwue.common.request.system.GetForeignUserInfoRequest;
import com.aiwue.common.response.RespEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @author cf
 * @Description
 * @Date 2018/12/7 11:25
 */
@FeignClient(value  = "service-system")
public interface SystemClientService {
    @RequestMapping( value = "system/user/foreign/getUserInfo", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_UTF8_VALUE ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("对外获取用户详情")
    public RespEntity getForeignUserInfo(@Valid @RequestBody GetForeignUserInfoRequest request) throws AiwueException ;


}
