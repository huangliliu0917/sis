package cm.huotu.sis.common;

import cm.huotu.sis.pages.AbstractPage;
import com.huotu.sis.boot.MVCConfig;
import com.huotu.sis.boot.RootConfig;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.PageFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by jinzj on 2016/3/25.
 */
@ActiveProfiles(value = {"development","develop"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MVCConfig.class, RootConfig.class})
//@Import({MVCConfig.class, RootConfig.class})
@WebAppConfiguration
public abstract class WebTest extends SpringWebTest{

    /**
     * 初始化逻辑页面
     *
     * @param <T>   该页面相对应的逻辑页面
     * @param clazz 该页面相对应的逻辑页面的类
     * @return 页面实例
     */
    public <T extends AbstractPage> T initPage(Class<T> clazz) {
        T page = PageFactory.initElements(webDriver, clazz);
        return page;
    }
}
