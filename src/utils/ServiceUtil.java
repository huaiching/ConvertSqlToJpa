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
 * 產生 sercvice, serviceImpl 的相關方法
 */
public class ServiceUtil {
    /**
     * 建立 service 介面，定義 CRUD 操作的抽象方法
     * @param entityName 實體名稱 (如：User)
     * @param fields 欄位名稱、型別等資料
     * @param primaryKeys 主鍵集合
     * @param primaryKeyExists 主鍵是否存在
     * @throws IOException 讀取或寫入檔案時的錯誤
     */
    public static void generateServiceInterface(String entityName, List<String[]> fields, Set<String> primaryKeys, Boolean primaryKeyExists) throws IOException {
        File serviceFile = new File("file/output/service/" + entityName + "Service.java");
        BufferedWriter serviceWriter = new BufferedWriter(new FileWriter(serviceFile));

        // 檢查主鍵的型態
        String primaryKeyType = primaryKeys.size() > 1 ? entityName + "." + entityName + "Key" : fields.stream()
                .filter(f -> primaryKeys.contains(f[2]))
                .findFirst()
                .map(f -> f[1])
                .orElse("Integer");

        serviceWriter.write("import java.util.List;\n");
        serviceWriter.write("\n");
        serviceWriter.write("public interface " + entityName + "Service {\n");

        // save 方法
        serviceWriter.write("    /**\n");
        if (primaryKeyExists) {
            serviceWriter.write("     * 根據主鍵 新增或更新 " + entityName.toLowerCase() + " <br/>\n");
            serviceWriter.write("     * 若有資料則更新，無資料則新增\n");
            serviceWriter.write("     * @param entity 要新增或更新的 " + entityName.toLowerCase() + "\n");
        } else {
            serviceWriter.write("     * 單筆新增 " + entityName.toLowerCase() + " <br/>\n");
            serviceWriter.write("     * @param entity 要新增的 " + entityName.toLowerCase() + "\n");
        }
        serviceWriter.write("     * @return 儲存後的實體物件\n");
        serviceWriter.write("     */\n");
        serviceWriter.write("    " + entityName + " save(" + entityName + " entity);\n\n");

        // saveAll 方法
        serviceWriter.write("    /**\n");
        if (primaryKeyExists) {
            serviceWriter.write("     * 根據主鍵 大量 新增或更新 " + entityName.toLowerCase() + " <br/>\n");
            serviceWriter.write("     * 若有資料則更新，無資料則新增\n");
            serviceWriter.write("     * @param entityList 要新增或更新的 " + entityName.toLowerCase() + " 清單\n");
        } else {
            serviceWriter.write("     * 多筆新增 " + entityName.toLowerCase() + " <br/>\n");
            serviceWriter.write("     * @param entityList 要新增的 " + entityName.toLowerCase() + " 清單\n");
        }
        serviceWriter.write("     * @return 儲存後的實體物件清單\n");
        serviceWriter.write("     */\n");
        serviceWriter.write("    List<" + entityName + "> saveAll(List<" + entityName + "> entityList);\n\n");

        // findById 方法
        if (primaryKeyExists) {
            serviceWriter.write("    /**\n");
            serviceWriter.write("     * 根據主鍵 查詢 " + entityName.toLowerCase() + "\n");
            serviceWriter.write("     * @param id 主鍵值\n");
            serviceWriter.write("     * @return 查詢到的實體物件，若無則返回 null\n");
            serviceWriter.write("     */\n");
            serviceWriter.write("    " + entityName + " findById(" + primaryKeyType + " id);\n\n");
        } else {
            serviceWriter.write("    // 無主鍵者，自行處理 查詢 方法\n\n");
        }

        // delete 方法
        if (primaryKeyExists) {
            serviceWriter.write("    /**\n");
            serviceWriter.write("     * 根據主鍵 刪除 " + entityName.toLowerCase() + "\n");
            serviceWriter.write("     * @param id 主鍵值\n");
            serviceWriter.write("     */\n");
            serviceWriter.write("    void deleteById(" + primaryKeyType + " id);\n");
        } else {
            serviceWriter.write("    /**\n");
            serviceWriter.write("     * 單筆刪除 " + entityName.toLowerCase() + "\n");
            serviceWriter.write("     * @param entity 要刪除的 " + entityName.toLowerCase() + "\n");
            serviceWriter.write("     */\n");
            serviceWriter.write("    void deleteByEntity(" + primaryKeyType + " entity);\n\n");
        }

        serviceWriter.write("}\n");
        serviceWriter.close();
        System.out.println("生成 Service Interface 檔案，位於 " + "file/output/service/" + entityName + "Service.java");
    }

