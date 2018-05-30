package com.edaijia.drivertraceservice.dao;

import com.edaijia.drivertraceservice.domain.DemoEntity;
import com.zhouyutong.zorm.annotation.Dao;
import com.zhouyutong.zorm.dao.jdbc.JdbcBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author zhoutao
 * @Description: 这是一个演示dao实现的演示
 * @date 2018/3/5
 */
@Dao(settingBeanName = "testDbJdbcSettings")
@Repository
public class DemoDao extends JdbcBaseDao<DemoEntity> {
}
