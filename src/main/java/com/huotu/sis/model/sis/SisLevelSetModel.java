package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lgh on 2016/3/31.
 */
public class SisLevelSetModel {

    @Getter
    @Setter
    private long customerId;

    private double rebate;

    public static Integer isLevelSet;

    private int id;

    private int level;

    private Integer zhituidianpuAmount;

    private Integer tuanduidianpuAmount;

    private String nickname;

    private Integer isSystem;

    private Integer extraUpgrade;

    public void setExtraUpgrade(Integer extraUpgrade) {this.extraUpgrade = extraUpgrade;}

    public Integer getExtraUpgrade() {return extraUpgrade;}

    public void setIsSystem(Integer isSystem)
    {
        this.isSystem = isSystem;
    }

    public Integer getIsSystem()
    {
        return isSystem;
    }

    public int getLevel()
    {
        return level;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getZhituidianpuAmount()
    {
        return zhituidianpuAmount;
    }

    public int getTuanduidianpuAmount()
    {
        return tuanduidianpuAmount;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public void setZhituidianpuAmount(int zhituidianpuAmount)
    {
        this.zhituidianpuAmount = zhituidianpuAmount;
    }

    public void setTuanduidianpuAmount(int tuanduidianpuAmount)
    {
        this.tuanduidianpuAmount = tuanduidianpuAmount;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public double getRebate()
    {
        return rebate;
    }

    public void setRebate(double rebate)
    {
        this.rebate = rebate;
    }
}
