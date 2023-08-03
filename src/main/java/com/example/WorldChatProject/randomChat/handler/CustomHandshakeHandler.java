//package com.example.WorldChatProject.randomChat.handler;
//
//import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//
//import java.security.Principal;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class CustomHandshakeHandler extends DefaultHandshakeHandler {
//    @Override
//    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
//                                      Map<String, Object> attributes) {
//        PrincipalDetails principal = null;
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() != null
//                && authentication.getPrincipal() instanceof PrincipalDetails) {
//            principal = (PrincipalDetails) authentication.getPrincipal();
//            if (principal != null) {
//                String roomId = request.getURI().getPath().split("/")[2];
//                attributes.put("roomId", roomId);
//                connectedUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet())
//                        .add(principal.getUsername());
//            }
//        }
//        return principal;
//    }
//}
//
