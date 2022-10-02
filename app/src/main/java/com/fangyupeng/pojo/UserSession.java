package com.fangyupeng.pojo;

/**
 * @Auther: fyp
 * @Date: 2022/5/23
 * @Description:
 * @Package: com.fangyupeng.UserSession
 * @Version: 1.0
 */
public class UserSession {

    private String userId;
    private String username;
    private String userToken;

    public UserSession() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", userToken='" + userToken + '\'' +
                '}';
    }
}
