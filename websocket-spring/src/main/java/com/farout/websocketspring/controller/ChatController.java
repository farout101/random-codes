package com.farout.websocketspring.controller;

import com.farout.websocketspring.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class ChatController {

    @MessageMapping("chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // Logic to handle sending the message
        // This could involve broadcasting the message to a topic or queue
        return chatMessage; // Return the message for confirmation or further processing
    }

    @MessageMapping("chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpleMessageHeaderAccessor accessor) {
        // Logic to handle adding a user
        accessor.getSessionAttributes().put("username", chatMessage.getSender());
        // This could involve updating the user list or notifying other users
        return chatMessage; // Return the message for confirmation or further processing
    }
}
