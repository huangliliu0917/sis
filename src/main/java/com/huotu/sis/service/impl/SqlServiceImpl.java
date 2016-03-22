package com.huotu.sis.service.impl;

import com.huotu.sis.model.SisDetailModel;
import com.huotu.sis.model.SisSumAmountModel;
import com.huotu.sis.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jinzj on 2016/2/22.
 */
@Service
public class SqlServiceImpl implements SqlService{

    @Autowired
    private EntityManager entityManager;

    private static String IMG_URL = "images/moren.png";

    public List<SisSumAmountModel> getListGroupBySrcType(Long userId) throws IOException {
        StringBuffer hql = new StringBuffer();
        hql.append("SELECT sis.srcType ,SUM(sis.amount) AS totalAmount FROM SisOpenAwardLog as sis" +
                " WHERE sis.shopId = :userId GROUP BY sis.srcType order by sis.srcType");
        Query query = entityManager.createQuery(hql.toString());
        query.setParameter("userId", userId);
        List list = query.getResultList();
        List<SisSumAmountModel> models = new ArrayList<>();

        list.forEach(r -> {
            Object[] obj = (Object[]) r;
            SisSumAmountModel model = new SisSumAmountModel();
            Integer srcType=Integer.parseInt(obj[0].toString());
            model.setSrcType(srcType);
            model.setAmount(Double.parseDouble(obj[1].toString()));
            model.setUserNum(0);
            models.add(model);
        });
        //补全某个等级没有返利的情况
        for(int i=1;i<=3;i++){
            boolean flag=true;
            for(SisSumAmountModel s:models){
                if(s.getSrcType()==i){
                    flag=false;
                    break;
                }
            }
            if(flag){
                SisSumAmountModel sisSumAmountModel=new SisSumAmountModel();
                sisSumAmountModel.setSrcType(i);
                sisSumAmountModel.setAmount(0.0);
                sisSumAmountModel.setUserNum(0);
                models.add(sisSumAmountModel);
            }
        }
        //排序
        Collections.sort(models, new Comparator<SisSumAmountModel>() {
            @Override
            public int compare(SisSumAmountModel o1, SisSumAmountModel o2) {
                return o1.getSrcType()-o2.getSrcType();
            }
        });

        //人数统计
        List<SisSumAmountModel> modelNumbers=getListGroupByBelongType(userId);
        if(!modelNumbers.isEmpty()){
            for(int i=0;i<models.size();i++){
                for(int j=0;j<modelNumbers.size();j++){
                    if(models.get(i).getSrcType().equals(modelNumbers.get(j).getSrcType())){
                        models.get(i).setUserNum(modelNumbers.get(j).getUserNum());
                    }
                }
            }
        }else{
            for(int i=0;i<models.size();i++){
                models.get(i).setUserNum(0);
            }
        }

        List<SisSumAmountModel> nowList = models.stream().filter(
                r -> r.getSrcType() > 0&&r.getSrcType()<4).collect(Collectors.toList());

        return nowList;
    }

    public List getListGroupByBelongType(Long userId) throws IOException {
        Integer[] numbers=new Integer[4];
        numbers[0]=1;
        Query query = entityManager.createNativeQuery("SELECT  BelongType ,\n" +
                "        COUNT(*) AS TotalNums\n" +
                "FROM    SIS AS A\n" +
                "        INNER JOIN ( SELECT UB_UserID ,\n" +
                "                            1 AS BelongType\n" +
                "                     FROM   Hot_UserBaseInfo\n" +
                "                     WHERE  UB_BelongOne = ?\n" +
                "                     UNION ALL\n" +
                "                     SELECT UB_UserID ,\n" +
                "                            2 AS BelongType\n" +
                "                     FROM   Hot_UserBaseInfo\n" +
                "                     WHERE  UB_BelongTwo = ?\n" +
                "                     UNION ALL\n" +
                "                     SELECT UB_UserID ,\n" +
                "                            3 AS BelongType\n" +
                "                     FROM   Hot_UserBaseInfo\n" +
                "                     WHERE  UB_BelongThree = ?\n" +
                "                   ) AS B ON A.USER_UB_UserID = B.UB_UserID\n" +
                "GROUP BY BelongType").setParameter(1, userId).setParameter(2,userId).setParameter(3, userId);
//        query.setParameter("userId", userId);
        List list = query.getResultList();
        List<SisSumAmountModel> models = new ArrayList<>();
        list.forEach(r -> {
            Object[] obj = (Object[]) r;
            SisSumAmountModel model = new SisSumAmountModel();
            model.setSrcType(Integer.parseInt(obj[0].toString()));
            model.setUserNum(Integer.parseInt(obj[1].toString()));
            models.add(model);
        });
//        numbers[1]=(Integer)list.get(0);
//        hql.delete(0,hql.length());
//        hql.append("select count(u) from Sis as s left join User as s on s.user=u where u.belongTwo=:userId");
//        query=entityManager.createQuery(hql.toString());
//        query.setParameter("userId", userId);
//        list = query.getResultList();
//        numbers[2]=(Integer)list.get(0);
//        hql.delete(0,hql.length());
//        hql.append("select count(u) from Sis as s left join User as s on s.user=u where u.belongThree=:userId");
//        query=entityManager.createQuery(hql.toString());
//        query.setParameter("userId", userId);
//        list = query.getResultList();
//        numbers[3]=(Integer)list.get(0);
        return models;
        //        hql.append("select B.BelongType,COUNT(A) as TotalNums from Sis AS A " +
//                "        INNER JOIN ( SELECT u1.userId , " +
//                "                            1 AS BelongType" +
//                "                     FROM   User as u1" +
//                "                     WHERE  u1.belongOne = :userId" +
//                "                     UNION ALL" +
//                "                     SELECT u2.userId ," +
//                "                            2 AS BelongType" +
//                "                     FROM   User as u2" +
//                "                     WHERE  u2.belongTwo = :userId" +
//                "                     UNION ALL" +
//                "                     SELECT u3.userId ," +
//                "                            3 AS BelongType" +
//                "                     FROM   User as u3" +
//                "                     WHERE  u3.belongThree = :userId" +
//                "                   ) AS B ON A.user.id = B.userId" +
//                " GROUP BY B.BelongType");
    }

