Teamcity browser notify
=======================

Description
-----------
Browser notify is a plugin for JetBrains Teamcity server, that adds notification via web api notification
mechanism.

![demo](https://github.com/grundic/teamcity-browser-notify/blob/master/media/teamcity-browser-notification.gif?raw=true)

Installation
------------
To install plugin [download zip archive](https://github.com/grundic/teamcity-browser-notify/releases)
it and copy it to Teamcity \<data directory\>/plugins (it is $HOME/.BuildServer/plugins under Linux and C:\Users\<user_name>\.BuildServer\plugins under Windows).
For more information, take a look at [official documentation](https://confluence.jetbrains.com/display/TCD10/Installing+Additional+Plugins)

Configuration
-------------
Configuration is accessible from user preferences, in `Notification Rules` tabs. Select `Browser Notifier` sub item.
First time you should see a warning, because browser notification should be manually accepted by user for each site.

![settings denied](https://github.com/grundic/teamcity-browser-notify/blob/master/media/settings-denied.png?raw=true)

Click on `Request notification access` button and you should see a pop-up from a browser, asking for permission.

![settings requested](https://github.com/grundic/teamcity-browser-notify/blob/master/media/settings-request.png?raw=true)

After access would be granted, you could try send test notification from this page by clicking `Test notification` button.

![settings granted](https://github.com/grundic/teamcity-browser-notify/blob/master/media/settings-granted.png?raw=true)

You could set a timeout for notification to be presented on a screen.
Next you have to actually configure notification rules for this type of notifier, the same as you probably done for other
notifiers. You can read more in [official documentation](https://confluence.jetbrains.com/display/TCD10/Subscribing+to+Notifications).

License
-------
[MIT](https://github.com/grundic/teamcity-browser-notify/blob/master/LICENSE)

