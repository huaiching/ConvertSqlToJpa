import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.*;

@Repository
public class ClntCustomRepositoryImpl implements ClntCustomRepository {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 單筆更新 clnt <br/>
     * @param entityOri 變更前的 clnt
     * @param entityNew 變更後的 clnt
     */
    @Override
    @Transactional
    public void update(Clnt entityOri, Clnt entityNew) {
        // 建立 SQL
        String sql = "UPDATE clnt " +
                     "SET client_id = :clientIdNew " +
                     "   ,names = :namesNew " +
                     "   ,sex = :sexNew " +
                     "   ,age = :ageNew " +
                     "WHERE client_id = :clientIdOri " +
                     "  AND names = :namesOri " + 
                     "  AND sex = :sexOri " + 
                     "  AND age = :ageOri ";
        // 填入 參數
        Map<String, Object> params = new HashMap<>();
        params.put("clientIdNew", entityNew.getClientId());
        params.put("namesNew", entityNew.getNames());
        params.put("sexNew", entityNew.getSex());
        params.put("ageNew", entityNew.getAge());
        params.put("clientIdOri", entityOri.getClientId());
        params.put("namesOri", entityOri.getNames());
        params.put("sexOri", entityOri.getSex());
        params.put("ageOri", entityOri.getAge());
        // 執行 方法
        namedParameterJdbcTemplate.update(sql, params);
    }

}
