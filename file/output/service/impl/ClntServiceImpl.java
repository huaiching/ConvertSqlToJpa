import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ClntServiceImpl implements ClntService {
    @Autowired
    private ClntRepository clntRepository;

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
