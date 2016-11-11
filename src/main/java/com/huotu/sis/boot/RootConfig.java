package com.huotu.sis.boot;

import com.huotu.huobanplus.sdk.common.CommonClientSpringConfig;
import com.huotu.huobanplus.sdk.mall.MallSDKConfig;
import com.huotu.huobanplus.smartui.common.SmartuiClientSpringConfig;
import org.luffy.lib.libspring.logging.LoggingConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * root
 * Created by lgh on 2016/3/22.
 */

@Configuration
//@EnableTransactionManagement(mode = AdviceMode.PROXY)
@ComponentScan(basePackages = {"com.huotu.sis.service","com.huotu.sis.controller"})
@EnableJpaRepositories(value = {"com.huotu.sis.repository"})

@ImportResource(value = {"classpath:spring-jpa.xml"})
//@PropertySource("classpath:/sms.properties")
@Import(value = {CommonClientSpringConfig.class,
        MallSDKConfig.class,
//        CommonServiceSpringConfig.class,
//        SmartUIServiceSpringConfig.class,
//        DataSupportConfig.class,
//        ResourceConfig.class,
//        DataServiceSpringConfig.class,
//        SmartUIDataServiceSpringConfig.class,
        SmartuiClientSpringConfig.class,
        LoggingConfig.class
})
public class RootConfig {
//    @Autowired
//    Environment env;
//
//    @Bean
//    public SMSInfo smsInfo() {
//        SMSInfo smsInfo = new SMSInfo();
//        smsInfo.setServerUrl(env.getProperty("serverUrl"));
//        smsInfo.setAccount(env.getProperty("account"));
//        smsInfo.setPswd(env.getProperty("pswd"));
//        return smsInfo;
//    }

//    @Bean
//    public PropertiesFactoryBean propertiesFactoryBean(){
//        PropertiesFactoryBean propertiesFactoryBean=new PropertiesFactoryBean();
//        propertiesFactoryBean.setProperties(new Properties());
//    }
//
//    @Bean
//    public PreferencesPlaceholderConfigurer placeholderConfigurer(){
//        PreferencesPlaceholderConfigurer placeholderConfigurer=new PreferencesPlaceholderConfigurer();
//        placeholderConfigurer.setProperties();
//    }


}
