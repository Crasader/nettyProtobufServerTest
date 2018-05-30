package com.edaijia.drivertraceservice.server;

import com.edaijia.drivertraceservice.domain.protobuf.DriverTrace;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tianhong on 2018/5/11.
 */
@Slf4j
public class ProtobufServerHandler extends ChannelInboundHandlerAdapter {

    public AtomicLong HANDLE_RECEIVE_COUNT = new AtomicLong(0);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("msg {}", msg.toString());
            //DriverTrace.DriverTraceMsg req = (DriverTrace.DriverTraceMsg) msg;
            Channel channel = ctx.channel();
//            if (!ChannelManager.channelMap.containsKey(req.getDriverId())) {
//                ChannelManager.channelMap.put(req.getDriverId(), channel);
//            }

            log.info("channel size {}", ChannelManager.channelMap.size());
            log.info("channelId {}, global {}, handler {}", channel.id(),ChannelManager.RECEIVE_COUNT.incrementAndGet(),HANDLE_RECEIVE_COUNT.incrementAndGet());
            ctx.writeAndFlush("你也hao");
            //if (req.getPointCount() > 0) {
                //正常上传轨迹
                //设置环境变量，写入kafka
                //MqProducer.send(MqTopics.DRIVER_LOCATION_NEW, MqTopics.DRIVER_LOCATION_NEW.getTopic(), req.toString());
                //log.info("receive DriverTraceMsg size: {}", req.getPointCount());
                //log.info("msg 16hex str:{}", new String(Hex.encodeHex(req.toByteArray())));
                //todo 因为逻辑比较简单，不必要加线程池
            //}
        } catch (Exception e) {
            log.error("ProtobufServerHandler channelRead DriverTraceMsg error", e);
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.error(Thread.currentThread().getName() + " 已经30分钟未收到客户端的消息了！-IdleStateHandlerstep1");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.error(Thread.currentThread().getName() + " 关闭这个不活跃通道!-IdleStateHandlerstep2");
                ctx.channel().close();
            }
        } else {
            //客户端主动断的时候会走到这
            log.error(Thread.currentThread().getName() + " 客户端主动断了！-IdleStateHandlerstep3");
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("ProtobufServerHandler exceptionCaught", cause);
        //发生异常，关闭链路
        //客户端主动关闭从这里感知到
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //tcp链路建立成功
        Channel channel = ctx.channel();
        if (!ChannelManager.channelMap.containsKey(channel.id())) {
            ChannelManager.channelMap.put(channel.id()+"", channel);
        }
        log.info("ProtobufServerHandler channelActive, tcp establish success, channel id {}, remote client ip {}, local server ip {}", channel.id(), channel.remoteAddress().toString(), channel.localAddress().toString());
    }
}
