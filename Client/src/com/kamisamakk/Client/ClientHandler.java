package Client.src.com.kamisamakk.Client;

import CommonClass.*;
import CommonClass.message.*;
import Client.src.com.kamisamakk.ui.LoginFrame;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    public ClientHandler(Socket socket) throws IOException {
        this.socket=socket;
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer=new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (true)
        {
            try {
                //接收服务器的信息
                String msg=reader.readLine();
                if(msg==null)
                {
                    System.out.println("客户端与服务器断开连接");
                    Client.getClient().reconnect();
                    break;
                }
                JSONObject jsonObject=JSONObject.fromObject(msg);
                String type=jsonObject.getString("type");
                if(type.equals(JsonMessage.LOGIN))
                {
                    login(jsonObject);
                }else if(type.equals(JsonMessage.RELOGIN))
                {
                    JOptionPane.showMessageDialog(null,"请勿重复登录");
                }else if(type.equals(JsonMessage.FAIL))
                {
                    JOptionPane.showMessageDialog(null,"登录失败");
                }else if(type.equals(JsonMessage.FRIENDS))
                {
                    getFriendsList(jsonObject);
                }else if(type.equals(JsonMessage.CHAT))
                {
                    getChatMsg(jsonObject);
                }else if(type.equals(JsonMessage.GROUPS)) {
                    getGroupsList(jsonObject);
                }
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("客户端与服务器断开连接");
                reconnect();
            }
        }
    }

    private void getChatMsg(JSONObject jsonObject) {
        LoginFrame.getLoginFrame().addTxtHistory(jsonObject, "RECEIVE");
    }

    private void getFriendsList(JSONObject jsonObject) {
        JSONArray jsonArray=jsonObject.getJSONArray("friendsList");
        ArrayList<User> friendsList=(ArrayList<User>) JSONArray.toCollection(jsonArray,User.class);
//        System.out.println(friendsList);
        LoginFrame.getLoginFrame().getFriendModel().clear();
        LoginFrame.getLoginFrame().getFriendModel().addElement("我的好友");
        for (User user:friendsList) {
            String text = user.getUserId() + " " + user.getUserName();
            LoginFrame.getLoginFrame().getFriendModel().addElement(text);
        }
    }
    private void getGroupsList(JSONObject jsonObject) {
        JSONArray jsonArray=jsonObject.getJSONArray("groupsList");
        ArrayList<Group> groupsList=(ArrayList<Group>) JSONArray.toCollection(jsonArray,Group.class);
//        System.out.println(groupsList);
        LoginFrame.getLoginFrame().getGroupModel().clear();
        LoginFrame.getLoginFrame().getGroupModel().addElement("我的群组");
        for (Group group: groupsList) {
            String text = group.getGroupId() + " " + group.getGroupName();
            LoginFrame.getLoginFrame().getGroupModel().addElement(text);
        }
    }

    private void reconnect() {
        Client.getClient().reconnect();
        this.socket=Client.getClient().getSocket();
        try {
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer=new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login(JSONObject jsonObject) {
        ResponseLogin responseLogin=(ResponseLogin)JSONObject.toBean(jsonObject,ResponseLogin.class);
        User user=responseLogin.getUser();
        if(user!=null)
        {
            JOptionPane.showMessageDialog(null,"登录成功");
            RequestFriends requestFriends=new RequestFriends(user.getUserId());
            String msg=JsonMessage.ObjToJson(requestFriends);
            Client.getClient().send(msg);

            RequestGroups requestGroups=new RequestGroups(user.getUserId());
            msg = JsonMessage.ObjToJson(requestGroups);
            Client.getClient().send(msg);

            LoginFrame.getLoginFrame().setTitle("Momotalk " + user.getUserName());
        }
        else {
            JOptionPane.showMessageDialog(null,"登录失败");
        }
    }
}
