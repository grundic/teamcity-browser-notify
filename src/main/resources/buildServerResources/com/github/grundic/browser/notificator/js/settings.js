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

var BrowserNotifier = {
    notify: window.Notify.default,

    basePath: "/plugins/teamcity-browser-notify/com/github/grundic/browser/notificator",

    onPermissionGranted: function(){
        $j("#notifications-info>img").attr('src', BrowserNotifier.basePath + "/img/permission_granted.png");
        $j("#notifications-info>span").html('Browser notifications are supported and enabled.');

        $j("#notification-request").hide();
        $j("#notification-test").fadeIn("slow");
    },

    onPermissionDenied: function(){
        $j("#notifications-info>img").attr('src', BrowserNotifier.basePath + "/img/permission_denied.png");
        $j("#notifications-info>span").html('Access to browser notifications is denied. Please, accept request from browser in order to get notified.');

        $j("#notification-test").hide();
        $j("#notification-request").show();
    },

    onNotSupported: function(){
        $j("#notifications-info>img").attr('src', BrowserNotifier.basePath + "/img/permission_denied.png");
        $j("#notifications-info>span").html('Unfortunately, browser notification is not supported on your browser.');
    },

    showPermissionsInfo: function(){
        if (this.notify.needsPermission) {
            if (this.notify.isSupported()) {
                this.onPermissionDenied();
            } else {
                this.onNotSupported();
            }
        } else  {
            this.onPermissionGranted();
        }
    },

    showTestNotification: function () {
        var testNotification = new this.notify('Test notification', {
            body: $j('#notification-text').val(),
            icon: window.location.protocol + "//" + window.location.host + BrowserNotifier.basePath + "/img/teamcity_logo.png",
            timeout: 5
        });

        testNotification.show();
    },

    requestNotificationAccess: function(){
        this.notify.requestPermission(this.onPermissionGranted, this.onPermissionDenied);
    }
};