package com.koistorynew;

public class UserSessionManager {
    private static UserSessionManager instance;
    private String fbUid;
    private String email;
    private String displayName;
    private String profilePictureUrl;

    private UserSessionManager() {
        // Private constructor để ngăn khởi tạo trực tiếp
    }

    public static synchronized UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    // Setters
    public void setUserData(String fbUid, String email, String displayName, String profilePictureUrl) {
        this.fbUid = fbUid;
        this.email = email;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getFbUid() {
        return fbUid;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    // Clear user data when logging out
    public void clearUserData() {
        fbUid = null;
        email = null;
        displayName = null;
        profilePictureUrl = null;
    }
}