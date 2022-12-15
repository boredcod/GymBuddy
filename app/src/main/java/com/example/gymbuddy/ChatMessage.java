package com.example.gymbuddy;

import java.util.HashMap;
import java.util.Map;
//Class for Chat Message
public class ChatMessage {
    public Map<String, Boolean> users = new HashMap<>();
    public Map<String,Chat> chats = new HashMap<>();
    public static class Chat {
        public String userEmail;
        public String message;
        public Object timestamp;
    }
}
