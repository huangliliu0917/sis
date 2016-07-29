package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.*;
import com.huotu.huobanplus.common.repository.MerchantConfigRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.huobanplus.common.repository.UserTempIntegralHistoryRepository;
import com.huotu.sis.common.DateHelper;
import com.huotu.sis.common.MathHelper;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.*;
import com.huotu.sis.model.sis.SisSearchModel;
import com.huotu.sis.model.sisweb.SisRebateModel;
import com.huotu.sis.model.sisweb.UserTempIntegralHistoryModel;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.repository.SisOrderItemsRepository;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.service.SisService;
import com.huotu.sis.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2016/1/29.
 */
@Service
public class SisServiceImpl implements SisService {
    private static Log log = LogFactory.getLog(SisServiceImpl.class);
    @Autowired
    SisRepository sisRepository;

    @Autowired
    UserTempIntegralHistoryRepository userTempIntegralHistoryRepository;

    @Autowired
    private SisOrderItemsRepository sisOrderItemsRepository;

    @Autowired
    private SisLevelRepository sisLevelRepository;

    @Autowired
    private MerchantConfigRepository merchantConfigRepository;

    @Autowired
    private SisConfigRepository sisConfigRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public long getSisLevelId(User user) throws Exception {
        Sis sis=sisRepository.findByUser(user);
        if(!Objects.isNull(sis)){
            SisLevel sisLevel=sis.getSisLevel();
            if(!Objects.isNull(sisLevel)){
                return sisLevel.getId();
            }
        }
        return 0;
    }

    @Override
    public Integer getSisLevel(User user) {
        Sis sis=sisRepository.findByUser(user);
        if(!Objects.isNull(sis)){
            SisLevel sisLevel=sis.getSisLevel();
            if(!Objects.isNull(sisLevel)){
                return sisLevel.getLevelNo();
            }
        }
        return null;
    }

