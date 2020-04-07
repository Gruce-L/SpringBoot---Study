package com.atguigu.springboot03logging;

import org.junit.jupiter.api.Test;
//import org.junit.platform.commons.logging.Logger;
//import org.junit.platform.commons.logging.LoggerFactory;
//是idea自动导入的包有问题，改成下面这个
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class SpringBoot03LoggingApplicationTests {

    //日志记录器
    Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    void contextLoads() {
        //过时的方式：System.out.println();
        //日志的级别：由低到高
        //可以调整输出的日志级别，日志就只会在这个级别以后的高级别生效
        logger.trace("这是trace日志...");
        logger.debug("这是debug日志");
        //SpringBoot默认给我们使用的是info及以上级别的
        //要是容trace到error全部输出，需要在application.properties中添加下面的话
        //logging.level.com.atguigu = trace
        //没有指定级别的，就用SpringBoot默认级别的：root级别
        logger.info("这是info日志...");
        logger.warn("这是warn日志...");
        logger.error("这是error的包...");
    }
}
