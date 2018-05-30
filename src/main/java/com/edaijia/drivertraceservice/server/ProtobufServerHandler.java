package com.edaijia.drivertraceservice.server;

import com.edaijia.drivertraceservice.domain.protobuf.DriverTrace;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by tianhong on 2018/5/11.
 */
@Slf4j
public class ProtobufServerHandler extends ChannelInboundHandlerAdapter {



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            DriverTrace.DriverTraceMsg req = (DriverTrace.DriverTraceMsg) msg;
            Channel channel = ctx.channel();
            if (!ChannelManager.channelMap.containsKey(req.getDriverId())) {
                ChannelManager.channelMap.put(req.getDriverId(), channel);
            }
            if (req.getPointCount() > 0) {
                //正常上传轨迹
                //设置环境变量，写入kafka
                //MqProducer.send(MqTopics.DRIVER_LOCATION_NEW, MqTopics.DRIVER_LOCATION_NEW.getTopic(), req.toString());
                log.info("receive DriverTraceMsg:\n {}", req.toString());
                //todo 因为逻辑比较简单，不必要加线程池
            }
        } catch (Exception e) {
            log.error("ProtobufServerHandler channelRead DriverTraceMsg error", e);
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("ProtobufServerHandler exceptionCaught", cause);
        //发生异常，关闭链路
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //tcp链路建立成功
        Channel channel = ctx.channel();
        log.info("ProtobufServerHandler channelActive, tcp establish success, channel id {}, remote client ip {}, local server ip {}", channel.id(), channel.remoteAddress().toString(), channel.localAddress().toString());
    }
}
