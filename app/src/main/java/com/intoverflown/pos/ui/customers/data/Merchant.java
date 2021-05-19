package com.intoverflown.pos.ui.customers.data;

public class Merchant {
    private String countryId;
    private String createdById;
    private String dateCreated;
    private int id;
    private String merchantName;
    private String merchantTypeId;
    private String statusId;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantTypeId() {
        return merchantTypeId;
    }

    public void setMerchantTypeId(String merchantTypeId) {
        this.merchantTypeId = merchantTypeId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
}
