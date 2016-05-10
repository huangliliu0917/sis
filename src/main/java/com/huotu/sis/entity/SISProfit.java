package com.huotu.sis.entity;

import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.sis.entity.support.ProfitUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.persistence.*;

/**
 * Created by jinzj on 2016/5/9.
 */
@Entity
@Table(name = "Sis_Profit")
@Getter
@Setter
@Cacheable(value = false)
public class SISProfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    @Description("主键ID")
    private Long id;

    @JoinColumn(name = "CustomerId")
    @Description("商户")
    @ManyToOne
    private Merchant merchant;

    @Column(name = "UserLevel")
    @Description("用户等级，总代1，总代2")
    private Long userLevel;

    @Description("店铺等级")
    @JoinColumn(name = "LevelId")
    @ManyToOne
    private SisLevel level;

    private ProfitUser profitUser;

    private Double profit;
}
