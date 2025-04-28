import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class BenfServiceImpl implements BenfService {
    @Autowired
    private BenfRepository benfRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 根據主鍵 新增或更新 benf <br/>
     * 若有資料則更新，無資料則新增
     * @param entity 要新增或更新的 benf
     * @return 儲存後的實體物件
     */
    @Override
    @Transactional
    public Benf save(Benf entity) {
        return benfRepository.save(entity);
    }

    /**
     * 根據主鍵 大量 新增或更新 benf <br/>
     * 若有資料則更新，無資料則新增
     * @param entityList 要新增或更新的 benf 清單
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

    /**
     * 根據主鍵 查詢 benf
     * @param id 主鍵值
     * @return 查詢到的實體物件，若無則返回 null
     */
    @Override
    @Transactional(readOnly = true)
    public Benf findById(String id) {
        return benfRepository.findById(id).orElse(null);
    }

    /**
     * 根據主鍵 刪除 benf
     * @param id 主鍵值
     */
    @Override
    @Transactional
    public void deleteById(String id) {
        if (benfRepository.existsById(id)) {
            benfRepository.deleteById(id);
        }
    }
}
