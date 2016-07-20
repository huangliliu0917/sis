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
import com.huotu.sis.entity.support.*;
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


    private SisLevelOpenAwardsConverter converter=new SisLevelOpenAwardsConverter();
    @Test
    public void sisLevelOpenAwardsConverter(){
        String origin="[{\"buysislvid\":247,\"cfg\":[{\"idx\":0,\"unified\":1.0,\"custom\":[]},{\"idx\":1,\"unified\":2.0,\"custom\":[]},{\"idx\":2,\"unified\":-1.0,\"custom\":[{\"lvid\":206,\"val\":3.0},{\"lvid\":218,\"val\":4.0},{\"lvid\":328,\"val\":5.0},{\"lvid\":374,\"val\":6.0},{\"lvid\":380,\"val\":7.0},{\"lvid\":1444,\"val\":8.0},{\"lvid\":1446,\"val\":9.0},{\"lvid\":1466,\"val\":10.0},{\"lvid\":1468,\"val\":11.0}]},{\"idx\":3,\"unified\":12.0,\"custom\":[]},{\"idx\":4,\"unified\":13.0,\"custom\":[]},{\"idx\":5,\"unified\":14.0,\"custom\":[]},{\"idx\":6,\"unified\":15.0,\"custom\":[]},{\"idx\":7,\"unified\":16.0,\"custom\":[]},{\"idx\":8,\"unified\":17.0,\"custom\":[]}]},{\"buysislvid\":205,\"cfg\":[{\"idx\":0,\"unified\":0.0,\"custom\":[]},{\"idx\":1,\"unified\":0.0,\"custom\":[]},{\"idx\":2,\"unified\":0.0,\"custom\":[]},{\"idx\":3,\"unified\":0.0,\"custom\":[]}]},{\"buysislvid\":336,\"cfg\":[{\"idx\":0,\"unified\":3.0,\"custom\":[]},{\"idx\":1,\"unified\":2.0,\"custom\":[]},{\"idx\":2,\"unified\":1.0,\"custom\":[]},{\"idx\":3,\"unified\":0.0,\"custom\":[]}]},{\"buysislvid\":383,\"cfg\":[{\"idx\":0,\"unified\":0.0,\"custom\":[]},{\"idx\":1,\"unified\":0.0,\"custom\":[]},{\"idx\":2,\"unified\":0.0,\"custom\":[]},{\"idx\":3,\"unified\":0.0,\"custom\":[]}]},{\"buysislvid\":328,\"cfg\":[{\"idx\":0,\"unified\":3.0,\"custom\":[]},{\"idx\":1,\"unified\":4.0,\"custom\":[]},{\"idx\":2,\"unified\":5.0,\"custom\":[]},{\"idx\":3,\"unified\":6.0,\"custom\":[]},{\"idx\":4,\"unified\":7.0,\"custom\":[]},{\"idx\":5,\"unified\":8.0,\"custom\":[]},{\"idx\":6,\"unified\":9.0,\"custom\":[]},{\"idx\":7,\"unified\":1.0,\"custom\":[]},{\"idx\":8,\"unified\":2.0,\"custom\":[]}]}]";
        SisLevelAwards sisLevelOpenAwards =converter.convertToEntityAttribute(origin);
        String output = converter.convertToDatabaseColumn(sisLevelOpenAwards);
        SisLevelAwards another = converter.convertToEntityAttribute(output);
        assertEquals("",sisLevelOpenAwards,another);
    }

    private SisLevelConditionsConverter conditionsConverter=new SisLevelConditionsConverter();
    @Test
    public void sisLevelConditionsConverter(){
        String origin="[\n" +
                "    {\n" +
                "        \"sislvid\": 20,\n" +
                "        \"num\": 40,\n" +
                "        \"relation\": 0\n" +
                "    },\n" +
                "    {\n" +
                "        \"sislvid\": 20,\n" +
                "        \"num\": 40,\n" +
                "        \"relation\": 0\n" +
                "    }\n" +
                "]";
        List<SisLevelCondition> sisLevelConditions =conditionsConverter.convertToEntityAttribute(origin);
        String output = conditionsConverter.convertToDatabaseColumn(sisLevelConditions);
        List<SisLevelCondition> another = conditionsConverter.convertToEntityAttribute(output);
        assertEquals("",sisLevelConditions,another);
    }


}
