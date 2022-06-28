package com.C3Collection.C3.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
@Document(collection="ReasonCodes")
public class ReasonCodeMaster {

    @Id
    private String id;
    private String purchaseOrderNumber;
    private String reasonCode;
    private LocalDateTime timeStamp;
    private String reasonCodeDescription;
    private String poType;
    private String fileName;
    private int row_count;

    public int getRow_count() {
        return row_count;
    }

    public void setRow_count(int row_count) {
        this.row_count = row_count;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ReasonCodeMaster(){}

    public ReasonCodeMaster(String id, String purchaseOrderNumber, String reasonCode,LocalDateTime timeStamp,
                            String reasonCodeDescription, String poType, String fileName, int row_count) {
        this.id = id;
        this.purchaseOrderNumber = purchaseOrderNumber;
        this.reasonCode = reasonCode;   
        this.timeStamp = timeStamp;
        this.reasonCodeDescription = reasonCodeDescription;
        this.poType = poType;
        this.fileName=fileName;
        this.row_count=row_count;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getReasonCodeDescription() {
        return reasonCodeDescription;
    }

    public void setReasonCodeDescription(String reasonCodeDescription) {
        this.reasonCodeDescription = reasonCodeDescription;
    }

    public String getPoType() {
        return poType;
    }

    public void setPoType(String poType) {
        this.poType = poType;
    }











}
