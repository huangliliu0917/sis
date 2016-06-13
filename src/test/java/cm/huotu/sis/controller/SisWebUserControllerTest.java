package cm.huotu.sis.controller;

import cm.huotu.sis.common.WebTest;
import cm.huotu.sis.pages.Template;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.entity.support.ProductSpecifications;
import com.huotu.huobanplus.common.repository.GoodsRepository;
import com.huotu.huobanplus.common.repository.MerchantRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.huobanplus.smartui.entity.TemplatePage;
import com.huotu.huobanplus.smartui.entity.support.Scope;
import com.huotu.huobanplus.smartui.repository.TemplatePageRepository;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.service.CommonConfigsService;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jinzj on 2016/3/28.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Transactional(value = "transactionManager")
@Ignore
public class SisWebUserControllerTest extends WebTest {

    @Autowired
    private TemplatePageRepository templatePageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SisConfigRepository sisConfigRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private CommonConfigsService commonConfigService;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private SisLevelRepository sisLevelRepository;

    /**
     * 模板页面
     * @throws Exception
     */
    @Test
    public void chooseTemplate() throws Exception {
        webDriver.get("http://localhost/sisweb/getTemplateList");
        List<TemplatePage> templatePage = templatePageRepository.findByScopeAndEnabled(Scope.sis, true);
        Template template = initPage(Template.class);
        template.validResult(templatePage);

    }

    /**
     * 开店商品详情
     * @throws Exception
     */
    @Test
    public void showOpenShopGoodsDetail() throws Exception {
        webDriver.get("http://localhost/sisweb/getTemplateList");
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if(Objects.isNull(user)||Objects.isNull(user.getMerchant())||Objects.isNull(user.getMerchant().getId()))
            return;

        SisConfig sisConfig = sisConfigRepository.findByMerchantId(user.getMerchant().getId());
        if (Objects.isNull(sisConfig) || sisConfig.getEnabled() == 0) {
            return;
        }
        if (Objects.isNull(sisConfig.getOpenGoodsId())) {
            return;
        }
        Goods goods = goodsRepository.findOne(sisConfig.getOpenGoodsId());
        if (Objects.isNull(goods)) {
            return;
        }
        ProductSpecifications productSpecifications = goods.getSpecificationsCache();
        if (Objects.isNull(productSpecifications) || productSpecifications.isEmpty()) {
            return;
        }
        Long productId = null;
        for (Long key : productSpecifications.keySet()) {
            productId = key;
            break;
        }
        if (Objects.isNull(productId)) {
            return;
        }
        Long customerId = user.getMerchant().getId();
        webDriver.get("http://localhost/sisweb/showOpenShopGoodsDetail?customerId="+user.getMerchant().getId()+"&goodsId="+goods.getId());
        WebElement smallPic = webDriver.findElement(By.cssSelector("div.s_bd")).findElement(By.cssSelector("ul>li>a>img"));
        assertThat(smallPic.getAttribute("src")).isEqualTo(goods.getSmallPic().getValue()).as("图片资源路径");
        WebElement goodsTitle = webDriver.findElement(By.cssSelector("div.sp_bt")).findElement(By.cssSelector("a"));
        assertThat(goodsTitle.getText().trim()).isEqualTo(goods.getTitle()).as("商品标题");
        WebElement stock = webDriver.findElement(By.cssSelector("span.sp_hongzz")).findElement(By.cssSelector("a"));
        assertThat(stock.getText().trim()).isEqualTo(goods.getStock()+"件").as("库存比较");
        WebElement description = webDriver.findElement(By.id("goodsDesc"));
        String resUrl=commonConfigService.getResourceServerUrl();
        assertThat(description.getText().trim())
                .isEqualTo(goods.getIntro().replaceAll("src=\"/resource/", "  width='100%' src=\"" + resUrl + "/resource/"));
        WebElement goodsUrl = webDriver.findElement(By.id("goodsUrl"));
        String url = getMerchantSubDomain(customerId) + "/mall/SubmitOrder.aspx?" +
                "fastbuy=1&" +
                "traitems=" + goods.getId() + "_" + productId + "_1&" +
                "customerid=" + customerId + "&" +
                "showwxpaytitle=1"+"&"+"returl=/UserCenter/ShopInShop/OpenSuccess.aspx%3Fcustomerid%3D"+customerId;
        assertThat(goodsUrl.getAttribute("href")).isEqualTo(url).as("跳转url的比较");
    }

    /**
     * 获取商户的网址
     * @param merchantId    商户ID
     * @return
     */
    private String getMerchantSubDomain(Long merchantId){
        String subDomain=merchantRepository.findSubDomainByMerchantId(merchantId);
        if(subDomain==null){
            subDomain="";
        }
        return  "http://"+subDomain+"."+commonConfigService.getMallDomain();
    }

    @Test
    public void findfirstOrderByLevelNoDescTest(){
        SisLevel sisLevel;
        for(int i=0;i<5;i++){
            sisLevel=new SisLevel();
            sisLevel.setLevelNo(i);
            sisLevel.setLevelName(i+"级");
            sisLevelRepository.save(sisLevel);
        }
//        SisLevel sisLevel1=sisLevelRepository.findTopByLevelNoOrderByLevelNoDesc();
//        Assert.assertEquals("yes",5,sisLevel1.getLevelNo().intValue());
    }

}
