package cm.huotu.sis.common;

import cm.huotu.sis.filter.ParamFilter;
import com.huotu.sis.boot.MVCConfig;
import com.huotu.sis.boot.RootConfig;
import com.huotu.sis.common.PublicParameterHolder;
import com.huotu.sis.model.PublicParameterModel;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by jinzj on 2016/3/25.
 */
@ActiveProfiles(value = {"development","develop"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MVCConfig.class, RootConfig.class, ParamFilter.class})
//@Import({MVCConfig.class, RootConfig.class})
@WebAppConfiguration
public abstract class WebTest extends SpringWebTest{

    /**
     * 获取当前登录的user
     *
     * @return
     */
    public Long getCurrentUserId() {
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();
        return userId;
    }

}
