package com.edaijia.drivertraceservice.server;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tianhong on 2018/5/11.
 */
public class ChannelManager {
    public static volatile Map<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>();
}
