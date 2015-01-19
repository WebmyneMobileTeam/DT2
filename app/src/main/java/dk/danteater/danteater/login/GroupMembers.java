package dk.danteater.danteater.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 16-09-2014.
 */
public class GroupMembers {

    @SerializedName("uid")
    private String uid;
    @SerializedName("mail")
    private String mail;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("passwd")
    private String passwd;
    @SerializedName("sn")
    private String sn ;
    @SerializedName("sex")
    private String sex;
    @SerializedName("primaryGroup")
    private String primaryGroup ;
    @SerializedName("street")
    private String street;
    @SerializedName("org_code")
    private String org_code;
    @SerializedName("loginAllowed")
    private String loginAllowed ;
    @SerializedName("l")
    private String l;
    @SerializedName("givenName")
    private String givenName;
    @SerializedName("loginExpirationDate")
    private String loginExpirationDate;
    @SerializedName("postalCode")
    private String postalCode;
    @SerializedName("telephoneNumber")
    private String telephoneNumber;
    @SerializedName("domain")
    private String domain;
    @SerializedName("st")
    private String st;
    @SerializedName("mobile")
    private String mobile;

    public GroupMembers(String uid, String mail, String birthday, String passwd, String sn, String sex, String primaryGroup, String street, String org_code, String loginAllowed, String l, String givenName, String loginExpirationDate, String postalCode, String telephoneNumber, String domain, String st, String mobile) {
        this.uid = uid;
        this.mail = mail;
        this.birthday = birthday;
        this.passwd = passwd;
        this.sn = sn;
        this.sex = sex;
        this.primaryGroup = primaryGroup;
        this.street = street;
        this.org_code = org_code;
        this.loginAllowed = loginAllowed;
        this.l = l;
        this.givenName = givenName;
        this.loginExpirationDate = loginExpirationDate;
        this.postalCode = postalCode;
        this.telephoneNumber = telephoneNumber;
        this.domain = domain;
        this.st = st;
        this.mobile = mobile;
    }

    public GroupMembers() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPrimaryGroup() {
        return primaryGroup;
    }

    public void setPrimaryGroup(String primaryGroup) {
        this.primaryGroup = primaryGroup;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getLoginAllowed() {
        return loginAllowed;
    }

    public void setLoginAllowed(String loginAllowed) {
        this.loginAllowed = loginAllowed;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getLoginExpirationDate() {
        return loginExpirationDate;
    }

    public void setLoginExpirationDate(String loginExpirationDate) {
        this.loginExpirationDate = loginExpirationDate;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
