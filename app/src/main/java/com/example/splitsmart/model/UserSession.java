package com.example.splitsmart.model;

/**
 * Singleton class representing the current user's session.
 * Contains a reference to the user object and their group, if they are in one.
 */
public class UserSession {
    private static User user;
    private static Group group;

    public static void login(User user, Group group) {
        UserSession.user = user;
        UserSession.group = group;
    }

    public static void logout() {
        UserSession.user = null;
        UserSession.group = null;
    }

    public static User getUser() {
        return UserSession.user;
    }

    public static Group getGroup() {
        return UserSession.group;
    }
}
