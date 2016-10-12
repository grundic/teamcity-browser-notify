/*
 * The MIT License
 *
 * Copyright (c) 2016 Grigory Chernyshev.
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
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.IdleResourceInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * User: g.chernyshev
 * Date: 09/10/16
 * Time: 15:03
 */
public class AtmosphereController extends BaseController {
    private static final String ASYNC_SUPPORTED = "org.apache.catalina.ASYNC_SUPPORTED";
    private final AtmosphereFramework atmosphereFramework;
    private final String webPath;

    protected AtmosphereController(
            @NotNull SBuildServer server,
            @NotNull BrowserNotificationHandler browserNotificationHandler,
            @NotNull WebControllerManager webControllerManager
    ) {
        super(server);
        webPath = String.format("/%s/notify.html", Constants.PLUGIN_TYPE);
        atmosphereFramework = createAtmosphereFramework(browserNotificationHandler);
        webControllerManager.registerController(webPath, this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        request.setAttribute(ASYNC_SUPPORTED, Boolean.TRUE);
        atmosphereFramework.doCometSupport(AtmosphereRequest.wrap(request), AtmosphereResponse.wrap(response));

        return null;
    }

    private AtmosphereFramework createAtmosphereFramework(@NotNull BrowserNotificationHandler handler) {
        AtmosphereFramework atmosphereFramework = new AtmosphereFramework();

        List<AtmosphereInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new AtmosphereResourceLifecycleInterceptor());
        interceptors.add(new HeartbeatInterceptor());
        interceptors.add(new IdleResourceInterceptor());
        interceptors.add(new TrackMessageSizeInterceptor());
        interceptors.add(new BroadcastOnPostAtmosphereInterceptor());

        atmosphereFramework.getBroadcasterFactory().lookup(webPath, true);
        atmosphereFramework.addAtmosphereHandler(webPath, handler, interceptors);

        atmosphereFramework.init();
        return atmosphereFramework;
    }
}
