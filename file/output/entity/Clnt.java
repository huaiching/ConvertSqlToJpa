import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import java.time.LocalDateTime;

@Entity
@Table(name = "clnt")
@Schema(description = "客戶資料檔")
public class Clnt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "客戶證號")
    @Column(name = "clinet_id")
    private String clinetId;

    @Schema(description = "客戶姓名")
    @Column(name = "names")
    private String names;

    @Schema(description = "客戶性別")
    @Column(name = "sex")
    private String sex;

    @Schema(description = "客戶年齡")
    @Column(name = "age")
    private Integer age;


    public Clnt() {
    }

    public String getClinetId() {
        return clinetId!= null ? clinetId.trim() : null;
    }

    public void setClinetId(String clinetId) {
        this.clinetId = clinetId;
    }

    public String getNames() {
        return names!= null ? names.trim() : null;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getSex() {
        return sex!= null ? sex.trim() : null;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clnt that = (Clnt) o;
        return Objects.equals(clinetId, that.clinetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clinetId);
    }

    // 主鍵 實體類
    public static class ClntKey implements Serializable {
        private static final long serialVersionUID = 1L;

        private String clinetId;

        public ClntKey() {
        }

        public String getClinetId() {
            return clinetId;
        }

        public void setClinetId(String clinetId) {
            this.clinetId = clinetId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClntKey that = (ClntKey) o;
            return Objects.equals(clinetId, that.clinetId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clinetId);
        }
    }

    // update 實體類
    public static class ClntUpdate implements Serializable {
        private static final long serialVersionUID = 1L;

        private Clnt clntOri;
        private Clnt clntNew;

        public Clnt getClntOri() {
            return clntOri;
        }

        public void setClntOri(Clnt clntOri) {
            this.clntOri = clntOri;
        }

        public Clnt getClntNew() {
            return clntNew;
        }

        public void setClntNew(Clnt clntNew) {
            this.clntNew = clntNew;
        }

    }
}
