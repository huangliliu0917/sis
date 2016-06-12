/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package func;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huotu.sis.entity.support.RelationAndPercent;
import com.huotu.sis.entity.support.SisRebateTeamManagerSetting;
import com.huotu.sis.entity.support.SisRebateTeamManagerSettingConverter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author slt
 */
public class StringCorrectTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    private SisRebateTeamManagerSettingConverter rebateTeamManagerSettingConverter=new SisRebateTeamManagerSettingConverter();
    @Test
    public void rebateTeamManagerSetting(){
        List<RelationAndPercent> rebateTeams=new ArrayList<>();
        for(int i=0;i<5;i++){
            RelationAndPercent rebateTeam=new RelationAndPercent();
            rebateTeam.setRelation("0_"+i);
            rebateTeam.setPercent(i);
            rebateTeams.add(rebateTeam);
        }
        SisRebateTeamManagerSetting rebateTeamManagerSetting=new SisRebateTeamManagerSetting();
        rebateTeamManagerSetting.setSaleAward(880.0);
        rebateTeamManagerSetting.setManageAwards(rebateTeams);
        String output=rebateTeamManagerSettingConverter.convertToDatabaseColumn(rebateTeamManagerSetting);
        System.out.println(output);
        SisRebateTeamManagerSetting read=rebateTeamManagerSettingConverter.convertToEntityAttribute(output);
        assertEquals("yes",rebateTeamManagerSetting,read);
        String origin="{\"SaleAward\":80.0,\"ManageAwards\":[{\"Relation\":\"0_0\",\"Percent\":10.0},{\"Relation\":\"0_1\",\"Percent\":6.0},{\"Relation\":\"1_1\",\"Percent\":0.0},{\"Relation\":\"1_2\",\"Percent\":4.0},{\"Relation\":\"2_2\",\"Percent\":0.0}]}";
        rebateTeamManagerSetting=rebateTeamManagerSettingConverter.convertToEntityAttribute(origin);
        output = rebateTeamManagerSettingConverter.convertToDatabaseColumn(rebateTeamManagerSetting);
        SisRebateTeamManagerSetting another = rebateTeamManagerSettingConverter.convertToEntityAttribute(output);
        assertEquals("",rebateTeamManagerSetting,another);

    }


}
