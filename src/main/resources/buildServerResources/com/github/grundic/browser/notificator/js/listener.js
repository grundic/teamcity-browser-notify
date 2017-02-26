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

'use strict';

(function () {
    var eventListener = {
        init: function () {
            var socket = atmosphere;
            var transport = 'websocket';

            var request = {
                url: base_uri + '/browserNotifier/notify.html',
                contentType: 'application/json',
                trackMessageLength: true,
                shared: true,
                transport: transport,
                fallbackTransport: 'long-polling'
            };

            this.initRequest(request);

            socket.subscribe(request);
        },

        initRequest: function (request) {
            request.onTransportFailure = function (errorMsg) {
                var notifier = window.Notify.default;
                var notification = new notifier("Transport Failure", errorMsg);
                notification.show();
            };

            request.onMessage = function (response) {
                var responseObject = JSON.parse(response.responseBody);
                var notifier = window.Notify.default;

                var extra = {
                    notifyClick: function (){
                        window.location.href = base_uri + responseObject.url;
                    }
                };

                responseObject = $j.extend(responseObject, extra);

                responseObject.icon = base_uri + "/plugins/teamcity-browser-notify/com/github/grundic/browser/notificator/img/" + responseObject.icon;
                var notification = new notifier(
                    responseObject.title,
                    responseObject
                );
                notification.show();
            };
        }
    };

    eventListener.init();
})();