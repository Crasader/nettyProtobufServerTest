package com.edaijia.drivertraceservice.web;

import com.edaijia.drivertraceservice.server.ChannelManager;
import io.netty.channel.Channel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @ApiOperation(value = "Tcp Server channel statistics", notes = "返回Tcp channel size and status")
    @GetMapping(path = "/channel/info")
    public String queryDriverTraceChannelStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("channel size : ").append(ChannelManager.channelMap.size()).append("\n");
        for (String driverId : ChannelManager.channelMap.keySet()) {
            Channel channel = ChannelManager.channelMap.get(driverId);
            sb.append("driverId : ").append(driverId).append(" ---> ").append("channel id ").append(channel.id()).append(" isRegistered ").append(channel.isRegistered()).append(" isActive ").append(channel.isActive()).append(" isOpen ").append(channel.isOpen()).append(" isWritable ").append(channel.isWritable()).append("\n");
        }
        return sb.toString();
    }
}
