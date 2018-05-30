package com.edaijia.drivertraceservice.service;

import com.edaijia.drivertraceservice.dao.OrderPositionDao;
import com.edaijia.drivertraceservice.domain.OrderPositionEntity;
import com.edaijia.drivertraceservice.domain.protobuf.DriverTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by tianhong on 2018/5/20.
 */
@Service
@Slf4j
public class OrderPositionService {
    @Autowired
    private OrderPositionDao orderPositionDao;

    public int saveOrderPosition(DriverTrace.DriverTraceMsg driverTraceMsg) {
        if (driverTraceMsg != null) {
            for (int i = 0; i < driverTraceMsg.getPointCount(); i++) {
                OrderPositionEntity entity = new OrderPositionEntity();
                entity.setOrderId(Long.parseLong(driverTraceMsg.getOrderId()));
                entity.setPositions("");//todo
                entity.setCreateTime(new Date());
                entity.setAccuracy(driverTraceMsg.getPoint(i).getAccuracy() + "");
                entity.setGpsType(driverTraceMsg.getPoint(i).getGpsType());
                entity.setLat(driverTraceMsg.getPoint(i).getLat() + "");
                entity.setLng(driverTraceMsg.getPoint(i).getLng() + "");
                entity.setLocate_time(driverTraceMsg.getPoint(i).getGpsTime());
                entity.setProvider(driverTraceMsg.getPoint(i).getProvider());
                entity.setTimestamp(driverTraceMsg.getPoint(i).getCreateTimeMilli());
                orderPositionDao.insert(entity);
            }
        }
        return driverTraceMsg.getPointCount();
    }
}
