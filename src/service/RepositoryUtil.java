package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

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
        repoWriter.write("public interface " + entityName + "Repository extends JpaRepository<" + entityName + ", " + primaryKeyType + "> {\n");
        repoWriter.write("}\n");

        repoWriter.close();
        System.out.println("生成 Repository 檔案，位於 " + "file/output/repository/" + entityName + "Repository.java");
    }
}
