package dk.danteater.danteater.login;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nirav on 16-09-2014.
 */
public class BeanGroupResult {

    @SerializedName("groups")
    private ArrayList<Group> groupArrayList;


    public ArrayList<Group> getGroupArrayList() {
        return groupArrayList;
    }

    public void setGroupArrayList(ArrayList<Group> groupArrayList) {
        this.groupArrayList = groupArrayList;
    }
}
