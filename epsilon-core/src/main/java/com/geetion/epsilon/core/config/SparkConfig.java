package com.geetion.epsilon.core.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="spark")
public class SparkConfig {

    @Value("${spark.spark-home}")
    private String sparkHome;
    @Value("${spark.app-name}")
    private String appName;
    @Value("${spark.master}")
    private String master;

    @Bean
    @ConditionalOnMissingBean(SparkConf.class)
    public SparkConf sparkConf(){
        SparkConf conf = new SparkConf()
                .setSparkHome(sparkHome)
                .setAppName(appName)
                .setMaster(master);
        return conf;
    }

    @Bean
    @ConditionalOnMissingBean(SparkSession.class)
    public SparkSession sparkSession(){
        return SparkSession.builder().config(sparkConf()).getOrCreate();
    }
}