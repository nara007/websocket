package com.sy.webchat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

import com.sy.process.AnglebracketProcessor;


@ServerEndpoint("/error")
public class Websocket 
{
	static private ArrayList<UserInfo> sessionArray=new ArrayList<UserInfo>();
	static private Map<Session,UserInfo> sessionMap=new HashMap<Session,UserInfo>();
	@OnMessage
	public void onMessage(String message, Session session)
	    throws IOException, InterruptedException 
	{
	   
	    // Print the client message for testing purposes

	   
	    // Send a final message to the client
		parseJson(message, session);
		System.out.println("Received: "+message+" "+session.getId());
	}
	   
	  @OnOpen
	  public void onOpen(Session session) 
	  {
		String ID=session.getId();
	    System.out.println("Client connected :"+ID);
	    JSONObject outputJsonObject = new JSONObject();
	    try{
	    	outputJsonObject.put("messageType", "initSession");
	    	outputJsonObject.put("sessionID", ID);
	    	session.getBasicRemote().sendText(outputJsonObject.toString());
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	  }
	 
	  @OnClose
	  public void onClose(Session session) 
	  {
	    System.out.println("Connection closed");

	    Websocket.sessionArray.remove(Websocket.sessionMap.get(session));
	    Websocket.sessionMap.remove(session);
	    try
	    {
	    	JSONObject outputJsonObject = new JSONObject();
	    	outputJsonObject.put("messageType", "userCancel");
	    	outputJsonObject.put("userID", session.getId());
	    
	    	for(UserInfo myUserInfo:Websocket.sessionArray)
	    	{
	    		myUserInfo.getSession().getBasicRemote().sendText(outputJsonObject.toString());
	    	}	
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    
	  }
	  
	  
	  private void parseJson(String jsonString,Session session)
	  {
		  //System.out.println("parser start");
		  JSONObject jo=null;
		  String msType=null;
		  try{
			  jo = new JSONObject(jsonString);
			  msType = jo.getString("messageType");
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  
		  if(msType.equals("open"))
		  {
			  UserInfo userInfo=new UserInfo();
			  userInfo.setSession(session);
			  try {
				userInfo.setUserName(jo.getString("username"));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			  Websocket.sessionArray.add(userInfo);
			  Websocket.sessionMap.put(session, userInfo);
			  //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.GERMANY); 
			  //String time = dateFormat.format(new Date());
			  JSONObject outputJsonObject = new JSONObject();
			  try{
				  outputJsonObject.put("messageType", "welcome");
				  outputJsonObject.put("serverTime", getCurrentTime());
				  outputJsonObject.put("username", new AnglebracketProcessor(null).processString(jo.getString("username")));
				  outputJsonObject.put("userID", session.getId());
				  
				  for(UserInfo myUserInfo:Websocket.sessionArray)
				  {
					  //String retText="["+time+"] "+"锟斤拷迎"+jo.getString("username")+"锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷";
					  myUserInfo.getSession().getBasicRemote().sendText(outputJsonObject.toString());
					  //System.out.println(outputJsonObject.toString());
					  //System.out.println("parser open");
				  }
				  
				  for(UserInfo myUserInfo:Websocket.sessionArray)
				  {
					  JSONObject localJsonObject = new JSONObject();
					  localJsonObject.put("messageType", "updateUserInfo");
					  //localJsonObject.put("serverTime", getCurrentTime());
					  localJsonObject.put("username", new AnglebracketProcessor(null).processString(myUserInfo.getUserName()));
					  localJsonObject.put("userID", myUserInfo.getSession().getId());	
					  session.getBasicRemote().sendText(localJsonObject.toString());
				  }
				  
				  //System.out.println("parser open");
			  }
			  catch(Exception e)
			  {
				  e.printStackTrace();
			  }
		  }
		  
		  else if(msType.equals("chat"))
		  {
			  JSONObject outputJsonObject = new JSONObject();
			  try{
				  outputJsonObject.put("messageType", "chat");
				  outputJsonObject.put("serverTime", getCurrentTime());
				  outputJsonObject.put("senderID", jo.getString("msgSender"));
				  outputJsonObject.put("receiverID", jo.getString("msgReceiver"));
				  outputJsonObject.put("text", jo.getString("chatText"));
				  
				  for(UserInfo myUserInfo:Websocket.sessionArray)
				  {
					  //String retText="["+time+"] "+"锟斤拷迎"+jo.getString("username")+"锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷";
					  myUserInfo.getSession().getBasicRemote().sendText(outputJsonObject.toString());
					  //System.out.println(outputJsonObject.toString());
				  }
				  
				  //System.out.println("parser chat");
			  }
			  catch(Exception e)
			  {
				  e.printStackTrace();
			  }			  
		  }
	  }
	  
	  private String getCurrentTime()
	  {
		  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.GERMANY); 
		  String time = dateFormat.format(new Date());
		  
		  return time;
	  }	  
	  
	  
}
