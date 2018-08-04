package com.techbeloved.hymnbook.about;

public class Library {

    private String name;
    private String copyright;
    private String license;
    private String licenseDetail;
    private String detailInfo;

    Library(String name, String copyright, String license) {
        this.name = name;
        this.copyright = copyright;
        this.license = license;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseDetail() {
        return licenseDetail;
    }

    public void setLicenseDetail(String licenseDetail) {
        this.licenseDetail = licenseDetail;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }
}
