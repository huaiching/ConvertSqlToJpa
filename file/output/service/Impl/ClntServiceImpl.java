import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ClntServiceImpl implements ClntService {
    @Autowired
    private ClntRepository clntRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 根據主鍵 新增或更新 clnt <br/>
     * 若有資料則更新，無資料則新增
     * @param entity 要新增或更新的 clnt
     * @return 儲存後的實體物件
     */
    @Override
    @Transactional
    public Clnt save(Clnt entity) {
        return clntRepository.save(entity);
    }

    /**
     * 根據主鍵 大量 新增或更新 clnt <br/>
     * 若有資料則更新，無資料則新增
     * @param entityList 要新增或更新的 clnt 清單
     * @return 儲存後的實體物件清單
     */
    @Override
    @Transactional
    public List<Clnt> saveAll(List<Clnt> entityList) {
        return clntRepository.saveAll(entityList);
    }

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
                     "SET clinet_id = :clinetIdNew " +
                     "   ,names = :namesNew " +
                     "   ,sex = :sexNew " +
                     "   ,age = :ageNew " +
                     "WHERE clinet_id = :clinetIdOri " +
                     "  AND names = :namesOri " + 
                     "  AND sex = :sexOri " + 
                     "  AND age = :ageOri ";
        // 填入 參數
        Map<String, Object> params = new HashMap<>();
        params.put("clinetIdNew", entityNew.getClinetId());
        params.put("namesNew", entityNew.getNames());
        params.put("sexNew", entityNew.getSex());
        params.put("ageNew", entityNew.getAge());
        params.put("clinetIdOri", entityOri.getClinetId());
        params.put("namesOri", entityOri.getNames());
        params.put("sexOri", entityOri.getSex());
        params.put("ageOri", entityOri.getAge());
        // 執行 方法
        namedParameterJdbcTemplate.update(sql, params);
    }

    /**
     * 根據主鍵 查詢 clnt
     * @param id 主鍵值
     * @return 查詢到的實體物件，若無則返回 null
     */
    @Override
    @Transactional(readOnly = true)
    public Clnt findById(String id) {
        return clntRepository.findById(id).orElse(null);
    }

    /**
     * 根據主鍵 刪除 clnt
     * @param id 主鍵值
     */
    @Override
    @Transactional
    public void deleteById(String id) {
        if (clntRepository.existsById(id)) {
            clntRepository.deleteById(id);
        }
    }
}
