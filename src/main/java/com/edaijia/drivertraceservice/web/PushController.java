package com.edaijia.drivertraceservice.web;

import com.edaijia.drivertraceservice.service.PushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by tianhong on 2018/5/17.
 */
@Api(tags = {"PushApi"})
@RestController
@RequestMapping(value = "/push")
@Slf4j
public class PushController {
    @Autowired
    private PushService pushService;


    @ApiOperation(value = "给司机端发push", notes = "无返回数据")
    @PostMapping(path = "/driver")
    public String pushDemo(String driverId, String title, String type, String message) {
        if (StringUtils.isNotBlank(driverId) && StringUtils.isNotBlank(title) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(message))
            pushService.push(driverId, title, type, message);
        return "success";
    }
}
