package Server.src.com.kamisamakk.server;

import CommonClass.*;
import CommonClass.message.*;
import Server.src.com.kamisamakk.dao.DaoFactory;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private User user;
    private static Map<String,Socket> onlineMap=new HashMap<>();
    private static Map<String,Group> groupMap=new HashMap<>();
    public ServerHandler(Socket socket) throws IOException {
        this.socket=socket;
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer=new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (true)
        {
            try {
                String msg=reader.readLine();
                System.out.println(msg);
                if(msg==null) {
                    close();
                    break;
                }
                JSONObject object=JSONObject.fromObject(msg);
                String type=object.getString("type");
                if(type.equals(JsonMessage.LOGIN)) {
                    login(object);
                }else if(type.equals(JsonMessage.FRIENDS)) {
                    sendFriendList(object);
                }else if(type.equals(JsonMessage.CHAT)) {
                    if (object.getString("targetType").equals(JsonMessage.GROUPS)) {
                        sendChatGroupMsg(object);
                    } else if (object.getString("targetType").equals(JsonMessage.FRIENDS)){
                        sendChatMsg(object);
                    } else if (object.getString("targetType").equals(JsonMessage.ALL)) {
                        sendChatAllMsg(object);
                    }
                }else if(type.equals(JsonMessage.REGISTER)) {
                    register(object);
                }else if(type.equals(JsonMessage.GROUPS)){
                    sendGroupList(object);
                }
            } catch (IOException | SQLException e) {
                //e.printStackTrace();
                System.out.println("用户退出");
                close();
                break;
            }
        }
    }

    //注册
    private void register(JSONObject object) {
        String msg=object.getString("user");
        JSONObject jsonObject=JSONObject.fromObject(msg);
        User user=(User)JSONObject.toBean(jsonObject,User.class);
        User newUser=DaoFactory.getUserDao().register(user);
        ResponseRegister responseRegister=new ResponseRegister(newUser);
        String reMsg=JsonMessage.ObjToJson(responseRegister);
        writer.println(reMsg);
        writer.flush();
    }

    //转发消息
    private void sendChatMsg(JSONObject object) {
        String socket= object.getString("friendId");
        Socket friendSocket=onlineMap.get(socket);
        System.out.println(friendSocket);
        if(friendSocket!=null) {
            try {
                writer=new PrintWriter(friendSocket.getOutputStream());
                writer.println(object);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void sendChatGroupMsg(JSONObject object) {
        String sendId = object.getString("sendId");
        Group group = groupMap.get(object.getString("groupId"));
        writer.println(group);
        for (User user: group.getGroupMembers()) {
            if (user.getUserId().equals(sendId)) continue;
            object.put("friendId", user.getUserId());
            sendChatMsg(object);
        }
    }

    private void sendChatAllMsg(JSONObject object) {
        String sendId = object.getString("sendId");
        Socket sendSocket=onlineMap.get(sendId);
        for (Socket socket: onlineMap.values()) {
            if (socket != null && socket != sendSocket) {
                try {
                    writer=new PrintWriter(socket.getOutputStream());
                    writer.println(object);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //响应好友列表请求
    private void sendFriendList(JSONObject object) throws SQLException {
        String userId=object.getString("userId");
        ArrayList<User> friendList=DaoFactory.getUserDao().friends(userId);
        String reMsg="";
        if(friendList!=null)
        {
            ResponseFriends responseFriends=new ResponseFriends(friendList);
            reMsg=JsonMessage.ObjToJson(responseFriends);
            writer.println(reMsg);
            writer.flush();
        }
    }
    private void sendGroupList(JSONObject object) throws SQLException {
        String userId = object.getString("userId");
        ArrayList<Group> groupList=DaoFactory.getUserDao().groups(userId);
//        System.out.println(groupList);
        String reMsg="";
        if (groupList!=null) {
            ResponseGroups responseGroups=new ResponseGroups(groupList);
            reMsg=JsonMessage.ObjToJson(responseGroups);
            writer.println(reMsg);
            writer.flush();

            for (Group group: groupList) {
                synchronized (ServerHandler.class) {
                    if (!groupMap.containsKey(group.getGroupId())) {
                        groupMap.put(group.getGroupId(), group);
                    }
                }
            }
        }
    }

    private void close() {
        if(user!=null) {
            synchronized (ServerHandler.class) {
                onlineMap.remove(user.getUserId());
            }
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //登录
    private void login(JSONObject object) throws SQLException {
        String userId=object.getString("userId");
        String userPassword=object.getString("userPassword");
        String reMsg="";
        user = DaoFactory.getUserDao().login(userId,userPassword);
        if(user!=null) {
            synchronized (ServerHandler.class) {
                if (onlineMap.containsKey(user.getUserId())) {
                    //用户已登录
                    ResponseLogin responseLogin = new ResponseLogin();
                    responseLogin.setType(JsonMessage.RELOGIN);
                    reMsg = JsonMessage.ObjToJson(responseLogin);
                } else {
                    //首次登录
                    onlineMap.put(user.getUserId(), socket);
                    System.out.println(onlineMap.toString());
                    ResponseLogin responseLogin = new ResponseLogin(user);
                    reMsg = JsonMessage.ObjToJson(responseLogin);
                }
            }
        } else {
            //用户名或密码错误、用户不存在
            ResponseLogin responseLogin=new ResponseLogin();
            responseLogin.setType(JsonMessage.FAIL);
            reMsg=JsonMessage.ObjToJson(responseLogin);
        }
        writer.println(reMsg);
        writer.flush();
    }
}
