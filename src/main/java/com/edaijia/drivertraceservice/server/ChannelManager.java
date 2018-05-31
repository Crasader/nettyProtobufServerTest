package com.edaijia.drivertraceservice.server;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tianhong on 2018/5/11.
 */
public class ChannelManager {
    public static volatile Map<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>();
    //public static AtomicLong RECEIVE_COUNT = new AtomicLong(0);
}
