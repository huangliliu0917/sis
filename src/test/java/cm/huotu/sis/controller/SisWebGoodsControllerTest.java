package cm.huotu.sis.controller;

import cm.huotu.sis.common.WebTest;
import cm.huotu.sis.pages.JuniorDetail;
import cm.huotu.sis.pages.SisCenter;
import cm.huotu.sis.pages.openShop;
import com.huotu.huobanplus.common.dataService.UserTempIntegralHistoryService;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.entity.UserTempIntegralHistory;
import com.huotu.huobanplus.common.repository.GoodsRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.huobanplus.common.utils.DateUtil;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.model.SisDetailModel;
import com.huotu.sis.model.SisSumAmountModel;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.service.CommonConfigsService;
import com.huotu.sis.service.SqlService;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private CommonConfigsService commonConfigService;

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


    /**
     * 开店
     *
     * @throws Exception
     */
    @Test
    public void openShopTest() throws Exception {
        User user = new User();
        user.setWxNickName("slt");
        userRepository.save(user);
        webDriver.get("http://localhost/sisweb/showOpenShop");
        openShop openShop = initPage(cm.huotu.sis.pages.openShop.class);
        openShop.validResult();
    }

    /**
     * 店铺订单页面
     *
     * @throws Exception
     */
    @Test
    public void juniorDetailList() throws Exception {
//        htmlunit暂时不行，继续webdriver
//        URL dynamicUrl = new URL("http://localhost:8080/sisweb/juniorDetailList?srcType=3");
//        HtmlPage dynamicPage = webClient.getPage(dynamicUrl);
//        dynamicPage.getElementById("");
//        dynamicPage.getWebResponse().getWebRequest().setUrl(new URL("http://localhost:8080/sisweb/juniorDetailListAjax"));
//        DomElement juniorDetailList = dynamicPage.getElementById("juniorDetailList");

//        WebRequest requestSettings = new WebRequest(new URL("http://localhost:8080/sisweb/juniorDetailListAjax"), HttpMethod.POST);

//        requestSettings.setRequestParameters(Collections.singletonList(new NameValuePair(InopticsNfcBoxPage.MESSAGE, Utils.marshalXml(inoptics, "UTF-8"))));

//        HtmlPage page = webClient.getPage(requestSettings);


        webDriver.get("http://localhost/sisweb/juniorDetailList?srcType=3");


        System.out.println("1");
//        HtmlElement element = dynamicPage.getBody();
//        element.getAttributeNode("");

    }

    /**
     * 我的团队
     *
     * @throws Exception
     */
    @Test
    public void ownerJuniorList() throws Exception {
        webDriver.get("http://localhost/sisweb/ownerJuniorList");
        Long userId = getCurrentUserId();
        List<SisSumAmountModel> list = sqlService.getListGroupBySrcType(userId);
        if (Objects.nonNull(list)) {
            List<WebElement> elements = webDriver.findElements(By.cssSelector("tbody tr"));
            //列表的判断
            for (int i = 0; i < list.size(); i++) {
                List<WebElement> tds = elements.get(i).findElements(By.cssSelector("td"));
                String srcType = tds.get(0).findElement(By.cssSelector("p")).getText();
                String amount = tds.get(1).findElement(By.cssSelector("p")).getText();
                String num = tds.get(2).findElement(By.cssSelector("a p")).getText();
                assertThat(list.get(i).getSrcType() + "级").isEqualTo(srcType.trim()).as("级数的比较");
                assertThat(Math.round(list.get(i).getAmount()) + "").isEqualTo(amount.trim()).as("开店奖的比较");
                assertThat(list.get(i).getUserNum().toString()).isEqualTo(num.trim()).as("人数比较");
            }
            //随便选择一列进行点击跳转到其他页面
            WebElement threeButton = elements.get(1).findElements(By.cssSelector("td")).get(2).findElement(By.cssSelector("a"));
            assertThat(threeButton.getAttribute("href")).contains("http://localhost/sisweb/juniorDetailList?srcType=2").as("url进行比较");
            threeButton.click();
            Page<SisDetailModel> sisDetailModel = sqlService.getListOpenShop(userId, 2, 1, 10);
            if (Objects.nonNull(sisDetailModel) && Objects.nonNull(sisDetailModel.getContent()) && sisDetailModel.getContent().size() > 0) {
                new WebDriverWait(webDriver, 10).until(
                        (ExpectedCondition<Boolean>) input -> input.findElement(By.id("juniorDetailList"))
                                //注意点，这边不能去找list
                                .findElement(By.cssSelector("ul.cjgl-a")).isEnabled());
                JuniorDetail juniorDetail = initPage(JuniorDetail.class);
                juniorDetail.validResult(sisDetailModel);
            }
        }
    }

    @Test
    public void goodsDetail() throws Exception {
        webDriver.get("http://localhost/sisweb/ownerJuniorList");
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        StringBuilder pictureUrl = new StringBuilder();
        Goods goods = new Goods();
        goods.setTitle(UUID.randomUUID().toString());
        goods.setScenes(0);
        goods.setOwner(user.getMerchant());
        goods.setSmallPic(UUID.randomUUID().toString());
        goods.setIntro(UUID.randomUUID().toString());
        goods.setStock(100);
        pictureUrl.append(commonConfigService.getResoureServerUrl() + goods.getSmallPic());
        goodsRepository.saveAndFlush(goods);
        webDriver.get("http://localhost/sisweb/getSisGoodsDetail?goodId=" + goods.getId() + "&customerId=" + user.getMerchant().getId());
        //商品图片
        WebElement picture = webDriver.findElement(By.cssSelector("div.s_bd")).findElement(By.cssSelector("ul>li>a>img"));
        assertThat(picture.getAttribute("src").trim()).isEqualTo(pictureUrl.toString()).as("商品图片的比较");
        //商品内容
        WebElement title = webDriver.findElement(By.cssSelector("div.sp_bt"));
        assertThat(title.getText().trim()).isEqualTo(goods.getTitle()).as("商品内容的比较");
        //各个值
        List<WebElement> elements = webDriver.findElements(By.cssSelector("span.sp_hongzz"));
        assertThat(elements.get(0).getText().trim()).isEqualTo(goods.getStock() + "件").as("库存的比较");
        //感觉商品详情这边有点问题
//        assertThat(elements.get(1).getText().trim()).isEqualTo(goods.getIntro()).as("商品详情");


    }


}
