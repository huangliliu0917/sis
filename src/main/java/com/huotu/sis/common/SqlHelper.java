package com.huotu.sis.common;

import org.springframework.util.StringUtils;

/**
 * Created by slt on 2015/12/25.
 */
public class SqlHelper {

    /**
     * 获得查询店主精选的sql语句，包含分页
     * @param title     商品标题
     * @param pageNo    页数
     * @param pageSize  每页条数
     * @return
     */
    public static String getfindPageSisRecommendGoodsSql(String title,Integer pageNo,Integer pageSize){
        StringBuffer hql=new StringBuffer();
        //分页
        hql.append("SELECT * FROM (SELECT ROW_NUMBER() OVER(ORDER BY b.SortNo DESC,b.RecId DESC)AS Row, * FROM (");
        hql.append("SELECT " +
                "mg.Goods_Id," +
                "mg.Name," +
                "mg.Small_Pic," +
                "mg.Store," +
                "mg.Price," +
                "mg.Price_LevelDesc," +
                "mg.ShopRebateMin," +
                "mg.Individuation," +
                "mg.rebateConfiguration," +
                "sg.selected," +
                "sgr.SortNo," +
                "sgr.RecId " +
                "FROM Mall_Goods as mg " +
                "        INNER JOIN SIS_Goods_Recommend as sgr ON mg.Goods_Id=sgr.GoodsId " +
                "        LEFT JOIN SIS_SisGoods as sg ON mg.Goods_Id=sg.GOODS_Goods_Id and sg.USER_UB_UserID=? " +
                " WHERE sgr.CustomerId=? and  " +
                " mg.Goods_Scenes=0 AND " +
                " mg.Marketable=1 and " +
                " mg.Disabled=0");
        if(!StringUtils.isEmpty(title)){
            hql.append(" and  mg.Name LIKE ? ");
        }
//        hql.append("ORDER BY sgr.SortNo DESC,sgr.RecId DESC");
        //分页END
        hql.append(") as b )AS A WHERE a.Row BETWEEN ("+pageNo+"-1)*"+pageSize+"+1 AND "+pageNo+"*"+pageSize);
        return hql.toString();
    }

    /**
     *  获得查询店主精选商品总数的sql语句
     * @param title     商品标题
     * @return
     */
    public static String getCountSisRecommendGoodsSql(String title){
        StringBuffer hql=new StringBuffer();
        //分页
        hql.append("SELECT count(*) FROM Mall_Goods as mg " +
                "        INNER JOIN SIS_Goods_Recommend as sgr ON mg.Goods_Id=sgr.GoodsId " +
                "        LEFT JOIN SIS_SisGoods as sg ON mg.Goods_Id=sg.GOODS_Goods_Id and sg.USER_UB_UserID=? " +
                " WHERE sgr.CustomerId=? and  " +
                " mg.Goods_Scenes=0 AND " +
                " mg.Marketable=1 and " +
                " mg.Disabled=0 and " +
                " sg.deleted=0 ");
        if(!StringUtils.isEmpty(title)){
            hql.append(" and  mg.Name LIKE ? ");
        }
//        hql.append("ORDER BY sgr.SortNo DESC,sgr.RecId DESC");
        return hql.toString();
    }


    /**
     * 获得查询店主精选的sql语句，包含分页
     * @param title     商品标题
     * @return
     */
    public static String getfindPageSisRecommendGoodsHql(String title){
        StringBuffer hql=new StringBuffer();
        //分页
        hql.append("SELECT " +
                "mg," +
                "sg.selected " +
                " FROM Goods as mg " +
                "        INNER JOIN SisGoodsRecommend as sgr ON mg.id=sgr.goodsId " +
                "        LEFT JOIN SisGoods as sg ON mg=sg.goods and sg.user=:user " +
                " WHERE sgr.customerId=:customerId and  " +
                " mg.scenes=0 AND " +
                " mg.marketable=true and " +
                " mg.disabled=false");
        if(!StringUtils.isEmpty(title)){
            hql.append(" and  mg.title LIKE :title ");
        }
        hql.append(" ORDER BY sgr.sortNo DESC,sgr.id DESC");
        return hql.toString();
    }



}
