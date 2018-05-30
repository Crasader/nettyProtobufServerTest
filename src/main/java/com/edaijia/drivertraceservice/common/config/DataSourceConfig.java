package com.edaijia.drivertraceservice.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import com.zhouyutong.zorm.dao.jdbc.JdbcSettings;
import com.zhouyutong.zorm.dao.jdbc.transaction.TransactionManager;
import com.zhouyutong.zorm.enums.DialectEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author zhoutao
 * @Description: 关系数据库的配置
 * @date 2018/3/8
 */
@Configuration
@Getter
public class DataSourceConfig {
    @Value("${dataSource.testDbMaster.driverClassName}")
    private String driverClassName;
    @Value("${dataSource.testDbMaster.url}")
    private String url;
    @Value("${dataSource.testDbMaster.username}")
    private String username;
    @Value("${dataSource.testDbMaster.password}")
    private String password;

    @Value("${druid.initialSize}")
    private int initialSize;
    @Value("${druid.minIdle}")
    private int minIdle;
    @Value("${druid.maxActive}")
    private int maxActive;
    @Value("${druid.maxWait}")
    private int maxWait;
    @Value("${druid.useUnfairLock}")
    private boolean useUnfairLock;
    @Value("${druid.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis;
    @Value("${druid.minEvictableIdleTimeMillis}")
    private long minEvictableIdleTimeMillis;
    @Value("${druid.poolPreparedStatements}")
    private boolean poolPreparedStatements;
    @Value("${druid.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;
    @Value("${druid.validationQuery}")
    private String validationQuery;
    @Value("${druid.testWhileIdle}")
    private boolean testWhileIdle;
    @Value("${druid.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${druid.testOnReturn}")
    private boolean testOnReturn;

    /**
     * 主库JdbcSettings
     *
     * @return
     */
    @Bean(name = "testDbJdbcSettings")
    JdbcSettings jdbcSettings() {
        DruidDataSource masterDataSource = newDruidDataSource(this.getUrl(), this.getUsername(), this.getPassword());
        List<DataSource> dataSourceList = Lists.newArrayList(masterDataSource);
        return newJdbcSettings(dataSourceList, dataSourceList);
    }

    /**
     * 主库TransactionManager
     *
     * @return
     */
    @Bean(name = "testDbTransactionManager")
    TransactionManager transactionManager() {
        DruidDataSource masterDataSource = newDruidDataSource(this.getUrl(), this.getUsername(), this.getPassword());
        PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(masterDataSource);
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.setTxManager(platformTransactionManager);
        return transactionManager;
    }

    private JdbcSettings newJdbcSettings(List<DataSource> masterDataSourceList, List<DataSource> slaveDataSourceList) {
        JdbcSettings jdbcSettings = new JdbcSettings();
        jdbcSettings.setDialectEnum(DialectEnum.MYSQL);
        jdbcSettings.setWriteDataSource(Lists.newArrayList(masterDataSourceList));
        jdbcSettings.setReadDataSource(Lists.newArrayList(slaveDataSourceList));
        return jdbcSettings;
    }

    /**
     * druid连接池配置
     * 不配置filters,因为引入spring-boot-admin和z-orm
     * 需要可配置stat(统计),wall(防sql注入)
     */
    private DruidDataSource newDruidDataSource(String url, String username, String password) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(this.getDriverClassName());
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setInitialSize(this.getInitialSize());
        druidDataSource.setMinIdle(this.getMinIdle());
        druidDataSource.setMaxActive(this.getMaxActive());
        druidDataSource.setUseUnfairLock(this.isUseUnfairLock());
        druidDataSource.setTimeBetweenEvictionRunsMillis(this.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(this.getMinEvictableIdleTimeMillis());
        druidDataSource.setPoolPreparedStatements(this.isPoolPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(this.getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setValidationQuery(this.getValidationQuery());
        druidDataSource.setTestWhileIdle(this.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(this.isTestOnBorrow());
        druidDataSource.setTestOnReturn(this.isTestOnReturn());
        return druidDataSource;
    }
}
