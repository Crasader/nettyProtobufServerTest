package com.edaijia.drivertraceservice.common.config;

import com.zhouyutong.zorm.dao.elasticsearch.ElasticSearchSettings;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoutao
 * @Description: es的配置
 * @date 2018/3/8
 */
@Configuration
@Getter
public class ElasticSearchConfig {
    @Value("${elasticsearch.cluster-name}")
    private String clusterName;
    @Value("${elasticsearch.cluster-nodes}")
    private String clusterNodes;

    /**
     * @return
     */
    @Bean(name = "elasticSearchSettings")
    ElasticSearchSettings elasticSearchSettings() {
        ElasticSearchSettings settings = new ElasticSearchSettings();
        settings.setClusterName(clusterName);
        settings.setServerAddressList(clusterNodes);
        return settings;
    }
}
