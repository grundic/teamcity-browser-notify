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

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.DeploymentException;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;
import java.util.Map;


public class WebsocketEndpointMapper {
    /**
     * Standard attribute to retrieve the {@link ServerContainer} instance.
     */
    private final static String SERVLET_CONTAINER_CLASS = "javax.websocket.server.ServerContainer";

    private final Logger LOG = Logger.getInstance(WebsocketEndpointMapper.class.getName());

    public WebsocketEndpointMapper(
            @NotNull final ApplicationContext applicationContext,
            @NotNull final ServletContext serverContext,
            @NotNull final ClassLoader classLoader,
            @NotNull final Map<String, Class<?>> endpoints
    ) {

        // We first do some general checks to see if the JSR-356 is supported by the server. Those are basically the
        // same checks that TeamCity itself is doing with two exceptions:
        // - We do not check if websockets were disabled with the "teamcity.ui.websocket.enabled" property (it's
        //   currently unclear how we can query those properties with the API we are currently using).
        // - We do not check (at least for now) if the Tomcat (or any other application server) version is well suited
        //   to activate the WebSocket feature.
        try {
            classLoader.loadClass(SERVLET_CONTAINER_CLASS);
        } catch (ClassNotFoundException e) {
            LOG.info("JSR-356 API not detected on classpath. WebSocket features are disabled.");
            return;
        }

        ServerContainer serverContainer = (ServerContainer) serverContext.getAttribute(SERVLET_CONTAINER_CLASS);

        for (String path : endpoints.keySet()) {
            ServerEndpointConfig serverConfig = ServerEndpointConfig.Builder
                    .create(endpoints.get(path), path)
                    .configurator(new Configurator() {
                                      @Override
                                      public <T> T getEndpointInstance(Class<T> endpointClass) {
                                          return applicationContext.getBean(endpointClass);
                                      }

                                      @Override
                                      public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse
                                              response) {
                                          if (null != HttpSession.class.getName()) {
                                              sec.getUserProperties().put(HttpSession.class.getName(), request.getHttpSession());
                                          }
                                      }


                                  }
                    ).build();

            try {
                serverContainer.addEndpoint(serverConfig);
            } catch (DeploymentException e) {
                LOG.warn("Deployment of WebSocket endpoint failed. Disabling WebSocket features.", e);

            }
        }
    }
}