    @Override
    public Page<Sis> findSisList(SisSearchModel sisSearchModel) throws Exception {
        log.debug(sisSearchModel.getCustomerId()+"into findSisList");
        return  sisRepository.findAll(new Specification<Sis>() {
            @Override
            public Predicate toPredicate(Root<Sis> root, CriteriaQuery<?> query, CriteriaBuilder cb){

                /**
                 * 前提是属于某个商家的店中店
                 */
                Predicate predicate = cb.equal(root.get("customerId").as(Long.class),
                        sisSearchModel.getCustomerId());

                //加入标题模糊搜索
                if (!StringUtils.isEmpty(sisSearchModel.getUserLoginName())){
                    predicate = cb.and(predicate,cb.like(root.get("user").get("loginName").as(String.class),
                            "%" + sisSearchModel.getUserLoginName()+"%"));
                }
                //加入时间搜索大于
                if(!StringUtils.isEmpty(sisSearchModel.getStartTime())){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(sisSearchModel.getStartTime());
                    } catch (ParseException e) {
                        throw  new RuntimeException("字符串转日期失败");
                    }
                    predicate=cb.and(predicate,cb.greaterThanOrEqualTo(root.get("openTime").as(Date.class),date));
                }
                //加入时间搜索小于
                if(!StringUtils.isEmpty(sisSearchModel.getEndTime())){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(sisSearchModel.getEndTime());
                    } catch (ParseException e) {
                        throw  new RuntimeException("字符串转日期失败");
                    }
                    predicate=cb.and(predicate,cb.lessThanOrEqualTo(root.get("openTime").as(Date.class),date));
                }
                return predicate;
            }
        },new PageRequest(sisSearchModel.getPageNoStr(), 10,new Sort(Sort.Direction.DESC,"id")));
    }

    @Override
    public void calculatePushAward(User user, Order order, String unionOrderId,SisConfig sisConfig) throws Exception {
        List<UserTempIntegralHistoryModel> models=new ArrayList<>();
        MerchantConfig merchantConfig =merchantConfigRepository.findByMerchant(user.getMerchant());
        if(Objects.isNull(merchantConfig)){
            //未找到商家配置信息，无法返利
            log.info("userId:"+user.getId()+" Store configuration information was not found to rebate");
            return;
        }
        List<OrderItems> orderItems = sisOrderItemsRepository.getOrderItemsByOrderId(order.getId());
        if(Objects.isNull(orderItems)){
            //未找到订单明细
            log.info("userId:"+user.getId()+" Did not find the order details");
            return;
        }
        Long shopLevelId=getSisLevelId(user);//店主店铺等级ID
        if(shopLevelId==0){
            //未找到店铺等级
            log.info("userId:"+user.getId()+" Store level was not found");
            return;
        }
        SisLevel sisLevel = sisLevelRepository.findOne(shopLevelId);
        if(Objects.isNull(sisLevel)){
            //未找到店铺等级，无法返利
            log.info("userId:"+user.getId()+" Store level was not found,Unable to rebate");
            return;
        }

        //积分兑换钱的比例
        int exchangeRate=merchantConfig.getExchangeRate();

        //积分转正天数
        int positiveDay=merchantConfig.getPositiveDay();

        //直推模式
        Integer pushAwardMode=sisConfig.getPushAwardMode();
        if(pushAwardMode==null){
            pushAwardMode=0;
        }

        switch (pushAwardMode){
            case 0://默认直推奖模式
//                UserTempIntegralHistoryModel model=countDefPush(user,order,sisLevel);
                //贡献人
                User contriUser=userRepository.findOne((long)order.getUserId());
                models=newCountDefPush(user,contriUser,sisConfig,shopLevelId);
                break;
            case 1://经营者直推奖模式
                models=countProprietor(user,order,sisConfig);
                break;
            default:
                break;
        }

        if(models.isEmpty()){
            log.info("userId:"+user.getId()+" Straight award calculation is empty");
            return;
        }
        log.debug("zhitui:"+models.toString());

        List<UserTempIntegralHistory> userTempIntegralHistories=new ArrayList<>();
        models.stream().filter(model->model.getPushRatio()!=0).forEach(model -> {
            UserTempIntegralHistory utih=new UserTempIntegralHistory();
            //返利的积分
            int integral=countTotalIntegral(orderItems,model.getPushRatio(),exchangeRate);
            model.setIntegral(integral);
            utih.setCustomerId(user.getMerchant().getId());
            utih.setIntegral(integral);//不同的
            utih.setUnionOrderId(unionOrderId);
            utih.setUserId(model.getUser().getId());//受益人的id//不同的
            utih.setStatus(0);
            utih.setAddTime(new Date());
            utih.setContributeUserID(model.getContributeUser().getId());
            utih.setContributeBelongOne(model.getContributeUser().getBelongOne().intValue());//不同的
            utih.setContributeUserType(model.getContributeUser().getUserType().ordinal());//不同的
            utih.setDesc("店主直推奖，订单号 :"+order.getId());
            utih.setPositiveFlag(1);
            utih.setUserLevelId((long)model.getUser().getLevelId());//不同的
            utih.setEstimatePostime(DateHelper.countPositiveTime(positiveDay));
            utih.setUserType(model.getUser().getUserType());//不同的
            utih.setType(0);
            utih.setNewType(500);
            utih.setOrder(order);
            utih.setContributeDesc(null);
            utih.setFlowIntegral(integral);//不同的
            utih.setContributeUserID(model.getContributeUser().getId());//不同的
            utih.setUserGroupId(0L);
            userTempIntegralHistories.add(utih);
        });
        //保存实体
        userTempIntegralHistoryRepository.save(userTempIntegralHistories);
        //用户临时积分修改
        saveUsersTempIntegral(models);

    }

    @Override
    public List<UserTempIntegralHistoryModel> countProprietor(User user, Order order,SisConfig sisConfig) throws Exception {

        List<UserTempIntegralHistoryModel> models=new ArrayList<>();
        if(sisConfig.getSisRebateTeamManagerSetting()==null){
            //没有直推奖配置信息
            log.info("userId:"+user.getId()+" No direct push prize configuration information");
            return models;
        }

        SisRebateTeamManagerSetting setting=sisConfig.getSisRebateTeamManagerSetting();
        List<User> users=userService.getAllRelationByUserId(user.getId());
        //没有可以返利的用户
//        log.info("userId:"+user.getId()+" No user can rebate");

        models=countUserTempIntegralHistoryModel(order,users,setting);

        //店主应该拿的比例
        double userSispushRate=setting.getSaleAward();

        //店主的管理奖金加上销售奖金
        models.get(0).setPushRatio(models.get(0).getPushRatio()+userSispushRate);
        //店主的贡献人为订单用户
        models.get(0).setContributeUser(userRepository.findOne((long)order.getUserId()));
        return models;
    }

    @Override
    public UserTempIntegralHistoryModel countDefPush(User user, Order order, SisLevel userSisLevel) throws Exception {
        UserTempIntegralHistoryModel model=new UserTempIntegralHistoryModel();
        //获得贡献人
        User contriUser=userRepository.findOne((long)order.getUserId());
        model.setUser(user);
        model.setContributeUser(contriUser);
        model.setPushRatio(userSisLevel.getRebateRate());
        return model;

    }

    @Override
    public List<UserTempIntegralHistoryModel> newCountDefPush(User user, User contriUser,SisConfig sisConfig,Long sislevelId) throws Exception {
        List<UserTempIntegralHistoryModel> models=new ArrayList<>();

        SisLevelAwards sisLevelPushAwards=sisConfig.getSisLevelPushAwards();
        if(sisLevelPushAwards==null){
            sisLevelPushAwards=userService.oldPushAwardCompatibility(sislevelId);
        }

        List<SisRebateModel> sisRebateModels=userService.getSisRebateModelList(user,sisLevelPushAwards.get(0L));

        for(SisRebateModel s:sisRebateModels){
            UserTempIntegralHistoryModel model=new UserTempIntegralHistoryModel();
            model.setUser(s.getUser());
            model.setContributeUser(contriUser);
            model.setPushRatio(s.getRebate());
            models.add(model);
        }
        return models;
    }

    @Override
    public List<UserTempIntegralHistory> convertModelToUserTempIntegralHistory(List<UserTempIntegralHistoryModel> models) throws Exception {
        List<UserTempIntegralHistory> list=new ArrayList<>();
        models.forEach(model->{
//
        });
        return list;
    }

    @Override
    public List<UserTempIntegralHistory> saveUserTempIntegralHistory(List<UserTempIntegralHistory> userTempIntegralHistories) throws Exception {
        return  userTempIntegralHistoryRepository.save(userTempIntegralHistories);
    }

    @Override
    public List<User> saveUsersTempIntegral(List<UserTempIntegralHistoryModel> models) throws Exception {
        List<User> users=new ArrayList<>();
        models.forEach(model->{
            User user=model.getUser();
            user.setUserTempIntegral(user.getUserTempIntegral()+model.getIntegral());
            users.add(user);
        });
        return userRepository.save(users);
    }

    @Override
    public double countOrderItemsTotalAmount(List<OrderItems> orderItemses) {
        double totalAmount = 0;
        for (int i = 0; i < orderItemses.size(); i++) {
            double pushAmount =  orderItemses.get(i).getZhituiPrize();
            totalAmount += pushAmount;
        }
        log.debug("orderItemses:"+totalAmount);
        return totalAmount;
    }

    @Override
    public int countTotalIntegral(List<OrderItems> orderItemses,double rebateRate,int exchangeRate) {
        double totalOrderItemsAmount=countOrderItemsTotalAmount(orderItemses)*rebateRate/100;
        log.debug("rebateRate:"+rebateRate+" exchangeRate:"+exchangeRate);
        return MathHelper.getIntegralRateByRate(totalOrderItemsAmount,exchangeRate);
    }

    @Override
    public List<UserTempIntegralHistoryModel> countUserTempIntegralHistoryModel(Order order,List<User> users, SisRebateTeamManagerSetting setting) throws Exception {
        List<UserTempIntegralHistoryModel> models=new ArrayList<>();
        List<RelationAndPercent> percents=setting.getManageAwards();

        //默认贡献人是订单用户
        User contributeUser=new User();
        if(order!=null){
            contributeUser=users.get(0);
        }


        int minSisLevelNo=-1;
        int userNo=0;
        int tierNo=0;
        Integer levelLeftNo=-1;
        Integer levelRightNo=-1;

        while (true){
            //只要所有的管理奖金比例列表都遍历完毕，或可返利用户列表遍历完毕则跳出循环
            if(userNo>=users.size()||tierNo>=percents.size()){
                break;
            }

            //可能返利用户
            User rebateUser=users.get(userNo);
            //可返利的直推比例
            double rate=0;
            //可能返利用户的店铺
            Sis rebateUserSis=sisRepository.findByUser(rebateUser);

            if(rebateUserSis!=null && rebateUserSis.getSisLevel()!=null){

                //可能返利用户的店铺等级序号
                Integer rebateUserSisLevelNo=rebateUserSis.getSisLevel().getLevelNo();
                while (true){
                    if(tierNo>=percents.size()){
                        break;
                    }
                    RelationAndPercent relationAndPercent=percents.get(tierNo);
                    String[] levelNos=relationAndPercent.getRelation().split("_");
                    //可得比例
                    double percent=relationAndPercent.getPercent();
                    if(levelNos.length!=2){
                        tierNo++;
                        continue;
                    }
                    levelLeftNo=Integer.valueOf(levelNos[0]);
                    levelRightNo=Integer.valueOf(levelNos[1]);
                    if(levelLeftNo<rebateUserSisLevelNo||(minSisLevelNo==levelLeftNo&&rebateUserSisLevelNo>=levelRightNo)){
                        rate+=percent;
                        tierNo++;
                    }else {
                        break;
                    }
                }
            }
            UserTempIntegralHistoryModel model=new UserTempIntegralHistoryModel();
            model.setUser(rebateUser);
            model.setContributeUser(contributeUser);
            model.setPushRatio(rate);
            models.add(model);
            if(rate>0){
                contributeUser=rebateUser;
            }
            minSisLevelNo=levelLeftNo;
            userNo++;
        }
        return models;
    }

    @Override
    public List<Double> testUserTempIntegralHistoryModel(List<Integer>levels,List<RelationAndPercent> manageAwards) throws Exception {
        List<Double> percents=new ArrayList<>();
        int minSisLevelNo=-1;
        int tierNo=0;
        Integer levelLeftNo=-1;
        Integer levelRightNo=-1;
        for(int i=0;i<levels.size();i++){
            double rate=0;
            Integer rebateUserSisLevelNo=levels.get(i);
            while (true){
                if(tierNo>=manageAwards.size()){
                    break;
                }
                RelationAndPercent relationAndPercent=manageAwards.get(tierNo);
                String[] levelNos=relationAndPercent.getRelation().split("_");
                //可得比例
                double percent=relationAndPercent.getPercent();

                levelLeftNo=Integer.valueOf(levelNos[0]);
                levelRightNo=Integer.valueOf(levelNos[1]);
                if(levelLeftNo<rebateUserSisLevelNo||(minSisLevelNo==levelLeftNo&&rebateUserSisLevelNo>=levelRightNo)){
                    rate+=percent;
                    tierNo++;
                }else {
                    break;
                }
            }
            percents.add(rate);
            minSisLevelNo=levelLeftNo;
        }
        return percents;
    }

    @Override
    public double countPushPercent(Integer levelNo, List<RelationAndPercent> manageAwards) {
        int tierNo=0;
        //可返利的直推比例
        double rate=0;
        Integer levelLeftNo=-1;
        while (true){
            if(tierNo>=manageAwards.size()){
                break;
            }
            RelationAndPercent relationAndPercent=manageAwards.get(tierNo);
            String[] levelNos=relationAndPercent.getRelation().split("_");
            //可得比例
            double percent=relationAndPercent.getPercent();
            if(levelNos.length!=2){
                tierNo++;
                continue;
            }
            levelLeftNo=Integer.valueOf(levelNos[0]);
            if(levelLeftNo<levelNo){
                rate+=percent;
                tierNo++;
            }else {
                break;
            }
        }
        return rate;
    }
}
