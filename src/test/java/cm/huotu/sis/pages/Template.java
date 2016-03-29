package cm.huotu.sis.pages;

import com.huotu.huobanplus.smartui.entity.TemplatePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jinzj on 2016/3/28.
 */
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
        WebElement button = pList.get(2).findElement(By.cssSelector("button"));
        button.click();
        //点击了按钮之后，这个button的text预期变成了“已选择该模板”
        Boolean isChange = new WebDriverWait(webDriver,30)
                .until(ExpectedConditions.textToBePresentInElement(button,"已选择该模板"));
        assertThat(isChange);
    }
}
