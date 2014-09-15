package wm.com.danteater.login;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nirav on 12-09-2014.
 */
public class BeanUser {

    @SerializedName("givenName")
    private String firstName;
    @SerializedName("sn")
    private String lastName;
    @SerializedName("uid")
    private String userId;
    @SerializedName("primaryGroup")
    private String primaryGroup;
    @SerializedName("roles")
    private ArrayList<String> roles;
    @SerializedName("domain")
    private String domain;

    public BeanUser(String firstName, String lastName, String userId, String primaryGroup, ArrayList<String> roles, String domain) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.primaryGroup = primaryGroup;
        this.roles = roles;
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrimaryGroup() {
        return primaryGroup;
    }

    public void setPrimaryGroup(String primaryGroup) {
        this.primaryGroup = primaryGroup;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }
}
