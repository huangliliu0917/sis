package com.huotu.sis.boot;

import com.huotu.huobanplus.common.CommonServiceSpringConfig;
import com.huotu.huobanplus.sdk.common.CommonClientSpringConfig;
import com.huotu.huobanplus.sdk.mall.MallSDKConfig;
import com.huotu.huobanplus.smartui.SmartUIServiceSpringConfig;
import org.luffy.lib.libspring.data.ClassicsRepositoryFactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by lgh on 2016/3/22.
 */

@Configuration
//@EnableTransactionManagement(mode = AdviceMode.PROXY)
@ComponentScan({"com.huotu.sis.service"})
@EnableJpaRepositories(value = "com.huotu.sis.repository", repositoryFactoryBeanClass = ClassicsRepositoryFactoryBean.class)
@ImportResource(value = {"classpath:spring-jpa.xml"})
@Import(value = {CommonClientSpringConfig.class, MallSDKConfig.class, CommonServiceSpringConfig.class, SmartUIServiceSpringConfig.class})
public class RootConfig {


}
