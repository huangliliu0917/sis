package cm.huotu.sis.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by jinzj on 2016/3/25.
 */
public class OpenShop extends AbstractPage {

    public OpenShop(WebDriver webDriver) {
        super(webDriver);
    }

    @FindBy(id = "form1")
    private WebElement form;

    @FindBy(tagName = "title")
    private WebElement title;



    @Override
    public void validate() {
        System.out.println(webDriver.getPageSource());
        System.out.println(form.getText());
//        assertThat(title.getText())
//                .isEqualTo("我要开店");
    }

    public void validResult(){
//        WebElement sisLevelName = webDriver.findElement(By.id("sisLevelName"));
//        assertThat(sisLevelName.getText().trim()).isEqualTo(sis.getSisLevel().getLevelName().trim())
//                .as("页面的标题和后台查询的标题进行比对");
//        List<WebElement> htmlIntegrals = webDriver.findElements(By.cssSelector("a.f-xiantwo>i"));
//        assertThat(todayIntegrals+"").isEqualTo(htmlIntegrals.get(0).getText())
//                .as("页面的今日积分收益和后台的今日积分收益进行比对");
//        assertThat(orderCount.toString()).isEqualTo(htmlIntegrals.get(2).getText())
//                .as("订单数量的比对");
//        assertThat(integrals+"").isEqualTo(htmlIntegrals.get(1).getText())
//                .as("总积分收益的比对");
    }

}
