import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "benf")
@IdClass(Benf.BenfKey.class)
@Schema(description = "受益人檔")
public class Benf implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "保單號碼")
    @Column(name = "policy_no")
    private String policyNo;

    @Id
    @Schema(description = "關係")
    @Column(name = "relation")
    private String relation;

    @Id
    @Schema(description = "客戶證號")
    @Column(name = "client_id")
    private String clientId;

    @Id
    @Schema(description = "姓名")
    @Column(name = "names")
    private String names;


    public Benf() {
    }

    public String getPolicyNo() {
        return policyNo!= null ? policyNo.trim() : null;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getRelation() {
        return relation!= null ? relation.trim() : null;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getClientId() {
        return clientId!= null ? clientId.trim() : null;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getNames() {
        return names!= null ? names.trim() : null;
    }

    public void setNames(String names) {
        this.names = names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Benf that = (Benf) o;
        return Objects.equals(policyNo, that.policyNo) && Objects.equals(relation, that.relation) && Objects.equals(clientId, that.clientId) && Objects.equals(names, that.names);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyNo, relation, clientId, names);
    }

    public static class BenfKey implements Serializable {
        private static final long serialVersionUID = 1L;

        private String policyNo;
        private String relation;
        private String clientId;
        private String names;

        public BenfKey() {
        }

        public String getPolicyNo() {
            return policyNo;
        }

        public void setPolicyNo(String policyNo) {
            this.policyNo = policyNo;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getNames() {
            return names;
        }

        public void setNames(String names) {
            this.names = names;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BenfKey that = (BenfKey) o;
            return Objects.equals(policyNo, that.policyNo) && Objects.equals(relation, that.relation) && Objects.equals(clientId, that.clientId) && Objects.equals(names, that.names);
        }

        @Override
        public int hashCode() {
            return Objects.hash(policyNo, relation, clientId, names);
        }
    }
    public static class BenfUpdate implements Serializable {
        private static final long serialVersionUID = 1L;

        private Benf benfOri;
        private Benf benfNew;

        public Benf getBenfOri() {
            return benfOri;
        }

        public void setBenfOri(Benf benfOri) {
            this.benfOri = benfOri;
        }

        public Benf getBenfNew() {
            return benfNew;
        }

        public void setBenfNew(Benf benfNew) {
            this.benfNew = benfNew;
        }

    }
}
