package com.edaijia.drivertraceservice.web;

import com.edaijia.drivertraceservice.domain.DemoDTO;
import com.edaijia.drivertraceservice.server.ChannelManager;
import com.edaijia.drivertraceservice.service.DemoService;
import com.zhouyutong.zapplication.api.Resp;
import io.netty.channel.Channel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * @author zhoutao
 * @Description: 这是一个演示api实现
 * @date 2018/3/5
 */
@Api(tags = {"2.DemoApi"})
@RestController
@RequestMapping(value = "/tcp")
@Slf4j
public class DemoController {
    @Autowired
    private DemoService demoService;

    @ApiOperation(value = "调用远程服务演示接口", notes = "返回数据data为String")
    @GetMapping(path = "/callRemoteService")
    public Resp callRemoteService() {
        String hello = demoService.callRemoteService();
        return Resp.success(hello);
    }

    @ApiOperation(value = "查询演示接口", notes = "返回数据data为数组具体请参考接口说明中的demoDTO说明")
    @GetMapping(path = "/query")
    public Resp query() {
        return Resp.success(demoService.query());
    }

    @ApiOperation(value = "创建演示接口", notes = "返回数据data为String表示创建说明")
    @PostMapping(path = "/create")
    public Resp create(@Valid DemoDTO demoDTO, @ApiIgnore BindingResult bindingResult) {
        String result = demoService.create(demoDTO);
        return Resp.success(result);
    }

    @ApiOperation(value = "redis使用演示接口", notes = "返回数据data为Long型表示PV")
    @GetMapping(path = "/pv")
    public Resp showPv() {
        return Resp.success(demoService.showPv());

    }

    @ApiOperation(value = "创建es演示接口", notes = "返回数据data为String表示创建说明")
    @PostMapping(path = "/es/create")
    public Resp createElasticsearch(@Valid DemoDTO demoDTO, @ApiIgnore BindingResult bindingResult) {
        String result = demoService.createElasticsearch(demoDTO);
        return Resp.success(result);
    }

    @ApiOperation(value = "查询es演示接口", notes = "返回数据data为数组具体请参考接口说明中的demoDTO说明")
    @GetMapping(path = "/es/query")
    public Resp queryElasticsearch() {
        return Resp.success(demoService.queryElasticsearch());
    }

    @ApiOperation(value = "Tcp Server channel statistics", notes = "返回Tcp channel size and status")
    @GetMapping(path = "/channel/info")
    public Resp queryDriverTraceChannelStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("channel size : ").append(ChannelManager.channelMap.size()).append("\n");
        for (String driverId : ChannelManager.channelMap.keySet()) {
            Channel channel = ChannelManager.channelMap.get(driverId);
            sb.append("driverId : ").append(driverId).append(" ---> ").append("channel id ").append(channel.id()).append(" isRegistered ").append(channel.isRegistered()).append(" isActive ").append(channel.isActive()).append(" isOpen ").append(channel.isOpen()).append(" isWritable ").append(channel.isWritable()).append("\n");
        }
        return Resp.success(sb.toString());
    }
}
