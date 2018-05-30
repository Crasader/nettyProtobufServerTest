package com.edaijia.drivertraceservice.common.config;

import com.zhouyutong.zapplication.httpclient.HttpClientUtils;
import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhoutao
 * @Description: rest 客户端配置类,超时默认10秒HttpClientUtils.DEFAULT_REQUEST_TIMEOUT
 * @date 2018/3/5
 */
@Configuration
public class RestClientConfig {

    /**
     * 注意：RestTemplate加了@LoadBalanced后只能调用注册中心的服务
     * 如果调用第三方的http服务，使用HttpClientUtils
     *
     * @return
     */
    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        //restTemplate默认使用java.net.UrlConnection,需要替换成HttpClient
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        return restTemplate;
    }

    @Bean
    ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpClientUtils httpClientUtils = httpClientUtils();
        HttpClient httpClient = httpClientUtils.getCloseableHttpClient();
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        return clientHttpRequestFactory;
    }

    @Bean
    HttpClientUtils httpClientUtils() {
        HttpClientUtils httpClientUtils = new HttpClientUtils();
        return httpClientUtils;
    }
}
