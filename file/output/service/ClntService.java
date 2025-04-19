import java.util.List;

public interface ClntService {
    /**
     * 單筆新增 clnt <br/>
     * @param entity 要新增的 clnt
     * @return 儲存後的實體物件
     */
    Clnt insert(Clnt entity);

    /**
     * 多筆新增 clnt <br/>
     * @param entityList 要新增的 clnt 清單
     * @return 儲存後的實體物件清單
     */
    List<Clnt> insertAll(List<Clnt> entityList);

    /**
     * 單筆刪除 clnt
     * @param entity 要刪除的 clnt
     */
    void deleteByEntity(Clnt entity);

}
