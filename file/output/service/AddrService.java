import java.util.List;

public interface AddrService {
    /**
     * 根據主鍵 新增或更新 addr <br/>
     * 若有資料則更新，無資料則新增
     * @param entity 要新增或更新的 addr
     * @return 儲存後的實體物件
     */
    Addr save(Addr entity);

    /**
     * 根據主鍵 大量 新增或更新 addr <br/>
     * 若有資料則更新，無資料則新增
     * @param entityList 要新增或更新的 addr 清單
     * @return 儲存後的實體物件清單
     */
    List<Addr> saveAll(List<Addr> entityList);

    /**
     * 單筆更新 addr <br/>
     * @param entityOri 變更前的 addr
     * @param entityNew 變更後的 addr
     */
    void update(Addr entityOri, Addr entityNew);

    /**
     * 根據主鍵 查詢 addr
     * @param id 主鍵值
     * @return 查詢到的實體物件，若無則返回 null
     */
    Addr findById(Addr.AddrKey id);

    /**
     * 根據主鍵 刪除 addr
     * @param id 主鍵值
     */
    void deleteById(Addr.AddrKey id);
}
