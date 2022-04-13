package model;

/**
 * @author qwefgh90
 */
public class User {
    int id;
    String userName;
    String password;
    String email;
    String nickName;
    String mobile;
    SnsUser snsUser;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public SnsUser getSnsUser() {
        return snsUser;
    }

    public void setSnsUser(SnsUser snsUser) {
        this.snsUser = snsUser;
    }

    public User() {
    }

    public User(String userName, String password, String email, String nickName, String mobile) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.nickName = nickName;
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", nickName='" + nickName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", snsUser=" + snsUser +
                '}';
    }
}
