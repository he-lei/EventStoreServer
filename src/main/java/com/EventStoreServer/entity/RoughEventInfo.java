package com.EventStoreServer.entity;


/**
 * 粗略事件信息类
 */
public class RoughEventInfo {
    // 业务对象名称
    private String businessObjectName;

    // 业务对象ID
    private String businessObjectUUID;

    // 事件编号
    private Integer eventNumber;

    // 事件类型
    private String eventType;

    // 完整数据地址
    private long completeDataAddress;

    // 事件时间
    private long eventTime;

    /**
     * 无参构造方法，为粗略时间信息对象初始化：
     * 业务对象名称: 25字节
     * 业务对象ID: 32字节
     * 事件编号：9字节
     * 事件类型：16字节
     * 完整数据地址：10字节
     * 事件时间：8字节
     */
    public RoughEventInfo() {
        this.businessObjectName = null;
        this.businessObjectUUID = null;
        this.eventNumber = 0;
        this.eventType = null;
        this.completeDataAddress = 0;
        this.eventTime = 0;
    }

    public RoughEventInfo(String businessObjectName, String businessObjectUUID, Integer eventNumber, String eventType,
                          long completeDataAddress, long eventTime) {
        this.businessObjectName = businessObjectName;
        this.businessObjectUUID = businessObjectUUID;
        this.eventNumber = eventNumber;
        this.eventType = eventType;
        this.completeDataAddress = completeDataAddress;
        this.eventTime = eventTime;
    }

    // 将数据中的业务对象名称+业务对象id组合成索引结构中的关键字，用“_”分隔
    public String getKey() {
        return this.businessObjectName + "_" + this.businessObjectUUID;
    }

    //将数据中的事件编号+事件类型+事件信息存入地址+时间，用“_”连接
    public String getValue(){
        return this.eventNumber + "_" + this.eventType + "_" + this.completeDataAddress + "_" + this.eventTime;
    }

    public String getBusinessObjectName() {
        return businessObjectName;
    }

    public void setBusinessObjectName(String businessObjectName) {
        this.businessObjectName = businessObjectName;
    }

    public String getBusinessObjectUUID() {
        return businessObjectUUID;
    }

    public void setBusinessObjectUUID(String businessObjectUUID) {
        this.businessObjectUUID = businessObjectUUID;
    }

    public Integer getEventNumber() {
        return eventNumber;
    }

    public void setEventNumber(Integer eventNumber) {
        this.eventNumber = eventNumber;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getCompleteDataAddress() {
        return completeDataAddress;
    }

    public void setCompleteDataAddress(long completeDataAddress) {
        this.completeDataAddress = completeDataAddress;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Integer eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return businessObjectName + businessObjectUUID + eventNumber + eventType + completeDataAddress + eventTime;
    }
}
