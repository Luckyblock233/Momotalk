package CommonClass.message;

public class RequestChatMsg {
    private String type;
    private String targetType;
    private String sendId;
    private String friendId;
    private String groupId;
    private String chatMsg;

    public RequestChatMsg() {
        this.type=JsonMessage.CHAT;
    }

    public RequestChatMsg(String sendId, String friendId, String groupId, String chatMsg) {
        this.sendId = sendId;
        this.friendId = friendId;
        this.groupId = groupId;
        this.chatMsg = chatMsg;
        this.type=JsonMessage.CHAT;

        if (friendId.isEmpty() && groupId.isEmpty()) {
            this.targetType=JsonMessage.ALL;
        } else if (friendId.isEmpty()) {
            this.targetType=JsonMessage.GROUPS;
        } else if (groupId.isEmpty()) {
            this.targetType=JsonMessage.FRIENDS;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(String chatMsg) {
        this.chatMsg = chatMsg;
    }

    public String getTargetType() {return targetType; }

    public void setTargetType(String targetType) {this.targetType = targetType; }

    public String getGroupId() {return groupId; }

    public void setGroupId(String groupId) {this.groupId = groupId; }
}
