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

package com.github.grundic.browser.notificator;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.serverSide.problems.BuildProblemInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.VcsRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


/**
 * User: g.chernyshev
 * Date: 09/10/16
 * Time: 00:05
 */
public class BrowserNotifier implements Notificator {
    public static final String PLUGIN_TYPE = "browserNotifier";
    public static final String PLUGIN_NAME = "Browser Notifier";

    private static final String NOTIFICATION_TIMEOUT = "NOTIFICATION_TIMEOUT";
    private static final int DEFAULT_TIMEOUT = 10;

    private final BrowserNotificationHandler notificationHandler;

    public BrowserNotifier(
            @NotNull NotificatorRegistry notificatorRegistry,
            @NotNull BrowserNotificationHandler notificationHandler
    ) {
        ArrayList<UserPropertyInfo> userProps = new ArrayList<>();
        userProps.add(new UserPropertyInfo(NOTIFICATION_TIMEOUT, "Notification timeout."));

        this.notificationHandler = notificationHandler;
        notificatorRegistry.register(this, userProps);
    }

    public static int getTimeout(SUser user) {
        String timeout = user.getPropertyValue(new NotificatorPropertyKey(PLUGIN_TYPE, NOTIFICATION_TIMEOUT));
        if (null == timeout || timeout.isEmpty()) {
            timeout = Integer.toString(DEFAULT_TIMEOUT);
        }

        int timeoutNumber;
        try {
            timeoutNumber = Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            timeoutNumber = DEFAULT_TIMEOUT;
        }

        return timeoutNumber;
    }

    private MessageBean getMessage(@NotNull String status, @NotNull Build build) {
        MessageBean message = new MessageBean();
        message.title = status;
        message.body = String.format("%s [%s]", build.getFullName(), build.getBuildNumber());
        message.md5Tag();

        return message;
    }

    private MessageBean getMessage(@NotNull String status, @NotNull SBuildType buildType) {
        MessageBean message = new MessageBean();
        message.title = status;
        message.body = String.format("%s", buildType.getFullName());
        message.md5Tag();

        return message;
    }

    private MessageBean getMessage(@NotNull String status, @NotNull SProject project) {
        MessageBean message = new MessageBean();
        message.title = status;
        message.body = String.format("%s", project.getFullName());
        message.md5Tag();

        return message;
    }

    private MessageBean getMessage(@NotNull String status, @NotNull MuteInfo muteInfo) {
        MessageBean message = new MessageBean();
        message.title = status;
        SProject project = muteInfo.getProject();
        message.body = String.format("%s", null == project ? "<unknown>" : project.getFullName());
        message.md5Tag();

        return message;
    }

    @NotNull
    @Override
    public String getNotificatorType() {
        return PLUGIN_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return PLUGIN_NAME;
    }

    @Override
    public void notifyBuildStarted(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        final MessageBean message = getMessage("Build started", build);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyBuildSuccessful(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        final MessageBean message = getMessage("Build successful", build);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyBuildFailed(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        final MessageBean message = getMessage("Build failed", build);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyBuildFailedToStart(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        final MessageBean message = getMessage("Build failed to start", build);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyLabelingFailed(@NotNull Build build, @NotNull VcsRoot root, @NotNull Throwable exception, @NotNull Set<SUser> users) {
        final MessageBean message = getMessage("Labeling failed", build);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyBuildFailing(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        final MessageBean message = getMessage("Build is failing", build);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyBuildProbablyHanging(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        final MessageBean message = getMessage("Build probably hanging", build);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyResponsibleChanged(@NotNull SBuildType buildType, @NotNull Set<SUser> users) {
        final MessageBean message = getMessage("Responsibility for configuration changed", buildType);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyResponsibleAssigned(@NotNull SBuildType buildType, @NotNull Set<SUser> users) {
        final MessageBean message = getMessage("You were assigned as responsible for build", buildType);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyResponsibleChanged(
            @Nullable TestNameResponsibilityEntry oldValue,
            @NotNull TestNameResponsibilityEntry newValue,
            @NotNull SProject project,
            @NotNull Set<SUser> users
    ) {
        final MessageBean message = getMessage("Responsibility for test changed", project);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyResponsibleAssigned(
            @Nullable TestNameResponsibilityEntry oldValue,
            @NotNull TestNameResponsibilityEntry newValue,
            @NotNull SProject project,
            @NotNull Set<SUser> users
    ) {
        final MessageBean message = getMessage("You were assigned as responsible for test", project);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyResponsibleChanged(
            @NotNull Collection<TestName> testNames,
            @NotNull ResponsibilityEntry entry,
            @NotNull SProject project,
            @NotNull Set<SUser> users
    ) {
        final MessageBean message = getMessage(String.format("Responsibility for %d tests changed", testNames.size()), project);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyResponsibleAssigned(
            @NotNull Collection<TestName> testNames,
            @NotNull ResponsibilityEntry entry,
            @NotNull SProject project,
            @NotNull Set<SUser> users
    ) {
        final MessageBean message = getMessage(String.format("You were assigned as responsible for %d tests", testNames.size()), project);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyBuildProblemResponsibleAssigned(
            @NotNull Collection<BuildProblemInfo> buildProblems,
            @NotNull ResponsibilityEntry entry,
            @NotNull SProject project,
            @NotNull Set<SUser> users
    ) {
        final MessageBean message = getMessage(String.format("Responsibility for %d build problems is assigned", buildProblems.size()), project);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyBuildProblemResponsibleChanged(
            @NotNull Collection<BuildProblemInfo> buildProblems,
            @NotNull ResponsibilityEntry entry,
            @NotNull SProject project,
            @NotNull Set<SUser> users
    ) {
        final MessageBean message = getMessage(String.format("Responsibility for %d build problems is changed", buildProblems.size()), project);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyTestsMuted(
            @NotNull Collection<STest> tests,
            @NotNull MuteInfo muteInfo,
            @NotNull Set<SUser> users
    ) {
        final MessageBean message = getMessage(String.format("%d tests are muted", tests.size()), muteInfo);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyTestsUnmuted(
            @NotNull Collection<STest> tests,
            @NotNull MuteInfo muteInfo,
            @Nullable SUser user,
            @NotNull Set<SUser> users
    ) {
        final MessageBean message = getMessage(String.format("%d tests are unmuted", tests.size()), muteInfo);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyBuildProblemsMuted(
            @NotNull Collection<BuildProblemInfo> buildProblems,
            @NotNull MuteInfo muteInfo,
            @NotNull Set<SUser> users
    ) {
        final MessageBean message = getMessage(String.format("%d problems are muted", buildProblems.size()), muteInfo);
        notificationHandler.broadcast(message, users);
    }

    @Override
    public void notifyBuildProblemsUnmuted(
            @NotNull Collection<BuildProblemInfo> buildProblems,
            @NotNull MuteInfo muteInfo,
            @Nullable SUser user,
            @NotNull Set<SUser> users
    ) {
        final MessageBean message = getMessage(String.format("%d problems are unmuted", buildProblems.size()), muteInfo);
        notificationHandler.broadcast(message, users);
    }

}
