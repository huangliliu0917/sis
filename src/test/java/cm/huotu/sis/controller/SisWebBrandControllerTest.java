package cm.huotu.sis.controller;

import cm.huotu.sis.common.WebTest;
import com.huotu.huobanplus.common.entity.Brand;
import com.huotu.huobanplus.common.repository.BrandRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jinzj on 2016/3/29.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Transactional(value = "transactionManager")
public class SisWebBrandControllerTest extends WebTest {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    public void brandDetail() throws Exception {
        webDriver.get("http://localhost/sisweb/getBrandDetail");
//        Brand brand = brandRepository.findOne(brandId);
    }
}
