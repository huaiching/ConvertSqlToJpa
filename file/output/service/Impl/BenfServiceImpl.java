import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.*;

@Service
public class BenfServiceImpl implements BenfService {
    @Autowired
    private BenfRepository benfRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 單筆新增 benf
     * @param entity 要新增的 benf
     * @return 儲存後的實體物件
     */
    @Override
    @Transactional
    public Benf save(Benf entity) {
        return benfRepository.save(entity);
    }

    /**
     * 多筆新增 benf
     * @param entityList 要新增的 benf 清單
     * @return 儲存後的實體物件清單
     */
    @Override
    @Transactional
    public List<Benf> insertAll(List<Benf> entityList) {
        return benfRepository.saveAll(entityList);
    }

    /**
     * 單筆更新 benf <br/>
     * @param entityOri 變更前的 benf
     * @param entityNew 變更後的 benf
     */
    @Override
    @Transactional
    public void update(Benf entityOri, Benf entityNew) {
        // 建立 SQL
        String sql = "UPDATE benf " +
                     "SET policy_no = :policyNoNew " +
                     "   ,relation = :relationNew " +
                     "   ,client_id = :clientIdNew " +
                     "   ,names = :namesNew " +
                     "WHERE policy_no = :policyNoOri " +
                     "  AND relation = :relationOri " + 
                     "  AND client_id = :clientIdOri " + 
                     "  AND names = :namesOri ";
        // 填入 參數
        Map<String, Object> params = new HashMap<>();
        params.put("policyNoNew", entityNew.getPolicyNo());
        params.put("relationNew", entityNew.getRelation());
        params.put("clientIdNew", entityNew.getClientId());
        params.put("namesNew", entityNew.getNames());
        params.put("policyNoOri", entityOri.getPolicyNo());
        params.put("relationOri", entityOri.getRelation());
        params.put("clientIdOri", entityOri.getClientId());
        params.put("namesOri", entityOri.getNames());
        // 執行 方法
        namedParameterJdbcTemplate.update(sql, params);
    }

    // 無主鍵者，自行處理 查詢 方法實作

    /**
     * 單筆刪除 benf
     * @param entity 要刪除的 benf
     */
    @Override
    @Transactional
    public void deleteByEntity(Benf.BenfKey entity) {
        if (benfRepository.existsById(entity)) {
            benfRepository.deleteById(entity);
        }
    }
}
