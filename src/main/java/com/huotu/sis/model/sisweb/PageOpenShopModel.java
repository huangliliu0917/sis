package com.huotu.sis.model.sisweb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by slt on 2016/2/1.
 */
public class PageOpenShopModel {

    private List<SisDetailModel> Rows;

    /**
     * 总条数
     */
    private int Total;

    /**
     * 总页数
     */
    private int PageCount;

    /**
     * 当前页数
     */
    private int PageIndex;

    /**
     * 每页几条
     */
    private int PageSize;

    @JsonProperty(value = "Rows")
    public List<SisDetailModel> getRows() {
        return Rows;
    }

    public void setRows(List<SisDetailModel> Rows) {
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
