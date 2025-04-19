import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClntServiceImpl implements ClntService {
    @Autowired
    private ClntRepository clntRepository;

    /**
     * 單筆新增 clnt <br/>
     * @param entity 要新增的 clnt
     * @return 儲存後的實體物件
     */
    @Override
    @Transactional
    public Clnt insert(Clnt entity) {
        return clntRepository.save(entity);
    }

    /**
     * 多筆新增 clnt <br/>
     * @param entityList 要新增的 clnt 清單
     * @return 儲存後的實體物件清單
     */
    @Override
    @Transactional
    public List<Clnt> insertAll(List<Clnt> entityList) {
        return clntRepository.saveAll(entityList);
    }

    /**
     * 單筆刪除 clnt
     * @param entity 要刪除的 clnt
     */
    @Override
    @Transactional
    public void deleteByEntity(Clnt entity) {
        if (clntRepository.existsById(entity)) {
            clntRepository.deleteById(entity);
        }
    }
}
