package com.huotu.sis.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by jinzj on 2015/12/30.
 */
public class PageGoodsModel {

    private List<PcSisGoodsModel> Rows;

    private int Total;

    private int PageCount;

    private int PageIndex;

    private int PageSize;



    @JsonProperty(value = "Rows")
    public List<PcSisGoodsModel> getRows() {
        return Rows;
    }

    public void setRows(List<PcSisGoodsModel> Rows) {
        this.Rows = Rows;
    }

    @JsonProperty(value = "Total")
    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    @JsonProperty(value = "PageCount")
    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int PageCount) {
        this.PageCount = PageCount;
    }

    @JsonProperty(value = "PageIndex")
    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int PageIndex) {
        this.PageIndex = PageIndex;
    }

    @JsonProperty(value = "PageSize")
    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int PageSize) {
        this.PageSize = PageSize;
    }
}
