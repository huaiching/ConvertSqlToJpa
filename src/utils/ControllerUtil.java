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
 * 產生 controller 的相關方法
 */
public class ControllerUtil {
    /**
     * 建立 controller 類別，自動根據 單主鍵 或 多組件 封裝 save, findById, delete
     * @param entityName 實體名稱 (如：User)
     * @param entityScheamName 實體的中文註解（可選）
     * @param fields 欄位名稱、型別等資料
     * @param primaryKeys 主鍵集合
     * @param primaryKeyExists 主鍵是否存在
     * @throws IOException 讀取或寫入檔案時的錯誤
     */
    public static void generateController(String entityName, String entityScheamName, List<String[]> fields, Set<String> primaryKeys, Boolean primaryKeyExists) throws IOException {
        File controllerFile = new File("file/output/controller/" + entityName + "Controller.java");
        BufferedWriter controllerWriter = new BufferedWriter(new FileWriter(controllerFile));

        controllerWriter.write("import org.slf4j.Logger;\n");
        controllerWriter.write("import org.slf4j.LoggerFactory;\n");
        controllerWriter.write("import org.springframework.beans.factory.annotation.Autowired;\n");
        controllerWriter.write("import org.springframework.http.ResponseEntity;\n");
        controllerWriter.write("import org.springframework.web.bind.annotation.*;\n");
        controllerWriter.write("import io.swagger.v3.oas.annotations.Operation;\n");
        controllerWriter.write("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        controllerWriter.write("import io.swagger.v3.oas.annotations.Parameter;\n");
        controllerWriter.write("import java.util.List;\n");
        controllerWriter.write("\n");

        controllerWriter.write("@RestController\n");
        controllerWriter.write("@RequestMapping(\"/api/" + toCamelCase(entityName, true) + "\")\n");

        if (entityScheamName.length() > 0) {
            controllerWriter.write("@Tag(name = \"" + entityName + " Controller\", description = \"" + entityScheamName + " API 接口\")\n");
        } else {
            controllerWriter.write("@Tag(name = \"" + entityName + " Controller\")\n");
        }
        controllerWriter.write("public class " + entityName + "Controller {\n");
        // Logger 定義
        controllerWriter.write("    private Logger logger = LoggerFactory.getLogger(this.getClass());\n\n");

        // 設定 @Autowired
        controllerWriter.write("    @Autowired\n");
        controllerWriter.write("    private " + entityName + "Service " + toCamelCase(entityName, false) + "Service;\n");
        controllerWriter.write("    @Autowired\n");
        controllerWriter.write("    private " + entityName + "Repository " + toCamelCase(entityName, false) + "Repository;\n");
        controllerWriter.write("\n");

        // 設定 主鍵
        String primaryKeyType =entityName + "." + entityName + "Key";

        // save 方法
        if (primaryKeyExists) {
            controllerWriter.write("    @Operation(summary = \"根據主鍵 新增或更新 " + entityName + "\",\n");
            controllerWriter.write("               description = \"根據主鍵，若有資料則更新，無資料則新增\",\n");
        } else {
            controllerWriter.write("    @Operation(summary = \"單筆新增 " + entityName + "\",\n");
            controllerWriter.write("               description = \"單筆新增 " + entityName + " 資料\",\n");
        }
        controllerWriter.write("               operationId = \"save\")\n");
        controllerWriter.write("    @PostMapping(\"/save\")\n");
        controllerWriter.write("    public ResponseEntity<" + entityName + "> save(@RequestBody " + entityName + " entity) {\n");
        controllerWriter.write("        " + entityName + " savedEntity = " + entityName.toLowerCase() + "Service.save(entity);\n");
        controllerWriter.write("        return ResponseEntity.ok(savedEntity);\n");
        controllerWriter.write("    }\n\n");

        // saveAll 方法
        if (primaryKeyExists) {
            controllerWriter.write("    @Operation(summary = \"根據主鍵 大量 新增或更新 " + entityName + "\",\n");
            controllerWriter.write("               description = \"根據主鍵，若有資料則更新，無資料則新增\",\n");
        } else {
            controllerWriter.write("    @Operation(summary = \"多筆新增 " + entityName + "\",\n");
            controllerWriter.write("               description = \"多筆新增 " + entityName + " 資料\",\n");
        }
        controllerWriter.write("               operationId = \"saveAll\")\n");
        controllerWriter.write("    @PostMapping(\"/saveAll\")\n");
        controllerWriter.write("    public ResponseEntity<List<" + entityName + ">> saveAll(@RequestBody List<" + entityName + "> entityList) {\n");
        controllerWriter.write("        List<" + entityName + "> savedEntityList = " + entityName.toLowerCase() + "Service.saveAll(entityList);\n");
        controllerWriter.write("        return ResponseEntity.ok(savedEntityList);\n");
        controllerWriter.write("    }\n\n");

        // update 方法
        controllerWriter.write("    @Operation(summary = \"單筆更新 " + entityName + "\",\n");
        controllerWriter.write("               description = \"單筆新增 " + entityName + " 資料\",\n");
        controllerWriter.write("               operationId = \"update\")\n");
        controllerWriter.write("    @PostMapping(\"/update\")\n");
        controllerWriter.write("    public ResponseEntity<Void> update(@RequestBody " + entityName + "." + entityName + "Update entityUpdate) {\n");
        controllerWriter.write("        " + entityName.toLowerCase() + "Repository.update(entityUpdate.get" + capitalize(entityName) + "Ori(), entityUpdate.get" + capitalize(entityName) + "New());\n");
        controllerWriter.write("        return ResponseEntity.ok().build();\n");
        controllerWriter.write("    }\n\n");


        // findById 方法
        if (primaryKeyExists) {
            controllerWriter.write("    @Operation(summary = \"根據主鍵 查詢 " + entityName + "\",\n");
            controllerWriter.write("               description = \"根據主鍵查詢 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"findById\")\n");

            controllerWriter.write("    @PostMapping(\"/findById\")\n");
            controllerWriter.write("    public ResponseEntity<" + entityName + "> findById(@RequestBody " + primaryKeyType + " id) {\n");
            if (primaryKeys.size() == 1) {
                String key = primaryKeys.stream().findFirst().orElse("");
                controllerWriter.write("        " + entityName + " entity = " + entityName.toLowerCase() + "Service.findById(id.get" + toCamelCase(key, true) + "());\n");
            } else {
                controllerWriter.write("        " + entityName + " entity = " + entityName.toLowerCase() + "Service.findById(id);\n");
            }
            controllerWriter.write("        if (entity == null) {\n");
            controllerWriter.write("            return ResponseEntity.ok(null); // 回傳 HTTP 200 OK 且 資料為 null\n");
            controllerWriter.write("        }\n");
            controllerWriter.write("        return ResponseEntity.ok(entity);  // 回傳 HTTP 200 OK 和資料\n");
            controllerWriter.write("    }\n\n");
        } else {
            controllerWriter.write("    // 無主鍵者，自行處理 查詢 方法\n\n");
        }

        // delete 方法
        if (primaryKeyExists) {
            controllerWriter.write("    @Operation(summary = \"根據主鍵 刪除 " + entityName + " 資料\",\n");
            controllerWriter.write("               description = \"根據主鍵刪除 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"deleteById\")\n");

            controllerWriter.write("    @PostMapping(\"/delete\")\n");
            controllerWriter.write("    public ResponseEntity<Void> delete(@RequestBody " + primaryKeyType + " id) {\n");
            if (primaryKeys.size() == 1) {
                String key = primaryKeys.stream().findFirst().orElse("");
                controllerWriter.write("        " + entityName.toLowerCase() + "Service.deleteById(id.get" + toCamelCase(key, true) + "());\n");
            } else {
                controllerWriter.write("        " + entityName.toLowerCase() + "Service.deleteById(id);\n");
            }
            controllerWriter.write("        return ResponseEntity.ok().build();\n");
            controllerWriter.write("    }\n");
        } else {
            controllerWriter.write("    @Operation(summary = \"單筆刪除 " + entityName + "\",\n");
            controllerWriter.write("               description = \"單筆刪除 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"deleteByEntity\")\n");
            controllerWriter.write("    @PostMapping(\"/deleteByEntity\")\n");
            controllerWriter.write("    public ResponseEntity<Void> deleteByEntity(@RequestBody " + primaryKeyType + " entity) {\n");
            controllerWriter.write("        " + entityName.toLowerCase() + "Service.deleteByEntity(entity);\n");
            controllerWriter.write("        return ResponseEntity.ok().build();\n");
            controllerWriter.write("    }\n\n");
        }

        controllerWriter.write("}\n");
        controllerWriter.close();
        System.out.println("生成 Controller 檔案，位於 " + "file/output/controller/" + entityName + "Controller.java");
    }
}
