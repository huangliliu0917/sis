package cm.huotu.sis.pages;

import com.huotu.sis.model.sisweb.SisDetailModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

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
            SisDetailModel model = sisDetailModels.getContent().get(0);
            List<WebElement> lis = webDriver.findElements(By.cssSelector("ul.cjgl-a")).get(0).findElements(By.cssSelector("li"));
            WebElement imgSrc = lis.get(0).findElement(By.cssSelector("a>img"));
            assertThat(imgSrc.getAttribute("src").trim()).isEqualTo(model.getWeixinImageUrl()).as("微信头像的比较");
            List<WebElement> ps = lis.get(1).findElements(By.cssSelector("p"));
            WebElement sisName = ps.get(0).findElement(By.cssSelector("b"));
            assertThat(sisName.getText().trim()).isEqualTo(model.getSisName()).as("店铺名称");
        }
    }
}
