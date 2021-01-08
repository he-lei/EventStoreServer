package com.EventStoreServer.entity;


/**
 * 完整事件信息类
 */
public class CompleteEventInfo {
    // 业务对象名称
    private String businessObjectName;

    // 业务对象ID
    private Integer businessObjectUUID;

    // 事件编号
    private Integer eventNumber;

    // 事件类型
    private String eventType;

    // 事件时间
    private Integer eventTime;

    // 事件地址
    private String completeDataAddress;

    public String getBusinessObjectName() {
        return businessObjectName;
    }

    public void setBusinessObjectName(String businessObjectName) {
        this.businessObjectName = businessObjectName;
    }

    public Integer getBusinessObjectUUID() {
        return businessObjectUUID;
    }

    public void setBusinessObjectUUID(Integer businessObjectUUID) {
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

    public Integer getEventTime() {
        return eventTime;
    }

    public void setEventTime(Integer eventTime) {
        this.eventTime = eventTime;
    }

    public String getCompleteDataAddress() {
        return completeDataAddress;
    }

    public void setCompleteDataAddress(String completeDataAddress) {
        this.completeDataAddress = completeDataAddress;
    }

    public CompleteEventInfo(String businessObjectName, Integer businessObjectUUID, Integer eventNumber,
                             String eventType, Integer eventTime, String completeDataAddress) {
        this.businessObjectName = businessObjectName;
        this.businessObjectUUID = businessObjectUUID;
        this.eventNumber = eventNumber;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.completeDataAddress = completeDataAddress;
    }

    @Override
    public String toString() {
        return "CompleteEventInfo{" +
                "businessObjectName='" + businessObjectName + '\'' +
                ", businessObjectUUID=" + businessObjectUUID +
                ", eventNumber=" + eventNumber +
                ", eventType='" + eventType + '\'' +
                ", eventTime=" + eventTime +
                ", completeDataAddress='" + completeDataAddress + '\'' +
                '}';
    }
}
