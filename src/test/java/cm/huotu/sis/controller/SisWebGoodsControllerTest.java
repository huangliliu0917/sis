package cm.huotu.sis.controller;

import cm.huotu.sis.common.WebTest;
import cm.huotu.sis.pages.SisCenter;
import com.huotu.huobanplus.common.dataService.UserTempIntegralHistoryService;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.entity.UserTempIntegralHistory;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.huobanplus.common.utils.DateUtil;
import com.huotu.sis.common.PublicParameterHolder;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.model.PublicParameterModel;
import com.huotu.sis.repository.SisRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
        if(Objects.isNull(user)||Objects.isNull(sis))
            return;
        Date now = new Date();
        Date startDate = DateUtil.makeStartDate(now);
        Date endDate = DateUtil.makeEndDate(now);
        //订单数量
        Long orderCount = userTempIntegralHistoryService.getCountByUserId(userId,500);
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
        sisCenter.validResult(sis,todayIntegrals,integrals,orderCount);
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
