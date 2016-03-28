package cm.huotu.sis.controller;

import cm.huotu.sis.common.WebTest;
import cm.huotu.sis.pages.Template;
import com.huotu.huobanplus.smartui.entity.TemplatePage;
import com.huotu.huobanplus.smartui.entity.support.Scope;
import com.huotu.huobanplus.smartui.repository.TemplatePageRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by jinzj on 2016/3/28.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Transactional(value = "transactionManager")
public class SisWebUserControllerTest extends WebTest {

    @Autowired
    private TemplatePageRepository templatePageRepository;

    /**
     * 模板页面
     * @throws Exception
     */
    @Test
    public void chooseTemplate() throws Exception {
        webDriver.get("http://localhost/sisweb/getTemplateList");
        List<TemplatePage> templatePage = templatePageRepository.findByScopeAndEnabled(Scope.sis, true);
        Template template = initPage(Template.class);
        template.validResult(templatePage);

    }

}
