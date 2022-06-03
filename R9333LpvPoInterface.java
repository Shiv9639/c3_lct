package com.C3Collection.C3.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection ="R9333LpvPoInterface")
public class R9333LpvPoInterface {

    @Id
    private String id;
    private String incoTerms2;

    private String processIndicator;
    private String purchaseOrderId;
    private String purchaseOrderType;
    private String supplierName;
    private String erpOrderType;


    public R9333LpvPoInterface() {
        // TODO Auto-generated constructor stub
    }


    public R9333LpvPoInterface(String id, String incoTerms2, String processIndicator, String purchaseOrderId,
                               String purchaseOrderType, String supplierName, String erpOrderType) {
        super();
        this.id = id;
        this.incoTerms2 = incoTerms2;
        this.processIndicator = processIndicator;
        purchaseOrderId = purchaseOrderId;
        this.purchaseOrderType = purchaseOrderType;
        this.supplierName = supplierName;
        this.erpOrderType=erpOrderType;
    }

    public String getErpOrderType() {
        return erpOrderType;
    }

    public void setErpOrderType(String erpOrderType) {
        this.erpOrderType = erpOrderType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIncoTerms2() {
        return incoTerms2;
    }

    public void setIncoTerms2(String incoTerms2) {
        this.incoTerms2 = incoTerms2;
    }

    public String getProcessIndicator() {
        return processIndicator;
    }

    public void setProcessIndicator(String processIndicator) {
        this.processIndicator = processIndicator;
    }

    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(String purchaseOrderId) {
        purchaseOrderId = purchaseOrderId;
    }

    public String getPurchaseOrderType() {
        return purchaseOrderType;
    }

    public void setPurchaseOrderType(String purchaseOrderType) {
        this.purchaseOrderType = purchaseOrderType;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}