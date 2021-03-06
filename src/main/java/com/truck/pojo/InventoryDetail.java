package com.truck.pojo;

import java.util.Date;

public class InventoryDetail {
    private Integer id;

    private Integer inventoryId;

    private Integer stockId;

    private Integer stockNum;

    private Integer inventoryNum;

    private Date createTime;

    private Date updateTime;

    private Integer status;

    private String errorDescs;

    public InventoryDetail(Integer id,Integer inventoryId, Integer stockId, Integer stockNum, Integer inventoryNum, Date createTime, Date updateTime,Integer status,String errorDescs) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.stockId = stockId;
        this.stockNum = stockNum;
        this.inventoryNum = inventoryNum;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.status = status;
        this.errorDescs = errorDescs;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public InventoryDetail() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public Integer getStockNum() {
        return stockNum;
    }

    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }

    public Integer getInventoryNum() {
        return inventoryNum;
    }

    public void setInventoryNum(Integer inventoryNum) {
        this.inventoryNum = inventoryNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorDescs() {
        return errorDescs;
    }

    public void setErrorDescs(String errorDescs) {
        this.errorDescs = errorDescs;
    }
}