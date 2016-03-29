package cm.huotu.sis.common;

import com.huotu.sis.boot.MVCConfig;
import com.huotu.sis.boot.RootConfig;
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
@ContextConfiguration(classes = {MVCConfig.class, RootConfig.class})
//@Import({MVCConfig.class, RootConfig.class})
@WebAppConfiguration
public abstract class WebTest extends SpringWebTest{

}
