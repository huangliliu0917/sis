package cm.huotu.sis.controller;

import cm.huotu.sis.common.WebTest;
import cm.huotu.sis.pages.OpenShop;
import cm.huotu.sis.pages.SisCenter;
import com.huotu.huobanplus.common.dataService.UserTempIntegralHistoryService;
import com.huotu.huobanplus.common.entity.Brand;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.entity.UserTempIntegralHistory;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.huobanplus.common.utils.DateUtil;
import com.huotu.sis.common.PublicParameterHolder;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.model.PublicParameterModel;
import com.huotu.sis.model.SisDetailModel;
import com.huotu.sis.model.SisSumAmountModel;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.service.SqlService;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jinzj on 2016/3/25.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Transactional(value = "transactionManager")
public class SisWebGoodsControllerTest extends WebTest {

    @Autowired
    private Environment env;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SisRepository sisRepository;

    @Autowired
    private UserTempIntegralHistoryService userTempIntegralHistoryService;

    @Autowired
    private SqlService sqlService;

    /**
     * 店铺中心页面的测试
     *
     * @throws Exception
     */
    @Test
    public void sisCenterTest() throws Exception {
        webDriver.get("http://localhost/sisweb/getSisCenter");
        SisCenter sisCenter = initPage(SisCenter.class);
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        //店铺详细信息
        Sis sis = sisRepository.findByUser(user);
        if (Objects.isNull(user) || Objects.isNull(sis))
            return;
        Date now = new Date();
        Date startDate = DateUtil.makeStartDate(now);
        Date endDate = DateUtil.makeEndDate(now);
        //订单数量
        Long orderCount = userTempIntegralHistoryService.getCountByUserId(userId, 500);
        //今日返利
        List<UserTempIntegralHistory> historyList = userTempIntegralHistoryService.getListByUserIdAndDate(userId, 1, 500, startDate, endDate);
        int todayIntegrals = 0;
        for (UserTempIntegralHistory history : historyList) {
            todayIntegrals += history.getIntegral();
        }

        //总收益
        List<UserTempIntegralHistory> list = userTempIntegralHistoryService.getListByUserIdAndDate(userId, 1, 500, null, null);
        int integrals = 0;
        for (UserTempIntegralHistory history : list) {
            integrals += history.getIntegral();
        }
        sisCenter.validResult(sis, todayIntegrals, integrals, orderCount);
    }




    @Test
    public void openShopTest() throws Exception {
        User user=new User();
        user.setWxNickName("slt");
        userRepository.save(user);
        webDriver.get("http://localhost/sisweb/showOpenShop");
        OpenShop openShop = initPage(OpenShop.class);
        openShop.validResult();
    }

    /**
     * 店铺订单页面
     *
     * @throws Exception
     */
    @Test
    public void juniorDetailList() throws Exception {
        webDriver.get("http://localhost/sisweb/juniorDetailList");
//        Page<SisDetailModel> sisDetailModel = sqlService.getListOpenShop(userId, srcType, page, pageSize);
//        new WebDriverWait(webDriver,30)
//                .until(ExpectedConditions.textToBePresentInElement(button,"已选择该模板"));
    }

    /**
     * 品牌详情页
     *
     * @throws Exception
     */
    @Test
    public void brandDetail() throws Exception {

    }

    /**
     * 我的团队
     * @throws Exception
     */
    @Test
    public void ownerJuniorList() throws Exception {
        webDriver.get("http://localhost/sisweb/ownerJuniorList");
        List<SisSumAmountModel> list = sqlService.getListGroupBySrcType(getCurrentUserId());
        if (Objects.nonNull(list)) {
            List<WebElement> elements = webDriver.findElements(By.cssSelector("tbody tr"));
            WebDriverWait driverWait = new WebDriverWait(webDriver,10);
            //列表的判断
            for (int i = 0; i < list.size(); i++) {
                List<WebElement> tds = elements.get(i).findElements(By.cssSelector("td"));
                String srcType = tds.get(0).findElement(By.cssSelector("p")).getText();
                String amount = tds.get(1).findElement(By.cssSelector("p")).getText();
                String num = tds.get(2).findElement(By.cssSelector("a p")).getText();
                assertThat(list.get(i).getSrcType()+"级").isEqualTo(srcType.trim()).as("级数的比较");
                assertThat(Math.round(list.get(i).getAmount())+"").isEqualTo(amount.trim()).as("开店奖的比较");
                assertThat(list.get(i).getUserNum().toString()).isEqualTo(num.trim()).as("人数比较");
            }
            //随便选择一列进行点击跳转到其他页面
            WebElement threeButton = elements.get(2).findElements(By.cssSelector("td")).get(2).findElement(By.cssSelector("a"));
            threeButton.click();
//            driverWait.until(ExpectedConditions.urlContains("http://localhost/sisweb/juniorDetailList"));

        }
    }

    /**
     * 获取当前登录的user
     *
     * @return
     */
    private Long getCurrentUserId() {
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();
        return userId;
    }




}
