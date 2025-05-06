package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static utils.BasicUtil.capitalize;
import static utils.BasicUtil.toCamelCase;

/**
 * 產生 repository 的相關方法
 */
public class RepositoryUtil {
    /**
     * 建立 repository 類別，自動根據 entity 設定 JpaRepository
     * @param entityName 實體名稱 (如：User)
     * @param fields 欄位名稱、型別等資料
     * @param primaryKeys 主鍵集合
     * @throws IOException 讀取或寫入檔案時的錯誤
     */
    public static void generateRepository(String entityName, List<String[]> fields, Set<String> primaryKeys) throws IOException {
        // 簡單　SQL
        File repoFile = new File("file/output/repository/" + entityName + "Repository.java");
        BufferedWriter repoWriter = new BufferedWriter(new FileWriter(repoFile));

        // 檢查主鍵的型態
        String primaryKeyType = primaryKeys.size() > 1 ? entityName + "." + entityName + "Key" : fields.stream()
                .filter(f -> primaryKeys.contains(f[2]))
                .findFirst()
                .map(f -> f[1])
                .orElse("Integer");

        repoWriter.write("import org.springframework.data.jpa.repository.JpaRepository;\n");
        repoWriter.write("\n");
        repoWriter.write("public interface " + entityName + "Repository extends JpaRepository<" + entityName + ", " + primaryKeyType + ">, " + entityName + "CustomRepository{\n");
        repoWriter.write("}\n");

        repoWriter.close();
        System.out.println("生成 Repository 檔案，位於 " + "file/output/repository/" + entityName + "Repository.java");

        // 複雜 SQL
        File repoCustomFile = new File("file/output/repository/" + entityName + "CustomRepository.java");
        BufferedWriter repoCustomWriter = new BufferedWriter(new FileWriter(repoCustomFile));

        repoCustomWriter.write("\n");
        repoCustomWriter.write("public interface " + entityName + "CustomRepository {\n");

        // update 方法
        repoCustomWriter.write("    /**\n");
        repoCustomWriter.write("     * 單筆更新 " + entityName.toLowerCase() + " <br/>\n");
        repoCustomWriter.write("     * @param entityOri 變更前的 " + entityName.toLowerCase() + "\n");
        repoCustomWriter.write("     * @param entityNew 變更後的 " + entityName.toLowerCase() + "\n");
        repoCustomWriter.write("     */\n");
        repoCustomWriter.write("    void update(" + entityName + " entityOri, " + entityName + " entityNew);\n\n");

        repoCustomWriter.write("}\n");

        repoCustomWriter.close();
        System.out.println("生成 Repository 檔案，位於 " + "file/output/repository/" + entityName + "CustomRepository.java");

    }

    /**
     * 建立 repository 實作類別，實現 repository 介面定義的 CRUD 操作
     * @param entityName 實體名稱 (如：User)
     * @param fields 欄位名稱、型別等資料
     * @param primaryKeys 主鍵集合
     * @param primaryKeyExists 主鍵是否存在
     * @throws IOException 讀取或寫入檔案時的錯誤
     */
    public static void generateRepositoryImpl(String entityName, List<String[]> fields, Set<String> primaryKeys, Boolean primaryKeyExists) throws IOException {
        File implFile = new File("file/output/repository/impl/" + entityName + "CustomRepositoryImpl.java");
        BufferedWriter implWriter = new BufferedWriter(new FileWriter(implFile));

        // 檢查主鍵的型態
        String primaryKeyType = primaryKeys.size() > 1 ? entityName + "." + entityName + "Key" : fields.stream()
                .filter(f -> primaryKeys.contains(f[2]))
                .findFirst()
                .map(f -> f[1])
                .orElse("Integer");

        implWriter.write("import org.springframework.stereotype.Repository;\n");
        implWriter.write("import org.springframework.beans.factory.annotation.Autowired;\n");
        implWriter.write("import org.springframework.transaction.annotation.Transactional;\n");
        implWriter.write("import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;\n");
        implWriter.write("import java.util.*;\n");
        implWriter.write("\n");
        implWriter.write("@Repository\n");
        implWriter.write("public class " + entityName + "CustomRepositoryImpl implements " + entityName + "CustomRepository {\n");

        // Repository 注入
        implWriter.write("    @Autowired\n");
        implWriter.write("    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;\n\n");

        // update 方法
        implWriter.write("    /**\n");
        implWriter.write("     * 單筆更新 " + entityName.toLowerCase() + " <br/>\n");
        implWriter.write("     * @param entityOri 變更前的 " + entityName.toLowerCase() + "\n");
        implWriter.write("     * @param entityNew 變更後的 " + entityName.toLowerCase() + "\n");
        implWriter.write("     */\n");
        implWriter.write("    @Override\n");
        implWriter.write("    @Transactional\n");
        implWriter.write("    public void update(" + entityName + " entityOri, " + entityName + " entityNew) {\n");
        implWriter.write("        // 建立 SQL\n");
        implWriter.write("        String sql = \"UPDATE " + entityName.toLowerCase() + " \" +\n");
        for (int i = 0; i < fields.size(); i++) {
            String[] field = fields.get(i);
            if (i == 0) {
                implWriter.write("                     \"SET " + field[2] + " = :" + field[0] + "New \" +\n");
            } else {
                implWriter.write("                     \"   ," + field[2] + " = :" + field[0] + "New \" +\n");
            }
        }
        for (int i = 0; i < fields.size(); i++) {
            String[] field = fields.get(i);
            if (i == 0) {
                implWriter.write("                     \"WHERE " + field[2] + " = :" + field[0] + "Ori \" +\n");
            } else if (i == fields.size() - 1) {
                implWriter.write("                     \"  AND " + field[2] + " = :" + field[0] + "Ori \";\n");
            } else {
                implWriter.write("                     \"  AND " + field[2] + " = :" + field[0] + "Ori \" + \n");
            }
        }
        implWriter.write("        // 填入 參數\n");
        implWriter.write("        Map<String, Object> params = new HashMap<>();\n");
        for (int i = 0; i < fields.size(); i++) {
            String[] field = fields.get(i);
            implWriter.write("        params.put(\"" + field[0] + "New\", entityNew.get" + capitalize(field[0]) + "());\n");
        }
        for (int i = 0; i < fields.size(); i++) {
            String[] field = fields.get(i);
            implWriter.write("        params.put(\"" + field[0] + "Ori\", entityOri.get" + capitalize(field[0]) + "());\n");
        }
        implWriter.write("        // 執行 方法\n");
        implWriter.write("        namedParameterJdbcTemplate.update(sql, params);\n");
        implWriter.write("    }\n\n");



        implWriter.write("}\n");
        implWriter.close();
        System.out.println("生成 Repository Impl 檔案，位於 " + "file/output/repository/impl/" + entityName + "CustomRepositoryImpl.java");
    }

}
