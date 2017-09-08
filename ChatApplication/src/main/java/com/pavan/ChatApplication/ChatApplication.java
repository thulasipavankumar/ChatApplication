/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pavan.ChatApplication;

import java.util.ArrayList;
import java.util.HashMap;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Pavan Kumar Tulasi thulasipavankumar@gmail.com Cognizant
 */
@ServerEndpoint("/demo")
public class ChatApplication {

    private static ArrayList<Session> sessionList = new ArrayList<Session>();
    private static HashMap<String, String> objectsList = new HashMap<String, String>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("opened a connection " + session.getId());
        sessionList.add(session);
        sendMessageToAllSenders("new user has joined the chat ",session.getId());
    }

    @OnMessage
    public void message(String message, Session session) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(message);
            String command = (String) json.get("message");
            System.out.println("command in onmessage :" + command);
            sendMessageToAllSenders(command,session.getId());
        } catch (Exception ex) {
            System.out.println("exception is "+ex.getMessage());
        }
    }

    private void sendMessageToAllSenders(String message, String origin) {
        origin = "";
        org.json.simple.JSONObject jobj = new org.json.simple.JSONObject();
        jobj.put("message", message);
        for (Session s : sessionList) 
            if (!s.getId().equals(origin)) 
                try {
                    s.getBasicRemote().sendText(jobj.toJSONString());
                    System.out.println("Sent the message to "+s.getId());
                } catch (Exception ex) {
                    System.out.println("Exception in sending the message to client");
                }
            
        
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    sessionList.remove(session);
        System.out.println("!!! \n" + session.getId() + "connection closed due to onClose " + closeReason.getCloseCode() + "\n");

    }
}
