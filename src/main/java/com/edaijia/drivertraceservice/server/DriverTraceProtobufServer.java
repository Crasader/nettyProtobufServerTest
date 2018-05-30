package com.edaijia.drivertraceservice.server;

import com.edaijia.drivertraceservice.domain.ParkMsgType;
import com.edaijia.drivertraceservice.domain.protobuf.DriverTrace;
import com.edaijia.drivertraceservice.domain.protobuf.ParkPushMsg;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by tianhong on 2018/5/4.
 */
@Component
@Slf4j
public class DriverTraceProtobufServer {

    @Value("${netty.boss.thread-count}")
    private int bossThreadCount;
    @Value("${netty.worker.thread-count}")
    private int workerThreadCount;
    @Value("${netty.tcp.port}")
    private int tcpPort;
    @Value("${netty.so.keepalive}")
    private boolean soKeepalive;
    @Value("${netty.so.backlog}")
    private int soBacklog;


    private void bind(int port) throws Exception {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        log.info("cpu count is {}, bossThreadCount {}, workerThreadCount {}, tcpPort {}, soKeepalive {}, soBacklog {}", cpuCount, bossThreadCount, workerThreadCount, tcpPort, soKeepalive, soBacklog);
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossThreadCount);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadCount);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ProtobufDecoder(DriverTrace.DriverTraceMsg.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new ProtobufServerHandler());
                        }
                    });

            Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
            Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
            for (@SuppressWarnings("rawtypes")
                    ChannelOption option : keySet) {
                b.option(option, tcpChannelOptions.get(option));
            }

            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
        options.put(ChannelOption.SO_KEEPALIVE, soKeepalive);
        /**
         * 未链接队列和已链接队列总和的最大值，tcp3次握手区分的这两个队列，netty默认100
         */
        options.put(ChannelOption.SO_BACKLOG, soBacklog);
        return options;
    }

    @PostConstruct
    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("DriverTraceProtobufServer start listen......");
                    bind(tcpPort);
                } catch (Exception e) {
                    log.error("DriverTraceProtobufServer init error !!!");
                }
            }
        }).start();
        //发送心跳
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    log.info("-----------TcpHeartBeatThread-----------");
                    for (String driverId : ChannelManager.channelMap.keySet()) {
                        Channel channel = ChannelManager.channelMap.get(driverId);
                        if (channel != null && channel.isOpen()) {
                            ParkPushMsg.ParkMsg.Builder parkMsgBuilder = ParkPushMsg.ParkMsg.newBuilder();
                            parkMsgBuilder.setResponseId(UUID.randomUUID().toString());
                            parkMsgBuilder.setTitle("heart beat");
                            parkMsgBuilder.setMessage("hi " + driverId);
                            parkMsgBuilder.setType(ParkMsgType.TEXT_MSG.ordinal() + "");
                            ChannelFuture channelFuture = channel.writeAndFlush(parkMsgBuilder.build());
                            channelFuture.addListener(new ChannelFutureListener() {
                                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                    log.info(Thread.currentThread().getName() + " " + driverId + " heart beat success!");
                                }
                            });
                        }
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //监控服务端channel
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                    }

                    for (String channelKey : ChannelManager.channelMap.keySet()) {
                        Channel channel = ChannelManager.channelMap.get(channelKey);
                        if (channel != null) {
                            if (!channel.isOpen()) {
                                ChannelManager.channelMap.remove(channelKey);
                            } else {
                                log.info("server channel {} isRegistered {} |isActive {} |isOpen {} |isWritable {} : ", channelKey, channel.isRegistered(), channel.isActive(), channel.isOpen(), channel.isWritable());
                            }

                        }
                    }
                }
            }
        }).start();
    }

    @PreDestroy
    private void stop() {
        log.error("DriverTraceProtobufServer stop !!!");
    }

}
