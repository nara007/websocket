package com.sy.webchat;

import javax.websocket.Session;

public class UserInfo 
{
	private Session session;
	private String userName;
	
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
