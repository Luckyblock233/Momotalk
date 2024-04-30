package Client.src.com.kamisamakk.ui;

import Client.src.com.kamisamakk.Client.Client;
import CommonClass.message.*;
import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginFrame extends JFrame {
    //登陆控件
    JTextField txtUserField;
    JTextField txtPwdField;
    JButton btnLogin;
    JButton btnReg;
    //好友区控件
    JList friend_list, group_list;
    DefaultListModel friend_model, group_model;
    //消息区控件
    JTextArea txtHistory = new JTextArea();
    JTextArea txtSend = new JTextArea();
    JButton btnSend = new JButton();
    JButton btnSendToAll = new JButton();
    private static LoginFrame loginFrame;

    public static LoginFrame getLoginFrame() {
        if (loginFrame == null) {
            loginFrame = new LoginFrame();
        }
        return loginFrame;
    }

    public LoginFrame()
    {
        System.out.println(Thread.currentThread().getName());
        // TODO Auto-generated constructor stub

        this.setTitle("Momotalk");

        txtUserField = new JTextField();
        txtPwdField = new JTextField();
        btnLogin = new JButton();
        btnLogin.setText("登录");

        btnReg = new JButton();
        btnReg.setText("注册");

        //左侧登录块
        this.setLayout(null);
        this.add(txtUserField);
        txtUserField.setBounds(10, 10, 100, 30);
        this.add(txtPwdField);
        txtPwdField.setBounds(10, 50, 100, 30);
        this.add(btnLogin);
        btnLogin.setBounds(10, 90, 100, 30);
        btnReg.setBounds(10, 130, 100, 30);
        this.add(btnReg);

        // JList是一个view，要添加数据，就是添加到model
        friend_model = new DefaultListModel();// Model
        friend_list = new JList(friend_model);// View
        this.add(friend_list);
        friend_list.setBounds(120, 10, 150, 410);

        group_model = new DefaultListModel();
        group_list = new JList(group_model);// View
        this.add(group_list);
        group_list.setBounds(290, 10, 150, 410);

        //消息块
        txtHistory.setBounds(460, 10, 410, 200);
        this.add(txtHistory);
        txtSend.setBounds(460, 220, 410, 200);
        this.add(txtSend);
        btnSend.setBounds(790, 430, 80, 30);
        this.add(btnSend);
        btnSend.setText("发送");
        btnSendToAll.setBounds(700, 430, 80, 30);
        this.add(btnSendToAll);
        btnSendToAll.setText("广播");

        //窗口布局
        setSize(900, 520);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent a)
            {
                super.windowClosing(a);
                int key=JOptionPane.showConfirmDialog(null,"是否要退出", "温馨提示",JOptionPane.OK_CANCEL_OPTION);
                if(key==JOptionPane.OK_CANCEL_OPTION)
                    System.exit(0);
            }
        });

        btnReg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                RegisterFrame.getRegisterFrame();
            }
        });

        //登录的监听事件
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                RequestLogin requestLogin=new RequestLogin(txtUserField.getText(),txtPwdField.getText());
                String msg= JsonMessage.ObjToJson(requestLogin);
                Client.getClient().send(msg);
            }
        });

        btnSend.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String friendId = "";
                String groupId = "";
                try {
                    friendId = friend_list.getSelectedValue().toString().split(" ")[0];
                } catch (Exception ex) {};
                try {
                    groupId = group_list.getSelectedValue().toString().split(" ")[0];
                } catch (Exception ex) {};
                if (friendId.isEmpty() && groupId.isEmpty()) {
                    JOptionPane.showMessageDialog(null,"请选择发送对象！");
                    return ;
                }
                RequestChatMsg requestSendMsg=new RequestChatMsg(txtUserField.getText(), friendId, groupId, txtSend.getText());
                String chatMsg=JsonMessage.ObjToJson(requestSendMsg);
                addTxtHistory(JSONObject.fromObject(requestSendMsg), "SEND");
                Client.getClient().send(chatMsg);
            }
        });
        btnSendToAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                RequestChatMsg requestSendMsg=new RequestChatMsg(txtUserField.getText(), "", "", txtSend.getText());
                String chatMsg=JsonMessage.ObjToJson(requestSendMsg);
                addTxtHistory(JSONObject.fromObject(requestSendMsg), "SEND");
                Client.getClient().send(chatMsg);
            }
        });

        friend_list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                group_list.clearSelection();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                group_list.clearSelection();
            }
        });

        group_list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                friend_list.clearSelection();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                friend_list.clearSelection();
            }
        });
    }

    public void addTxtHistory(JSONObject jsonObject, String type) {
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeNow=format.format(date);
        String chatMsg=jsonObject.getString("chatMsg");
        String targetType=jsonObject.getString("targetType");
        String chat= "";
        if (type.equals("RECEIVE")) {
            String sendId=jsonObject.getString("sendId");
            if (targetType.equals(JsonMessage.FRIENDS)) {
                chat=timeNow+" "+sendId+" 对你说:\n"+chatMsg+"\n";
            } else if (targetType.equals(JsonMessage.GROUPS)){
                String groupId=jsonObject.getString("groupId");
                chat=timeNow+" "+sendId+" 在群组 "+groupId+" 中说:\n"+chatMsg+"\n";
            } else {
                chat=timeNow+" "+sendId+" 对所有人说:\n"+chatMsg+"\n";
            }
        } else if (type.equals("SEND")) {
            if (targetType.equals(JsonMessage.FRIENDS)) {
                String groupId=jsonObject.getString("friendId");
                chat=timeNow+" 你对 " + groupId + " 说:\n"+chatMsg+"\n";
            } else if (targetType.equals(JsonMessage.GROUPS)){
                String groupId=jsonObject.getString("groupId");
                chat=timeNow+" 你在群组 "+groupId+" 中说:\n"+chatMsg+"\n";
            } else {
                chat=timeNow+" 你对所有人说:\n"+chatMsg+"\n";
            }
        }
        getTxtHistory().append(chat);
    }

    public JTextField getTxtUserField() {
        return txtUserField;
    }

    public void setTxtUserField(JTextField txtUserField) {
        this.txtUserField = txtUserField;
    }

    public JTextField getTxtPwdField() {
        return txtPwdField;
    }

    public void setTxtPwdField(JTextField txtPwdField) {
        this.txtPwdField = txtPwdField;
    }

    public JList getFriendList() {
        return friend_list;
    }

    public void setFriend_list(JList list) {
        this.friend_list = friend_list;
    }

    public JList getGroupList() {
        return group_list;
    }

    public void setGroup_list(JList list) {
        this.group_list = group_list;
    }

    public DefaultListModel getFriendModel() {
        return friend_model;
    }
    public DefaultListModel getGroupModel() {
        return group_model;
    }

    public void setFriendModel(DefaultListModel model) {
        this.friend_model = friend_model;
    }
    public void setGroupModel(DefaultListModel model) {
        this.group_model = group_model;
    }

    public JTextArea getTxtHistory() {
        return txtHistory;
    }

    public void setTxtHistory(JTextArea txtHistory) {
        this.txtHistory = txtHistory;
    }

    public JTextArea getTxtSend() {
        return txtSend;
    }

    public void setTxtSend(JTextArea txtSend) {
        this.txtSend = txtSend;
    }
}
