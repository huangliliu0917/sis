package com.huotu.sis.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by jinzj on 2016/2/1.
 */
public class PageOrderModel {

    private List<OrderHistoryModel> Rows;

    private int Total;

    private int PageCount;

    private int PageIndex;

    private int PageSize;

    @JsonProperty(value = "Rows")
    public List<OrderHistoryModel> getRows() {
        return Rows;
    }

    public void setRows(List<OrderHistoryModel> Rows) {
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
