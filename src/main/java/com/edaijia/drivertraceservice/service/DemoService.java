package com.edaijia.drivertraceservice.service;

import com.edaijia.drivertraceservice.common.config.redis.RedisTemplateWarpper;
import com.edaijia.drivertraceservice.dao.DemoDao;
import com.edaijia.drivertraceservice.dao.DemoEsDao;
import com.edaijia.drivertraceservice.domain.DemoDTO;
import com.edaijia.drivertraceservice.domain.DemoEntity;
import com.zhouyutong.zapplication.utils.DateUtils;
import com.zhouyutong.zorm.dao.jdbc.transaction.TransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhoutao
 * @Description: 这是一个service演示
 * @date 2018/3/5
 */
@Service
@Slf4j
public class DemoService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DemoDao demoDao;
    @Autowired
    private DemoEsDao demoEsDao;
    @Autowired
    private TransactionManager transactionManager;
    @Autowired
    private RedisTemplateWarpper redisTemplateWarpper;

    /**
     * sayHell接口具体实现
     * 注意：1、使用了hystrix做限流降级断路，一般情况不要使用，除非接口并发特别高或者是需要降级的接口再使用.
     * 2、降级方法必须和原方法在同一个类里，返回值必须类型一致
     * 3、以下列举常用属性配置,默认配置在com.netflix.hystrix.HystrixCommandProperties类
     * 4、方法默认的commandKey是方法名
     *
     * @return
     */
//    @HystrixCommand(fallbackMethod = "callRemoteServiceFallback",
//            commandProperties = {
//                    //隔离策略,默认线程池策略
//                    @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
//                    //执行超时时间,默认1000ms,hystrix 1.4.0开始semaphore-isolated也支持超时
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
//                    //Semaphore策略下,最大请求并发数,默认10
//                    @HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests", value = "2000"),
//                    //开启断路器功能,默认true
//                    @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
//                    //强制打开断路器,默认false
//                    @HystrixProperty(name = "circuitBreaker.forceOpen", value = "false"),
//                    //强制关闭断路器,默认false
//                    @HystrixProperty(name = "circuitBreaker.forceClosed", value = "false"),
//                    //默认10秒内达到此数量的失败后，进行短路。默认失败20次
//                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3"),
//                    //短路多久以后开始尝试恢复，默认5s
//                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
//            }
//    )
    public String callRemoteService() {
        //基于注册中心的远程服务地址
        String remoteService = "http://localhost:8081/helloWorld/java";
        return restTemplate.getForObject(remoteService, String.class);
    }

    private String callRemoteServiceFallback(Throwable e) {
        log.error("sayHello接口失败发生了降级处理", e);
        return "服务繁忙，请稍后重试";
    }

    /**
     * create接口的具体实现
     * 注意：没有特殊要求的接口不要使用@HystrixCommand
     *
     * @return
     */
    public String create(DemoDTO demoDTO) {
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setTitle(demoDTO.getTitle());
        demoEntity.setUserName(demoDTO.getUserName());
        demoEntity.setCreateTime(DateUtils.formatNow2Long());
        //不需要doInTransaction只是演示用
        transactionManager.doInTransaction(() -> {
            demoDao.insert(demoEntity);
            return null;
        });
        return "创建成功ID=" + demoEntity.getId();
    }

    /**
     * query接口的具体实现
     * 注意：没有特殊要求的接口不要使用@HystrixCommand
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<DemoDTO> query() {
        //缓存里获取
        String queryKey = "demo:dtoList";
        List demoDTOList = redisTemplateWarpper.lRange(queryKey, 0L, -1L);
        if (CollectionUtils.isNotEmpty(demoDTOList)) {
            return demoDTOList;
        }

        //db里获取
        List<DemoEntity> demoEntityList = demoDao.findAllList();
        if (CollectionUtils.isEmpty(demoEntityList)) {
            return Collections.EMPTY_LIST;
        }

        //DTO格式转换
        List demoDtoList = demoEntityList.stream().map(demoEntity -> {
            DemoDTO demoDTO = new DemoDTO();
            demoDTO.setId(demoEntity.getId());
            demoDTO.setTitle(demoEntity.getTitle());
            demoDTO.setUserName(demoEntity.getUserName());
            return demoDTO;
        }).collect(Collectors.toList());

        //写缓存
        if (redisTemplateWarpper.lLeftPushAll(queryKey, demoDtoList) > 0L) {
            redisTemplateWarpper.kExpire(queryKey, 1L, TimeUnit.MINUTES);
        }
        return demoDtoList;
    }

    /**
     * pv接口的具体实现
     * 演示redis使用
     *
     * @return
     */
    public Long showPv() {
        String pvKey = "demo:pv";
        Long count = redisTemplateWarpper.vIncr(pvKey);
        return count;
    }

    /**
     * createElasticsearch接口的具体实现
     * 注意：没有特殊要求的接口不要使用@HystrixCommand
     *
     * @return
     */
    public String createElasticsearch(DemoDTO demoDTO) {
        DemoEntity demoEsEntity = new DemoEntity();
        demoEsEntity.setId(demoDTO.getId());
        demoEsEntity.setUserName(demoDTO.getUserName());
        demoEsEntity.setTitle(demoDTO.getTitle());
        demoEsEntity.setCreateTime(DateUtils.formatNow2Long());
        demoEsDao.insert(demoEsEntity);
        return "创建成功ID=" + demoEsEntity.getId();
    }

    /**
     * query接口的具体实现
     * 注意：没有特殊要求的接口不要使用@HystrixCommand
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<DemoDTO> queryElasticsearch() {
        //es里获取
        List<DemoEntity> demoEsEntityList = demoEsDao.findAllList();
        if (CollectionUtils.isEmpty(demoEsEntityList)) {
            return Collections.EMPTY_LIST;
        }
        //DTO格式转换
        List demoDtoList = demoEsEntityList.stream().map(demoEsEntity -> {
            DemoDTO demoDTO = new DemoDTO();
            demoDTO.setId(demoEsEntity.getId());
            demoDTO.setTitle(demoEsEntity.getTitle());
            demoDTO.setUserName(demoEsEntity.getUserName());
            return demoDTO;
        }).collect(Collectors.toList());
        return demoDtoList;
    }
}
