package cn.javayuli.businessserver.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * websocket服务
 *
 * @author hanguilin
 */
@Component
@ServerEndpoint("/endpoint/business")
public class SocketHandler {

    private static CopyOnWriteArraySet<Session> sessionSet = new CopyOnWriteArraySet<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketHandler.class);

    @OnOpen
    public void onOpen(Session session) {
        sessionSet.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {

    }

    @OnClose
    public void onClose(Session session) {
        sessionSet.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessionSet.remove(session);
        LOGGER.error("服务端发生错误: {}", throwable);
    }

    /**
     * 发送消息给所有连接
     *
     * @param message 消息
     */
    public static void sendMessage (String message) {
        sessionSet.forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOGGER.error("服务端发送消息失败: {}", e);
            }
        });
    }

    /**
     * 发送消息给指定连接
     *
     * @param message 消息
     * @param sessionId session的id
     */
    public static void sendMessage (String message, String sessionId) {
        Optional<Session> first = sessionSet.stream().filter(o -> sessionId.equals(o.getId())).findFirst();
        first.ifPresent(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOGGER.error("服务端发送消息失败: {}", e);
            }
        });
    }

}
