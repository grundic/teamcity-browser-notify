/*
 * The MIT License
 *
 * Copyright (c) 2018 Grigory Chernyshev.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.grundic.browser.notificator.websocket;

import com.github.grundic.browser.notificator.Constants;
import com.github.grundic.browser.notificator.MessageBean;
import com.google.gson.Gson;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NotificationEndpoint {
    private final Logger LOG = Logger.getInstance(NotificationEndpoint.class.getName());
    private static final String USER_ID = "USER_ID";

    private static final Map<Long, Queue<Session>> peers = new ConcurrentHashMap<>();

    private final Gson myGson = new Gson();

    @OnOpen
    public void onOpen(@NotNull Session session, @NotNull EndpointConfig config) {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        if (null == httpSession) {
            LOG.error("Can't get http session from websocket!");
            return;
        }

        final SUser currentUser = SessionUser.getUser(httpSession);
        if (currentUser == null) {
            LOG.error("Websocket open request with unknown user!");
            return;
        }
        LOG.debug(String.format("WebSocket connection is opened by %s. Connection id: %s",
                currentUser.getUsername(), session.getId())
        );

        // Store connection
        session.getUserProperties().put(USER_ID, currentUser.getId());
        Queue<Session> sessions = peers.get(currentUser.getId());
        if (sessions == null) {
            sessions = new ConcurrentLinkedQueue<>();
            peers.put(currentUser.getId(), sessions);
        }

        sessions.add(session);
    }

    @OnClose
    public void onClose(@NotNull Session session) {
        session.getUserProperties().get(USER_ID);
        final Long userId = (Long) session.getUserProperties().get(USER_ID);
        final Queue<Session> sessions = peers.get(userId);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    public void broadcast(@NotNull MessageBean message, @NotNull Set<SUser> users) {
        for (SUser user : users) {
            message.timeout = getTimeout(user);
            final String jsonMessage = myGson.toJson(message);

            final Queue<Session> sessions = peers.get(user.getId());
            if (sessions != null) {
                for (Session session : sessions) {
                    if (!session.isOpen()) {
                        continue;
                    }

                    try {
                        session.getBasicRemote().sendText(jsonMessage);
                    } catch (IOException e) {
                        LOG.error(e);
                    }
                }
            }
        }
    }

    /**
     * Method for getting notification display timeout. The value is user configurable from the UI.
     * If not set, default value will be used.
     * @param user - user for whom to get timeout setting.
     * @return - timeout in seconds.
     */
    private static int getTimeout(SUser user) {
        String timeout = user.getPropertyValue(new NotificatorPropertyKey(Constants.PLUGIN_TYPE, Constants.NOTIFICATION_TIMEOUT));
        if (null == timeout || timeout.isEmpty()) {
            timeout = Integer.toString(Constants.DEFAULT_TIMEOUT);
        }

        int timeoutNumber;
        try {
            timeoutNumber = Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            timeoutNumber = Constants.DEFAULT_TIMEOUT;
        }

        return timeoutNumber;
    }
}
