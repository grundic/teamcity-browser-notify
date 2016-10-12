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

package com.github.grundic.browser.notificator.web;

import com.github.grundic.browser.notificator.Constants;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import org.jetbrains.annotations.NotNull;

/**
 * User: g.chernyshev
 * Date: 09/10/16
 * Time: 17:19
 */
public class ClientEventListenerProvider extends SimplePageExtension {
    public ClientEventListenerProvider(@NotNull PagePlaces pagePlaces, @NotNull PluginDescriptor descriptor) {
        super(
                pagePlaces,
                PlaceId.ALL_PAGES_HEADER,
                Constants.PLUGIN_TYPE,
                descriptor.getPluginResourcesPath("com/github/grundic/browser/notificator/jsp/empty.jsp")
        );

        addJsFile(descriptor.getPluginResourcesPath("com/github/grundic/browser/notificator/js/notify.js"));
        addJsFile(descriptor.getPluginResourcesPath("com/github/grundic/browser/notificator/js/listener.js"));
        register();
    }
}
