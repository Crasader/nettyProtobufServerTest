package com.edaijia.drivertraceservice.service;


import com.edaijia.drivertraceservice.domain.protobuf.ParkPushMsg;
import com.edaijia.drivertraceservice.server.ChannelManager;
import io.netty.channel.Channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;


/**
 * Created by tianhong on 2018/5/17.
 */
@Service
@Slf4j
public class PushService {

    public void push(final String driverId, String title, String type, String msg) {
        log.info("push driverId {}, title {}, type {}, msg {}", driverId, title, type, msg);
        if (StringUtils.isNotBlank(driverId) && StringUtils.isNotBlank(title) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(msg)) {
            Channel channel = ChannelManager.channelMap.get(driverId);
            log.info("channelId {}, isOpen {}", channel.id(), channel.isOpen());
            if (channel != null && channel.isOpen()) {
                ParkPushMsg.ParkMsg.Builder parkMsgBuilder = ParkPushMsg.ParkMsg.newBuilder();
                parkMsgBuilder.setResponseId(UUID.randomUUID().toString());
                parkMsgBuilder.setTitle(title);
                parkMsgBuilder.setMessage(msg);
                parkMsgBuilder.setType(type);
                ChannelFuture channelFuture = channel.writeAndFlush(parkMsgBuilder.build());
                channelFuture.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        log.info(Thread.currentThread().getName() + " push to {} success!", driverId);
                    }
                });
            } else {
                log.error("push driverId {}, channel is invalid", driverId);
            }

        }
    }

}
