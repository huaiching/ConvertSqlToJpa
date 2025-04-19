import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "clnt")
@Schema(description = "客戶資料檔")
public class Clnt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "客戶證號")
    @Column(name = "client_id")
    private String clientId;

    @Schema(description = "姓名")
    @Column(name = "names")
    private String names;

    @Schema(description = "出生日期")
    @Column(name = "birth_date")
    private String birthDate;

    @Schema(description = "性別")
    @Column(name = "sex")
    private String sex;


    public Clnt() {
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

    public String getBirthDate() {
        return birthDate!= null ? birthDate.trim() : null;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        return sex!= null ? sex.trim() : null;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clnt that = (Clnt) o;
        return Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }

}
