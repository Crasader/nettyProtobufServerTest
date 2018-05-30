package com.edaijia.drivertraceservice.domain;

/**
 * Created by tianhong on 2018/5/17.
 */
public enum ParkMsgType {
    TEXT_MSG,//普通文本
    PAKE_ORDER,//泊车单
    TAKEN_ORDER,//取车单
    GO_BACK_ORDER;//中途折返单
}
