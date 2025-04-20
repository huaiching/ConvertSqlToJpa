import java.util.List;

public interface BenfService {
    /**
     * 單筆新增 benf <br/>
     * @param entity 要新增的 benf
     * @return 儲存後的實體物件
     */
    Benf insert(Benf entity);

    /**
     * 多筆新增 benf <br/>
     * @param entityList 要新增的 benf 清單
     * @return 儲存後的實體物件清單
     */
    List<Benf> saveAll(List<Benf> entityList);

    /**
     * 單筆更新 benf <br/>
     * @param entityOri 變更前的 benf
     * @param entityNew 變更後的 benf
     */
    void update(Benf entityOri, Benf entityNew);

    // 無主鍵者，自行處理 查詢 方法

    /**
     * 單筆刪除 benf
     * @param entity 要刪除的 benf
     */
    void deleteByEntity(Benf.BenfKey entity);

}
