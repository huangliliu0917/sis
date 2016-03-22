package com.huotu.sis.entity;

import com.huotu.huobanplus.common.entity.Brand;
import com.huotu.huobanplus.common.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.persistence.*;

/**
 * Created by jinzj on 2016/1/25.
 */
@Entity
@Table(name = "SIS_SisBrand")
@Getter
@Setter
@Cacheable(value = false)
public class SisBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description("主键，rest不可见")
    private Long id;

    @ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.MERGE})
    @Description("该店中店商品的店主")
    private User user;

    @ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.MERGE})
    @Description("关联的实际品牌")
    private Brand brand;

    @Description("是否已上架")
    private boolean selected;

    /**
     * 降序排序
     *
     * 每次请求置顶时，则设置该值为聚合max+1
     */
    @Description("排序权重")
    private int orderWeight = 50;
}
