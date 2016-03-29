package cm.huotu.sis.pages;

import libspringtest.SpringWebTest;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Consumer;

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


    /**
     * 操作该页面的测试实例
     */
    private SpringWebTest TestInstance;


    public SpringWebTest getTestInstance() {
        return TestInstance;
    }

    public void setTestInstance(SpringWebTest testInstance) {
        TestInstance = testInstance;
    }

    /**
     * 刷新当前页面,跟浏览器刷新是一致的,并在完成之后调用验证
     */
    public void refresh() {
        webDriver.navigate().refresh();
        PageFactory.initElements(webDriver, this);
        validate();
    }

    /**
     * 重新载入当前逻辑页面信息,并在完成之后调用验证
     */
    public void reloadPageInfo() {
        PageFactory.initElements(webDriver, this);
        validate();
    }

    /**
     * 等待,只到当前页面符合指定条件
     * <p>
     * 完成后会调用 {@link #reloadPageInfo() 刷新}
     * </p>
     *
     * @param waitConsumer 条件给予者
     */
    public void waitOn(Consumer<FluentWait<WebDriver>> waitConsumer) throws InterruptedException {
        Thread.sleep(500L);
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.ignoring(NoSuchElementException.class);
        waitConsumer.accept(wait);
        reloadPageInfo();
    }
}
