package dk.danteater.danteater.login;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

/**
 * Created by nirav on 16-09-2014.
 */
public class Group {


    @SerializedName("domain")
    private String domain;
    @SerializedName("groupType")
    private String groupType;
    @SerializedName("displayName")
    private String groupName;
    @SerializedName("cn")
    private String groupId;
    @SerializedName("calculatedName")
    private String calculatedName;
    @SerializedName("description")
    private String description;
    @SerializedName("firstSchoolYear")
    private String firstSchoolYear;

    public Group(String domain, String groupType, String groupName, String groupId, String calculatedName, String description, String firstSchoolYear) {
        this.domain = domain;
        this.groupType = groupType;
        this.groupName = groupName;
        this.groupId = groupId;
        this.calculatedName = calculatedName;
        this.description = description;
        this.firstSchoolYear = firstSchoolYear;
    }

    public Group() {
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCalculatedName() {
        return calculatedName;
    }

    public void setCalculatedName(String calculatedName) {
        this.calculatedName = calculatedName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirstSchoolYear() {
        return firstSchoolYear;
    }

    public void setFirstSchoolYear(String firstSchoolYear) {
        this.firstSchoolYear = firstSchoolYear;
    }


}
