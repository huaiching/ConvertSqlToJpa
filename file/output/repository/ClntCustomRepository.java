
public interface ClntCustomRepository {
    /**
     * 單筆更新 clnt <br/>
     * @param entityOri 變更前的 clnt
     * @param entityNew 變更後的 clnt
     */
    void update(Clnt entityOri, Clnt entityNew);

}
