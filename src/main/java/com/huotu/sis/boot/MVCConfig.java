package com.huotu.sis.boot;

import com.huotu.common.api.ApiResultHandler;
import com.huotu.common.api.OutputHandler;
import com.huotu.sis.common.WebHandlerExceptionResolver;
import com.huotu.sis.interceptor.CommonUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lgh on 2016/1/12.
 */
@Configuration
@EnableWebMvc
public class MVCConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment environment;


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    CommonUserInterceptor commonUserInterceptor() {
        return new CommonUserInterceptor();
    }

    @Autowired
    private CommonUserInterceptor commonUserInterceptor;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/sisweb/**/*", "/**/*.html")
                .addResourceLocations("/sisweb/", "/");
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(commonUserInterceptor)
                .addPathPatterns("/sisweb/**")
                .excludePathPatterns("/sisweb/auth","/sisweb/appLogin") ;

    }


    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new WebHandlerExceptionResolver());

    }


    /**
     * 设置控制器方法参数化输出
     * @param argumentResolvers
     */
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new OutputHandler());
    }

    /**
     * 监听 控制器的ApiResult返回值
     * @param returnValueHandlers
     */
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        returnValueHandlers.add(new ApiResultHandler());
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
        converters.add(converter);
    }

    public void configureViewResolvers(ViewResolverRegistry registry) {
        super.configureViewResolvers(registry);
        registry.viewResolver(viewResolver());
        registry.jsp();
    }

    /**
     * for upload
     */
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {

        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        SpringTemplateEngine engine = new SpringTemplateEngine();
        ServletContextTemplateResolver rootTemplateResolver = new ServletContextTemplateResolver(webApplicationContext.getServletContext());
        rootTemplateResolver.setPrefix("/");
        rootTemplateResolver.setSuffix(".html");
        rootTemplateResolver.setTemplateMode(TemplateMode.HTML);
        rootTemplateResolver.setCharacterEncoding("UTF-8");
        // start cache
        if(environment.acceptsProfiles("development")||environment.acceptsProfiles("develop")){
            rootTemplateResolver.setCacheable(false);
        }
        engine.setTemplateResolver(rootTemplateResolver);
        resolver.setContentType("text/html;charset=utf-8");
        resolver.setTemplateEngine(engine);
        resolver.setOrder(2147483647 + 10);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setExcludedViewNames(new String[]{
                "content/**"
        });
        return resolver;
    }

//    /**
//     * THYMELEAF: View Resolver - implementation of Spring's ViewResolver interface.
//     */
//    @Bean
//    public ViewResolver viewResolver() {
//        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
//        viewResolver.setTemplateEngine(templateEngine());
//        viewResolver.setContentType("text/html;charset=utf-8");
//        viewResolver.setOrder(2147483647 + 10);
//        viewResolver.setCharacterEncoding("UTF-8");
//        viewResolver.setExcludedViewNames(new String[]{
//                "content/**"
//        });
//        return viewResolver;
//    }
//
//    /**
//     * THYMELEAF: Template Engine (Spring4-specific version).
//     */
//    @Bean
//    public SpringTemplateEngine templateEngine() {
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//        templateEngine.addTemplateResolver(webTemplateResolver());
//        return templateEngine;
//    }
//
//
//    /**
//     * THYMELEAF: Template Resolver for webapp pages.
//     */
//    private ITemplateResolver webTemplateResolver() {
//        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
//        ServletContext servletContext = webApplicationContext.getServletContext();
//        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
//
//        templateResolver.setPrefix("/");
//        templateResolver.setSuffix(".html");
//        templateResolver.setCharacterEncoding("UTF-8");
//        // start cache
//        if(environment.acceptsProfiles("development")||environment.acceptsProfiles("develop")){
//            templateResolver.setCacheable(false);
//        }
//        return templateResolver;
//    }

}
