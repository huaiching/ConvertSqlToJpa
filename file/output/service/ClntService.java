import java.util.List;

public interface ClntService {
    /**
     * 根據主鍵 新增或更新 clnt <br/>
     * 若有資料則更新，無資料則新增
     * @param entity 要新增或更新的 clnt
     * @return 儲存後的實體物件
     */
    Clnt save(Clnt entity);

    /**
     * 根據主鍵 大量 新增或更新 clnt <br/>
     * 若有資料則更新，無資料則新增
     * @param entityList 要新增或更新的 clnt 清單
     * @return 儲存後的實體物件清單
     */
    List<Clnt> saveAll(List<Clnt> entityList);

    /**
     * 單筆更新 clnt <br/>
     * @param entityOri 變更前的 clnt
     * @param entityNew 變更後的 clnt
     */
    void update(Clnt entityOri, Clnt entityNew);

    /**
     * 根據主鍵 查詢 clnt
     * @param id 主鍵值
     * @return 查詢到的實體物件，若無則返回 null
     */
    Clnt findById(String id);

    /**
     * 根據主鍵 刪除 clnt
     * @param id 主鍵值
     */
    void deleteById(String id);
}
