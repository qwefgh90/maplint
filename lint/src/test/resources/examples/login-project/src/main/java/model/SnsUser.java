package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author qwefgh90
 */
public class SnsUser {
    int id;
    String snsId;
    String snsType;
    String snsName;
    String snsProfile;
    LocalDateTime snsConnectDate;

    public LocalDateTime getSnsConnectDate() {
        return snsConnectDate;
    }

    public void setSnsConnectDate(LocalDateTime snsConnectDate) {
        this.snsConnectDate = snsConnectDate;
    }

    public SnsUser() {
    }

    public SnsUser(int id, String snsId, String snsType, String snsName, String snsProfile) {
        this.id = id;
        this.snsId = snsId;
        this.snsType = snsType;
        this.snsName = snsName;
        this.snsProfile = snsProfile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSnsId() {
        return snsId;
    }

    public void setSnsId(String snsId) {
        this.snsId = snsId;
    }

    public String getSnsType() {
        return snsType;
    }

    public void setSnsType(String snsType) {
        this.snsType = snsType;
    }

    public String getSnsName() {
        return snsName;
    }

    public void setSnsName(String snsName) {
        this.snsName = snsName;
    }

    public String getSnsProfile() {
        return snsProfile;
    }

    public void setSnsProfile(String snsProfile) {
        this.snsProfile = snsProfile;
    }


    @Override
    public String toString() {
        return "SnsUser{" +
                "id=" + id +
                ", snsId='" + snsId + '\'' +
                ", snsType='" + snsType + '\'' +
                ", snsName='" + snsName + '\'' +
                ", snsProfile='" + snsProfile + '\'' +
                '}';
    }
}
