package com.extrainch.pos.ui.orders.data;

public class PurchaseRemoteData {
    private int id;
    private int supplierId;
    private String requestDate;
    private String paymentStatusId;
    private String purchaseStatusId;
    private String supplier;
    private String product;
    private String quantity;
    private String pUnitCost;
    private String pTotalCost;
    private String pRemarks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getPurchaseStatusId() {
        return purchaseStatusId;
    }

    public void setPurchaseStatusId(String purchaseStatusId) {
        this.purchaseStatusId = purchaseStatusId;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getpUnitCost() {
        return pUnitCost;
    }

    public void setpUnitCost(String pUnitCost) {
        this.pUnitCost = pUnitCost;
    }

    public String getpTotalCost() {
        return pTotalCost;
    }

    public void setpTotalCost(String pTotalCost) {
        this.pTotalCost = pTotalCost;
    }

    public String getpRemarks() {
        return pRemarks;
    }

    public void setpRemarks(String pRemarks) {
        this.pRemarks = pRemarks;
    }
}
