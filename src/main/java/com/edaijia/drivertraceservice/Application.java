package com.edaijia.drivertraceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.*;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.util.StringUtils;

/**
 * @author zhoutao
 * @Description: 应用启动类
 * @date 2018/3/5
 * <p>
 * 自动配置类排除说明：
 * spring boot默认加载的自动配置类在spring-boot-autoconfigure.jar\META-INF\spring.factories下.
 * 应用因为用了z-orm作为统一dao层框架，因此DataSourceAutoConfiguration的自动化配置也去掉.
 * 如果classPath下有依赖的相关组件类而实际上应用没有使用,如果不去掉相关的自动配置类可能会导致/health监控检测不通过
 */
@SpringBootApplication(
        scanBasePackages = {"com.edaijia.drivertraceservice"},
        exclude = {
                //datasource相关自动配置类排除
                DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, JndiDataSourceAutoConfiguration.class,
                XADataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
                //事物相关自动配置类排除
                TransactionAutoConfiguration.class, JtaAutoConfiguration.class,
                //redis相关自动配置类排除
                RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class,
                //elasticsearch相关自动配置类排除
                ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class,
        }
)
@EnableHystrix
public class Application {
    public static final String NAME = System.getProperty("spring.application.name");

    public static void main(String[] args) throws Exception {
        if (StringUtils.isEmpty(NAME)) {
            throw new Exception("Start application, must set environment variable:spring.application.name");
        }
        new SpringApplication(Application.class).run(args);
    }
}
