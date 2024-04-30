package CommonClass;

import CommonClass.User;

import java.util.ArrayList;

public class Group {
    private String GroupId;
    private String GroupName;
    private ArrayList<User> GroupMembers = new ArrayList<User>();

    public Group() {super();}

    public Group(String GroupId, String GroupName, ArrayList<User> GroupMembers) {
        super();
        this.GroupId = GroupId;
        this.GroupName = GroupName;
        this.GroupMembers = GroupMembers;
    }

    public String getGroupId() { return this.GroupId; }
    public void setGroupId(String GroupId) { this.GroupId = GroupId; }
    public String getGroupName() { return this.GroupName;}
    public void setGroupName(String GroupName) { this.GroupName = GroupName; }
    public ArrayList<User> getGroupMembers() { return this.GroupMembers;}
    public void setGroupMembers(ArrayList<User> GroupMembers) { this.GroupMembers = GroupMembers; }

    @Override
    public String toString() {
        return "Group [GroupId=" + GroupId + ", GroupName=" + GroupName + "]";
    }
}
