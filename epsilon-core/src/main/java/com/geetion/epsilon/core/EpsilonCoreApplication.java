package com.geetion.epsilon.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * mybatis-plus Spring Boot 测试 Demo<br>
 * 文档：http://mp.baomidou.com<br>
 */
@SpringBootApplication
public class EpsilonCoreApplication {

    protected final static Logger logger = LoggerFactory.getLogger(EpsilonCoreApplication.class);
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(EpsilonCoreApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}
