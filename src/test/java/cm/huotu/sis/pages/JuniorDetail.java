package cm.huotu.sis.pages;

import com.huotu.sis.model.SisDetailModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jinzj on 2016/3/29.
 */
public class JuniorDetail extends AbstractPage {


    public JuniorDetail(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validate() {

    }

    public void validResult(Page<SisDetailModel> sisDetailModels) {
        if (Objects.nonNull(sisDetailModels) && Objects.nonNull(sisDetailModels.getContent())
                && sisDetailModels.getContent().size() > 0) {
//            new WebDriverWait(webDriver,10)
//                    .until((ExpectedCondition<Boolean>) input -> input.findElement(By.id("juniorDetailList")).isDisplayed());
//            WebDriverWait webDriverWait = new WebDriverWait(webDriver, 10);
            SisDetailModel model = sisDetailModels.getContent().get(0);
//            WebElement webElement;
//            if(sisDetailModels.getContent().size()==1){
//                webElement = webDriverWait.until((ExpectedCondition<WebElement>) input ->
//                                input.findElement(By.cssSelector("ul")));
////                assertThat();
//            }else{
//                webElement = webDriverWait.until((ExpectedCondition<WebElement>) input ->
//                                input.findElements(By.cssSelector("ul[class~=cjgl]")).get(0));
//            }
//            List<WebElement> lis = webElement.findElements(By.cssSelector("li"));
//            //图片路径
//            WebElement imgSrc = lis.get(0).findElement(By.cssSelector("a img"));
//            assertThat(imgSrc.getAttribute("src").trim()).isEqualTo(model.getWeixinImageUrl().trim()).as("图片路径的比较");
        }
    }
}
