package cm.huotu.sis.pages;

import com.huotu.sis.entity.Sis;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jinzj on 2016/3/25.
 */
@Transactional
public class SisCenter extends AbstractPage {

    public SisCenter(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validate() {

    }

    public void validResult(Sis sis,int todayIntegrals,int integrals,Long orderCount){
        WebElement sisLevelName = webDriver.findElement(By.id("sisLevelName"));
        assertThat(sisLevelName.getText().trim()).isEqualTo(sis.getSisLevel().getLevelName().trim())
                .as("页面的标题和后台查询的标题进行比对");
        List<WebElement> htmlIntegrals = webDriver.findElements(By.cssSelector("a.f-xiantwo>i"));
        assertThat(todayIntegrals+"").isEqualTo(htmlIntegrals.get(0).getText())
                .as("页面的今日积分收益和后台的今日积分收益进行比对");
        assertThat(orderCount.toString()).isEqualTo(htmlIntegrals.get(2).getText())
                .as("订单数量的比对");
        assertThat(integrals+"").isEqualTo(htmlIntegrals.get(1).getText())
                .as("总积分收益的比对");
    }

}
