import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.IdClass;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_info")
@IdClass(UserInfo.Key.class)
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "保單號碼")
    @Column(name = "policy_no")
    private String policyNo;

    @Schema(description = "地址")
    @Column(name = "address")
    private String address;

    @Schema(description = "訊息")
    @Column(name = "msg")
    private String msg;

    @Schema(description = "保障號碼")
    @Column(name = "coverage_no")
    private short coverageNo;

    @Schema(description = "用戶年齡")
    @Column(name = "user_age")
    private int userAge;

    @Id
    @Schema(description = "測試號碼")
    @Column(name = "test_no")
    private long testNo;

    @Schema(description = "保額")
    @Column(name = "face_amt")
    private Double faceAmt;


    public UserInfo() {
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public short getCoverageNo() {
        return coverageNo;
    }

    public void setCoverageNo(short coverageNo) {
        this.coverageNo = coverageNo;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public long getTestNo() {
        return testNo;
    }

    public void setTestNo(long testNo) {
        this.testNo = testNo;
    }

    public Double getFaceAmt() {
        return faceAmt;
    }

    public void setFaceAmt(Double faceAmt) {
        this.faceAmt = faceAmt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo that = (UserInfo) o;
        return Objects.equals(policyNo, that.policyNo) && Objects.equals(testNo, that.testNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyNo, testNo);
    }

    public static class Key implements Serializable {
        private static final long serialVersionUID = 1L;

        private String policyNo;
        private long testNo;

        public Key() {
        }

        public String getPolicyNo() {
            return policyNo;
        }

        public void setPolicyNo(String policyNo) {
            this.policyNo = policyNo;
        }

        public long getTestNo() {
            return testNo;
        }

        public void setTestNo(long testNo) {
            this.testNo = testNo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key that = (Key) o;
            return Objects.equals(policyNo, that.policyNo) && Objects.equals(testNo, that.testNo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(policyNo, testNo);
        }
    }
}
