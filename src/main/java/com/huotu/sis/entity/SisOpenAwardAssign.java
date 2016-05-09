package com.huotu.sis.entity;

import com.huotu.huobanplus.common.entity.Merchant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.persistence.*;

/**
 * Created by slt on 2016/3/31.
 */
@Entity
@Table(name = "SIS_OpenAwardAssign")
@Getter
@Setter
@Cacheable(value = false)
public class SisOpenAwardAssign {

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

    @Description("上线用户店铺的等级")
    @JoinColumn(name = "LevelId")
    @ManyToOne
    private SisLevel level;

    @Description("开店用户店铺等级")
    @ManyToOne
    @JoinColumn(name = "GuideLevelId")
    private SisLevel guideLevel;

    @Column(name = "AdvanceVal")
    @Description("增加的余额")
    private Double advanceVal=0.0;

    @Column(name = "Integral")
    @Description("增加的积分")
    private Integer integral=0;

    @Column(name = "UpgradeIntegral")
    @Description("升级增加的积分")
    private Integer upgradeIntegral=0;

    @Column(name = "GoldVal")
    @Description("增加的金币")
    private Double goldVal=0.0;




}
