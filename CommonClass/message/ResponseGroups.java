package CommonClass.message;

import CommonClass.*;

import java.util.ArrayList;

public class ResponseGroups {
    private String type;
    private ArrayList<Group> groupsList;

    public ResponseGroups(ArrayList<Group> groupsList) {
        this.groupsList = groupsList;
        this.type=JsonMessage.GROUPS;
    }

    public ResponseGroups() {
        this.type=JsonMessage.GROUPS;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Group> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(ArrayList<Group> groupsList) {
        this.groupsList = groupsList;
    }
}
