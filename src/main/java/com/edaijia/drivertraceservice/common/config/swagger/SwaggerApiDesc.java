package com.edaijia.drivertraceservice.common.config.swagger;

import com.edaijia.drivertraceservice.domain.DemoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author zhoutao
 * @Description: 接口公共说明包括:
 * 1、错误码
 * 2、方法返回对象说明
 * 3、其他的可扩展
 * @date 2018/4/2
 */
@Api(tags = {"1.接口说明"})
@Controller
@RequestMapping(value = "/docs")
public class SwaggerApiDesc {
    @ApiOperation(value = "错误码说明")
    @RequestMapping(path = "/errorCode", method = RequestMethod.HEAD)
    public void errorCode(ErrorCodeDesc errorCodeDesc) {
    }

    @ApiOperation(value = "demoDTO说明")
    @RequestMapping(path = "/demoDTO", method = RequestMethod.HEAD)
    public void demoDTO(DemoDTO demoDTO) {
    }
}
