package com.intoverflown.pos.ui.merchantbranch.data;

public class BranchRemoteDate {
    int id;
    String regionId;
    String branchName;
    String address;
    String phone;
    String contactPerson;
    String merchants;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getMerchants() {
        return merchants;
    }

    public void setMerchants(String merchants) {
        this.merchants = merchants;
    }
}
