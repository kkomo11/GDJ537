package com.app.home.messenger;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SocketHandler extends TextWebSocketHandler{

	HashMap<String, WebSocketSession>sessionMap=new HashMap<>(); // 웹소켓 세션을 담아둘 맵
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	//메서지 발송
		
		String msg= message.getPayload();
		for (String key: sessionMap.keySet()) {
			WebSocketSession wss= sessionMap.get(key);
			try {
			wss.sendMessage(new TextMessage(msg));
			}catch(Exception e) {
				e.printStackTrace();
			}
     	}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	
	//소켓 연결
		super.afterConnectionEstablished(session);
		sessionMap.put(session.getId(), session);
		JSONObject obj =new JSONObject();
		obj.put("type", "getId");
		obj.put("sessionId", session.getId());
		session.sendMessage(new TextMessage(obj.toJSONString()));
	}

	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
	
	// 소켓 종료
		
	sessionMap.remove(session.getId());
	super.afterConnectionClosed(session, status);
	}


     public static JSONObject jsonToOJsonObject(String jsonStr) {
    	
    	 JSONParser parser =new JSONParser();
    	 JSONObject obj= null;
    	 
    	 try {
			obj=(JSONObject)parser.parse(jsonStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
    	 return obj;
     }



}


