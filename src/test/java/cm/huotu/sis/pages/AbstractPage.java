package cm.huotu.sis.pages;

import org.openqa.selenium.WebDriver;

/**
 * Created by jinzj on 2016/3/25.
 */
public abstract class AbstractPage {

    protected WebDriver webDriver;

    public AbstractPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    /**
     * 页面开始的一些验证工作
     */
    public abstract void validate();
}