    @Override
    public Page<SisDetailModel> getListOpenShop(Long userId, Integer srcType,Integer pageNo, Integer pageSize) throws IOException{
        String userBelong;
        switch (srcType){
            case 1:
                userBelong="UB_BelongOne";
                break;
            case 2:
                userBelong="UB_BelongTwo";
                break;
            case 3:
                userBelong="UB_BelongThree";
                break;
            default:
                userBelong="UB_BelongOne";
        }
        String sql="SELECT * FROM (\n" +
                "                SELECT ROW_NUMBER() OVER(ORDER BY TT.ID DESC)AS Row, * FROM (\n" +
                "                  SELECT  A.ID,A.Title,B.*,\n" +
                "                    ISNULL(C.TotalAmount, 0) AS ContributeAmount,A.OPENTIME,A.RealName,A.Mobile\n" +
                "                  FROM    SIS AS A\n" +
                "                    INNER JOIN\n" +
                "                    ( SELECT UB_UserID , UB_WxNickName , UB_WxHeadImg\n" +
                "                      FROM   Hot_UserBaseInfo\n" +
                "                      WHERE  "+userBelong+" = "+userId+"\n" +
                "                    ) AS B ON A.USER_UB_UserID = B.UB_UserID\n" +
                "\n" +
                "                    LEFT JOIN ( SELECT  ContribShopId ,SUM(Amount) AS TotalAmount\n" +
                "                                FROM  SIS_OpenAwardLog\n" +
                "                                WHERE ShopId = "+userId+" AND SrcType = "+srcType+"\n" +
                "                                GROUP BY ContribShopId\n" +
                "                              ) AS C ON C.ContribShopId = A.USER_UB_UserID\n" +
                "                ) AS TT )\n" +
                "  AS MM WHERE MM.Row BETWEEN ("+pageNo+"-1)*"+pageSize+"+1 AND "+pageNo+"*"+pageSize+"";
        Query query=entityManager.createNativeQuery(sql);
        List list = query.getResultList();
        List<SisDetailModel> sisDetailModels=new ArrayList<>();
        list.forEach(r -> {
            Object[] obj = (Object[]) r;
            SisDetailModel model = new SisDetailModel();
            //sis ID
            model.setSisId(Long.parseLong(obj[1].toString()));
            //店铺名字
            if(null!=obj[2])
                model.setSisName(obj[2].toString());
            if(null!=obj[4])
                model.setUserName(obj[4].toString());
            if(null!=obj[5])
                model.setWeixinImageUrl(obj[5].toString());
            else
                model.setWeixinImageUrl(IMG_URL);
            if(obj[6]!=null){
                model.setOpenPrice(Double.parseDouble(obj[6].toString()));
            }else {
                model.setOpenPrice(0);
            }
            if(obj[7]!=null){
                model.setStartDate(obj[7].toString());
            }
            if(obj[8]!=null){
                model.setRealName(obj[8].toString());
            }
            if(obj[9]!=null){
                model.setMobile(Long.parseLong(String.valueOf(obj[9])));
            }
            sisDetailModels.add(model);
        });


        String total=" SELECT  count(*) FROM    SIS AS A INNER JOIN\n" +
                "                                                                                ( SELECT UB_UserID ,UB_UserLoginName , UB_WxNickName , UB_WxHeadImg\n" +
                "                                                                                  FROM   Hot_UserBaseInfo\n" +
                "                                                                                  WHERE  "+userBelong+" = "+userId+"\n" +
                "                                                                                ) AS B ON A.USER_UB_UserID = B.UB_UserID\n" +
                "\n" +
                "                                                                                LEFT JOIN ( SELECT  ContribShopId ,SUM(Amount) AS TotalAmount\n" +
                "                                                                                            FROM  SIS_OpenAwardLog\n" +
                "                                                                                            WHERE ShopId = "+userId+" AND SrcType = "+srcType+"\n" +
                "                                                                                            GROUP BY ContribShopId\n" +
                "                                                                                          ) AS C ON C.ContribShopId = A.USER_UB_UserID";
        query=entityManager.createNativeQuery(total);
        list = query.getResultList();

        return new PageImpl<>(sisDetailModels,new PageRequest(pageNo-1,pageSize),Long.parseLong(list.get(0).toString()));
    }
}