    /**
     * 建立 service 實作類別，實現 service 介面定義的 CRUD 操作
     * @param entityName 實體名稱 (如：User)
     * @param fields 欄位名稱、型別等資料
     * @param primaryKeys 主鍵集合
     * @param primaryKeyExists 主鍵是否存在
     * @throws IOException 讀取或寫入檔案時的錯誤
     */
    public static void generateServiceImpl(String entityName, List<String[]> fields, Set<String> primaryKeys, Boolean primaryKeyExists) throws IOException {
        File implFile = new File("file/output/service/impl/" + entityName + "ServiceImpl.java");
        BufferedWriter implWriter = new BufferedWriter(new FileWriter(implFile));

        // 檢查主鍵的型態
        String primaryKeyType = primaryKeys.size() > 1 ? entityName + "." + entityName + "Key" : fields.stream()
                .filter(f -> primaryKeys.contains(f[2]))
                .findFirst()
                .map(f -> f[1])
                .orElse("Integer");

        implWriter.write("import org.springframework.stereotype.Service;\n");
        implWriter.write("import org.springframework.beans.factory.annotation.Autowired;\n");
        implWriter.write("import org.springframework.transaction.annotation.Transactional;\n");
        implWriter.write("import java.util.*;\n");
        implWriter.write("\n");
        implWriter.write("@Service\n");
        implWriter.write("public class " + entityName + "ServiceImpl implements " + entityName + "Service {\n");

        // Repository 注入
        implWriter.write("    @Autowired\n");
        implWriter.write("    private " + entityName + "Repository " + toCamelCase(entityName, false) + "Repository;\n\n");

        // save 方法實作
        implWriter.write("    /**\n");
        if (primaryKeyExists) {
            implWriter.write("     * 根據主鍵 新增或更新 " + entityName.toLowerCase() + " <br/>\n");
            implWriter.write("     * 若有資料則更新，無資料則新增\n");
            implWriter.write("     * @param entity 要新增或更新的 " + entityName.toLowerCase() +"\n");
        } else {
            implWriter.write("     * 單筆新增 " + entityName.toLowerCase() + "\n");
            implWriter.write("     * @param entity 要新增的 " + entityName.toLowerCase() + "\n");
        }
        implWriter.write("     * @return 儲存後的實體物件\n");
        implWriter.write("     */\n");
        implWriter.write("    @Override\n");
        implWriter.write("    @Transactional\n");
        implWriter.write("    public " + entityName + " save(" + entityName + " entity) {\n");
        implWriter.write("        return " + entityName.toLowerCase() + "Repository.save(entity);\n");
        implWriter.write("    }\n\n");

        // saveAll 方法實作
        implWriter.write("    /**\n");
        if (primaryKeyExists) {
            implWriter.write("     * 根據主鍵 大量 新增或更新 " + entityName.toLowerCase() + " <br/>\n");
            implWriter.write("     * 若有資料則更新，無資料則新增\n");
            implWriter.write("     * @param entityList 要新增或更新的 " + entityName.toLowerCase() + " 清單\n");
        } else {
            implWriter.write("     * 多筆新增 " + entityName.toLowerCase() + "\n");
            implWriter.write("     * @param entityList 要新增的 " + entityName.toLowerCase() + " 清單\n");
        }
        implWriter.write("     * @return 儲存後的實體物件清單\n");
        implWriter.write("     */\n");
        implWriter.write("    @Override\n");
        implWriter.write("    @Transactional\n");
        implWriter.write("    public List<" + entityName + "> saveAll(List<" + entityName + "> entityList) {\n");
        implWriter.write("        return " + entityName.toLowerCase() + "Repository.saveAll(entityList);\n");
        implWriter.write("    }\n\n");

        // findById 方法實作
        if (primaryKeyExists) {
            implWriter.write("    /**\n");
            implWriter.write("     * 根據主鍵 查詢 " + entityName.toLowerCase() + "\n");
            implWriter.write("     * @param id 主鍵值\n");
            implWriter.write("     * @return 查詢到的實體物件，若無則返回 null\n");
            implWriter.write("     */\n");
            implWriter.write("    @Override\n");
            implWriter.write("    @Transactional(readOnly = true)\n");
            implWriter.write("    public " + entityName + " findById(" + primaryKeyType + " id) {\n");
            implWriter.write("        return " + entityName.toLowerCase() + "Repository.findById(id).orElse(null);\n");
            implWriter.write("    }\n\n");
        } else {
            implWriter.write("    // 無主鍵者，自行處理 查詢 方法實作\n\n");
        }

        // delete 方法實作
        if (primaryKeyExists) {
            implWriter.write("    /**\n");
            implWriter.write("     * 根據主鍵 刪除 " + entityName.toLowerCase() + "\n");
            implWriter.write("     * @param id 主鍵值\n");
            implWriter.write("     */\n");
            implWriter.write("    @Override\n");
            implWriter.write("    @Transactional\n");
            implWriter.write("    public void deleteById(" + primaryKeyType + " id) {\n");
            implWriter.write("        if (" + entityName.toLowerCase() + "Repository.existsById(id)) {\n");
            implWriter.write("            " + entityName.toLowerCase() + "Repository.deleteById(id);\n");
            implWriter.write("        }\n");
            implWriter.write("    }\n");
        } else {
            implWriter.write("    /**\n");
            implWriter.write("     * 單筆刪除 " + entityName.toLowerCase() + "\n");
            implWriter.write("     * @param entity 要刪除的 " + entityName.toLowerCase() + "\n");
            implWriter.write("     */\n");
            implWriter.write("    @Override\n");
            implWriter.write("    @Transactional\n");
            implWriter.write("    public void deleteByEntity(" + primaryKeyType + " entity) {\n");
            implWriter.write("        if (" + entityName.toLowerCase() + "Repository.existsById(entity)) {\n");
            implWriter.write("            " + entityName.toLowerCase() + "Repository.deleteById(entity);\n");
            implWriter.write("        }\n");
            implWriter.write("    }\n");
        }

        implWriter.write("}\n");
        implWriter.close();
        System.out.println("生成 Service Impl 檔案，位於 " + "file/output/service/Impl/" + entityName + "ServiceImpl.java");
    }

}
