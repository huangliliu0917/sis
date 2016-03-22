package com.huotu.sis.common;

import com.huotu.huobanplus.base.service.BaseService;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.MerchantConfig;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.entity.support.RebateConfiguration;
import com.huotu.huobanplus.common.model.RebateMode;
import com.huotu.huobanplus.common.model.RebatePair;
import com.huotu.huobanplus.common.repository.MallBaseRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by lgh on 2015/11/12.
 */
public class MathHelper {

    @Autowired
    BaseService baseService;

    @Autowired
    MallBaseRepository mallBaseRepository;

    private Log log = LogFactory.getLog(MathHelper.class);

    /**
     * 商品返利统计
     * @param goods     返利商品
     * @param user      会员
     * @return          该商品对应会员可以返利的积分
     */
    public static double countRebate(Goods goods,User user){
        try{

            if(user==null||goods==null){
                return 0;
            }
            if(user.getMerchant()==null){
                return 0;
            }
            //获取商户配置
            MerchantConfig merchantConfig=user.getMerchant().getConfig();
            if(merchantConfig==null){
                return 0;
            }
            if(goods.getRebateConfiguration()==null){
                return 0;
            }
            //获取会员等级
            long userLevelId=user.getLevelId();
            //如果积分转汇率为0，则算作100
            if(merchantConfig.getExchangeRate()==0){
                merchantConfig.setExchangeRate(100);
            }
            //商品返利是否是个性化
            Boolean ind=goods.getIndividuation();

            //商城默认的返利配置
            if(ind==null||!ind){
                List<RebatePair> rebatePairList=null;
                //获取到底是按照销售额还是配额计算返利
                RebateMode rebateMode= merchantConfig.getRebateMode();

                if(RebateMode.BySale.equals(rebateMode)){  //按销售额
                    if(merchantConfig.getBySale()==null){
                        return 0;
                    }
                    rebatePairList=merchantConfig.getBySale().getBelong1Ratios();


                }else if(RebateMode.ByQuota.equals(rebateMode)) { //按配额
                    if(merchantConfig.getByQuato()==null){
                        return 0;
                    }
                    rebatePairList=merchantConfig.getByQuato().getBelong1Ratios();

                }
                if(rebatePairList==null||rebatePairList.size()==0){
                    return 0;
                }

                Integer ratio=null;
                //获取用户等级所对应的比例
                for(int i=0;i<rebatePairList.size();i++){
                    if(rebatePairList.get(i).getLevelId()==userLevelId){
                        ratio=rebatePairList.get(i).getRatio();
                    }
                }

                if(ratio!=null){
                    double rebate=Math.rint(goods.getPrice()*goods.getRebateConfiguration().getSaleRatio()*ratio/merchantConfig.getExchangeRate());
                    return rebate;
                }else {
                    return 0;
                }

            }else {//商户自己定义的返利配置
                List<RebatePair> rebatePairList=null;
                RebateConfiguration rebateConfiguration= goods.getRebateConfiguration();
                if(rebateConfiguration==null){
                    return 0;
                }

                Double saleRatio=null;


                //获取到底是按照销售额还是配额计算返利
                RebateMode rebateMode=rebateConfiguration.getMode();

                if(RebateMode.BySale.equals(rebateMode)){//按销售额
                    if(rebateConfiguration.getSale()==null){
                        return 0;
                    }
                    rebatePairList=rebateConfiguration.getSale().getBelong1Ratios();
                    saleRatio=rebateConfiguration.getSaleRatio();


                }else if(RebateMode.ByQuota.equals(rebateMode)) {   //按配额
                    if(rebateConfiguration.getQuato()==null){
                        return 0;
                    }
                    rebatePairList=rebateConfiguration.getQuato().getBelong1Ratios();
                    saleRatio=rebateConfiguration.getQuatoRadio();

                }
                if(saleRatio==null){
                    return 0;
                }
                if(rebatePairList==null||rebatePairList.size()==0){
                    return 0;
                }

                Integer ratio=null;
                for(int i=0;i<rebatePairList.size();i++){
                    if(rebatePairList.get(i).getLevelId()==userLevelId){
                        ratio=rebatePairList.get(i).getRatio();
                    }
                }
                //计算返利
                if(ratio!=null){
                    double rebate=Math.rint(goods.getPrice()*saleRatio*ratio/merchantConfig.getExchangeRate());
                    return rebate;
                }else {
                    return 0;
                }
            }
        }catch (Exception e){
            return 0;
        }

    }


    /**
     * 去除特殊字符，包括 $,\,'
     * @param string
     * @return
     */
    public static String filterSpecialCharacter(String string){
        if(string==null){
            return "";
        }
        string=string.replace("$","");
        string=string.replace("\\","");
        string=string.replace("\'","");
        return string;
    }

}
