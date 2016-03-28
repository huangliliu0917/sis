package cm.huotu.sis.pages;

import com.huotu.huobanplus.smartui.entity.TemplatePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jinzj on 2016/3/28.
 */
@Transactional
public class Template extends AbstractPage {
    public Template(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validate() {

    }

    public void validResult(List<TemplatePage> templatePage){
        List<WebElement> list = webDriver.findElements(By.cssSelector("div.commfont>div"));
        List<WebElement> pList = list.get(0).findElements(By.cssSelector("p"));
        assertThat(pList.get(0).getText().trim()).isEqualTo(templatePage.get(0).getTitle())
                .as("模板标题的比对");
    }
}
