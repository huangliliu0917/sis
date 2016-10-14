package cm.huotu.sis.common;

import cm.huotu.sis.filter.ParamFilter;
import cm.huotu.sis.pages.AbstractPage;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by jinzj on 2016/3/24.
 */
public class SpringWebTest {


    /**
     * 自动注入应用程序上下文
     **/
    @Autowired
    protected WebApplicationContext context;
    /**
     * 自动注入servlet上下文
     **/
    @Autowired
    protected ServletContext servletContext;
//    /**
//     * 选配 只有在SecurityConfig起作用的情况下
//     **/
//    @SuppressWarnings("SpringJavaAutowiringInspection")
//    @Autowired(required = false)
//    private FilterChainProxy springSecurityFilter;

    /**
     * mock请求
     **/
    @Autowired
    protected MockHttpServletRequest request;

    /**
     * mockMvc等待初始化
     **/
    protected MockMvc mockMvc;
//    protected WebClient webClient;
    protected WebDriver webDriver;

    @PersistenceContext(unitName = "entityManager")
    protected EntityManager entityManager;

    @Resource(name = "entityManagerFactory")
    protected EntityManagerFactory entityManagerFactory;

    @Resource(name = "transactionManager")
    protected JpaTransactionManager transactionManager;

    @Autowired
    private ParamFilter paramFilter;

    protected void createMockMVC() {
        MockitoAnnotations.initMocks(this);
//        if (springSecurityFilter != null)
//            mockMvc = webAppContextSetup(context)
//                    .addFilters(springSecurityFilter)
//                    .build();
//        else
//            mockMvc = webAppContextSetup(context)
//                    .build();
//        mockMvc = webAppContextSetup(context)
//                .addFilters(paramFilter)
//                .build();
        mockMvc = webAppContextSetup(context)
                .build();
    }
    @Before
    public void initTest(){
        //初始化mockMvc
        this.createMockMVC();
//        this.webClient = MockMvcWebClientBuilder
//                .mockMvcSetup(this.mockMvc)
//                .build();
        this.webDriver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(this.mockMvc)
                .build();
    }

    @After
    public void afterTest() {
//        if (webDriver != null) {
//            webDriver.close();
//        }
    }


    /**
     * 初始化逻辑页面
     *
     * @param <T>   该页面相对应的逻辑页面
     * @param clazz 该页面相对应的逻辑页面的类
     * @return 页面实例
     */
    public <T extends AbstractPage> T initPage(Class<T> clazz) {
        T page = PageFactory.initElements(webDriver, clazz);
        page.validate();
        return page;
    }

//    /**
//     * 保存登陆过以后的信息
//     **/
//    protected void saveAuthedSession(HttpSession session) {
//        SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
//
//        if (securityContext == null)
//            throw new IllegalStateException("尚未登录");
//
//        request.setSession(session);
//
//        // context 不为空 表示成功登陆
//        SecurityContextHolder.setContext(securityContext);
//    }
}
