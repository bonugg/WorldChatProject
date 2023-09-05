    package com.example.WorldChatProject.webChat.dto;

    import org.springframework.web.socket.WebSocketSession;

    import java.util.Map;
    import java.util.Set;
    import java.util.concurrent.ConcurrentHashMap;

    public class UserSessionManager {
        // 싱글톤 인스턴스
        private static final UserSessionManager instance = new UserSessionManager();

        // 유저 세션을 저장하는 map
        private final Map<String, WebSocketSession> userSessionMapByUsername = new ConcurrentHashMap<>();

        // private 생성자: 외부에서 인스턴스 생성을 막음
        private UserSessionManager() {}

        // 싱글톤 인스턴스 반환 메서드
        public static UserSessionManager getInstance() {
            return instance;
        }

        public void addUserSession(String username, WebSocketSession session) {
            userSessionMapByUsername.put(username, session);
        }

        public WebSocketSession getUserSession(String username) {
            return userSessionMapByUsername.get(username);
        }

        public void removeUserSession(String username) {
            userSessionMapByUsername.remove(username);
        }

        public Set<String> getAllKeys() {
            return userSessionMapByUsername.keySet();
        }

        // 필요한 다른 메서드들도 추가할 수 있습니다.
    }
