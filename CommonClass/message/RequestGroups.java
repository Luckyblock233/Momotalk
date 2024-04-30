package CommonClass.message;

public class RequestGroups {
    private String userId;
    private String type;

    public RequestGroups() {
        this.type=JsonMessage.GROUPS;
    }

    public RequestGroups(String userId) {
        this.userId = userId;
        this.type=JsonMessage.GROUPS;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
