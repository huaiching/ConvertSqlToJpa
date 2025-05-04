import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.IdClass;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import java.time.LocalDateTime;

@Entity
@Table(name = "addr")
@IdClass(Addr.AddrKey.class)
@Schema(description = "客戶地址檔")
public class Addr implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "客戶姓名")
    @Column(name = "clinet_id")
    private String clinetId;

    @Id
    @Schema(description = "地址指示")
    @Column(name = "addr_ind")
    private String addrInd;

    @Schema(description = "地址")
    @Column(name = "address")
    private String address;

    @Schema(description = "電話")
    @Column(name = "tel")
    private String tel;


    public Addr() {
    }

    public String getClinetId() {
        return clinetId!= null ? clinetId.trim() : null;
    }

    public void setClinetId(String clinetId) {
        this.clinetId = clinetId;
    }

    public String getAddrInd() {
        return addrInd!= null ? addrInd.trim() : null;
    }

    public void setAddrInd(String addrInd) {
        this.addrInd = addrInd;
    }

    public String getAddress() {
        return address!= null ? address.trim() : null;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel!= null ? tel.trim() : null;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Addr that = (Addr) o;
        return Objects.equals(clinetId, that.clinetId) && Objects.equals(addrInd, that.addrInd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clinetId, addrInd);
    }

    // 主鍵 實體類
    public static class AddrKey implements Serializable {
        private static final long serialVersionUID = 1L;

        private String clinetId;
        private String addrInd;

        public AddrKey() {
        }

        public String getClinetId() {
            return clinetId;
        }

        public void setClinetId(String clinetId) {
            this.clinetId = clinetId;
        }

        public String getAddrInd() {
            return addrInd;
        }

        public void setAddrInd(String addrInd) {
            this.addrInd = addrInd;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AddrKey that = (AddrKey) o;
            return Objects.equals(clinetId, that.clinetId) && Objects.equals(addrInd, that.addrInd);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clinetId, addrInd);
        }
    }

    // update 實體類
    public static class AddrUpdate implements Serializable {
        private static final long serialVersionUID = 1L;

        private Addr addrOri;
        private Addr addrNew;

        public Addr getAddrOri() {
            return addrOri;
        }

        public void setAddrOri(Addr addrOri) {
            this.addrOri = addrOri;
        }

        public Addr getAddrNew() {
            return addrNew;
        }

        public void setAddrNew(Addr addrNew) {
            this.addrNew = addrNew;
        }

    }
}
