package cm.huotu.sis.controller;

import cm.huotu.sis.common.WebTest;
import com.huotu.huobanplus.common.entity.Brand;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.BrandRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.sis.common.PublicParameterHolder;
import com.huotu.sis.entity.SisBrand;
import com.huotu.sis.model.PublicParameterModel;
import com.huotu.sis.repository.SisBrandRepository;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jinzj on 2016/3/29.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Transactional(value = "transactionManager")
public class SisWebBrandControllerTest extends WebTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void brandDetail() throws Exception {
        webDriver.get("http://localhost/sisweb/getBrandListPage");

        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        Brand brand = new Brand();
        brand.setBrandName(UUID.randomUUID().toString());
        brand.setBrandLogo(UUID.randomUUID().toString());
        brandRepository.saveAndFlush(brand);
        webDriver.get("http://localhost/sisweb/getBrandDetail?brandId="+brand.getId()+"&customerId="+user.getMerchant().getId());
        WebElement img = webDriver.findElement(By.cssSelector("div.s_bd")).findElement(By.cssSelector("ul>li>a>img"));
        assertThat(img.getAttribute("src").trim()).isEqualTo(brand.getBrandLogo()).as("品牌详情logo比较");
        WebElement brandName = webDriver.findElement(By.cssSelector("span.sp_bt"));
        assertThat(brandName.getText().trim()).isEqualTo(brand.getBrandName()).as("品牌名字的比较");
    }
}
