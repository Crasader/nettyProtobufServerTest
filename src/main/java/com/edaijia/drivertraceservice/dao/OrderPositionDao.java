package com.edaijia.drivertraceservice.dao;

import com.edaijia.drivertraceservice.domain.OrderPositionEntity;
import com.zhouyutong.zorm.annotation.Dao;
import com.zhouyutong.zorm.dao.jdbc.JdbcBaseDao;
import org.springframework.stereotype.Repository;

/**
 * Created by tianhong on 2018/5/20.
 */
@Dao(settingBeanName = "testDbJdbcSettings")
@Repository
public class OrderPositionDao extends JdbcBaseDao<OrderPositionEntity> {
}
