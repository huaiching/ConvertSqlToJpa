import java.util.List;

public interface BenfService {
    /**
     * 根據主鍵 新增或更新 benf <br/>
     * 若有資料則更新，無資料則新增
     * @param entity 要新增或更新的 benf
     * @return 儲存後的實體物件
     */
    Benf insert(Benf entity);

    /**
     * 根據主鍵 大量 新增或更新 benf <br/>
     * 若有資料則更新，無資料則新增
     * @param entityList 要新增或更新的 benf 清單
     * @return 儲存後的實體物件清單
     */
    List<Benf> saveAll(List<Benf> entityList);

    /**
     * 單筆更新 benf <br/>
     * @param entityOri 變更前的 benf
     * @param entityNew 變更後的 benf
     */
    void update(Benf entityOri, Benf entityNew);

    /**
     * 根據主鍵 查詢 benf
     * @param id 主鍵值
     * @return 查詢到的實體物件，若無則返回 null
     */
    Benf findById(String id);

    /**
     * 根據主鍵 刪除 benf
     * @param id 主鍵值
     */
    void deleteById(String id);
}
