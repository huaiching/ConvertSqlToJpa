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
     * @param fields 欄位名稱、型別等資料
     * @param primaryKeys 主鍵集合
     * @param primaryKeyExists 主鍵是否存在
     * @throws IOException 讀取或寫入檔案時的錯誤
     */
    public static void generateController(String entityName, List<String[]> fields, Set<String> primaryKeys, Boolean primaryKeyExists) throws IOException {
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
        controllerWriter.write("@Tag(name = \"" + entityName + " Controller\")\n");
        controllerWriter.write("public class " + entityName + "Controller {\n");
        // Logger 定義
        controllerWriter.write("    private Logger logger = LoggerFactory.getLogger(this.getClass());\n\n");

        // 設定 @Autowired
        controllerWriter.write("    @Autowired\n");
        controllerWriter.write("    private " + entityName + "Service " + toCamelCase(entityName, false) + "Service;\n");
        controllerWriter.write("\n");

        // 設定 主鍵
        String primaryKeyType = primaryKeys.size() > 1 ? entityName + "." + entityName + "Key" :
                fields.stream()
                        .filter(f -> primaryKeys.contains(f[2]))
                        .findFirst()
                        .map(f -> f[1])
                        .orElse("Integer");

        if (primaryKeyExists) {
            // 設定 save
            controllerWriter.write("    @Operation(summary = \"根據主鍵 新增或更新 " + entityName + "\",\n");
            controllerWriter.write("               description = \"根據主鍵，若有資料則更新，無資料則新增\",\n");
            controllerWriter.write("               operationId = \"save\")\n");
            controllerWriter.write("    @PostMapping(\"/save\")\n");
            controllerWriter.write("    public ResponseEntity<" + entityName + "> save(@RequestBody " + entityName + " entity) {\n");
            controllerWriter.write("        " + entityName + " savedEntity = " + entityName.toLowerCase() + "Service.save(entity);\n");
            controllerWriter.write("        return ResponseEntity.ok(savedEntity);\n");
            controllerWriter.write("    }\n\n");

            // 設定 saveAll
            controllerWriter.write("    @Operation(summary = \"根據主鍵 大量 新增或更新 " + entityName + "\",\n");
            controllerWriter.write("               description = \"根據主鍵，若有資料則更新，無資料則新增\",\n");
            controllerWriter.write("               operationId = \"saveAll\")\n");
            controllerWriter.write("    @PostMapping(\"/saveAll\")\n");
            controllerWriter.write("    public ResponseEntity<List<" + entityName + ">> saveAll(@RequestBody List<" + entityName + "> entityList) {\n");
            controllerWriter.write("        List<" + entityName + "> savedEntityList = " + entityName.toLowerCase() + "Service.saveAll(entityList);\n");
            controllerWriter.write("        return ResponseEntity.ok(savedEntityList);\n");
            controllerWriter.write("    }\n\n");

            // 設定 findById
            controllerWriter.write("    @Operation(summary = \"根據主鍵 查詢 " + entityName + "\",\n");
            controllerWriter.write("               description = \"根據主鍵查詢 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"findById\")\n");
            if (primaryKeys.size() == 1) {
                controllerWriter.write("    @GetMapping(\"/{id}\")\n");
                controllerWriter.write("    public ResponseEntity<" + entityName + "> findById(@Parameter(description = \"主鍵\") @PathVariable(\"id\") " + primaryKeyType + " id) {\n");
            } else {
                controllerWriter.write("    @PostMapping(\"/getByIds\")\n");
                controllerWriter.write("    public ResponseEntity<" + entityName + "> getByIds(@RequestBody " + primaryKeyType + " id) {\n");
            }
            controllerWriter.write("        " + entityName + " entity = " + entityName.toLowerCase() + "Service.findById(id);\n");
            controllerWriter.write("        if (entity == null) {\n");
            controllerWriter.write("            return ResponseEntity.ok(null); // 回傳 HTTP 200 OK 且 資料為 null\n");
            controllerWriter.write("        }\n");
            controllerWriter.write("        return ResponseEntity.ok(entity);  // 回傳 HTTP 200 OK 和資料\n");
            controllerWriter.write("    }\n\n");

            // 設定 deleteById
            controllerWriter.write("    @Operation(summary = \"根據主鍵 刪除 " + entityName + " 資料\",\n");
            controllerWriter.write("               description = \"根據主鍵刪除 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"deleteById\")\n");
            if (primaryKeys.size() == 1) {
                controllerWriter.write("    @DeleteMapping(\"/{id}\")\n");
                controllerWriter.write("    public ResponseEntity<Void> deleteById(@Parameter(description = \"主鍵\") @PathVariable(\"id\") " + primaryKeyType + " id) {\n");
            } else {
                controllerWriter.write("    @PostMapping(\"/delete\")\n");
                controllerWriter.write("    public ResponseEntity<Void> delete(@RequestBody " + primaryKeyType + " id) {\n");
            }
            controllerWriter.write("        " + entityName.toLowerCase() + "Service.deleteById(id);\n");
            controllerWriter.write("        return ResponseEntity.ok().build();\n");
            controllerWriter.write("    }\n");
        } else {
            // 設定 insert
            controllerWriter.write("    @Operation(summary = \"單筆新增 " + entityName + "\",\n");
            controllerWriter.write("               description = \"單筆新增 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"insert\")\n");
            controllerWriter.write("    @PostMapping(\"/insert\")\n");
            controllerWriter.write("    public ResponseEntity<" + entityName + "> insert(@RequestBody " + entityName + " entity) {\n");
            controllerWriter.write("        " + entityName + " savedEntity = " + entityName.toLowerCase() + "Service.insert(entity);\n");
            controllerWriter.write("        return ResponseEntity.ok(savedEntity);\n");
            controllerWriter.write("    }\n\n");

            // 設定 insertAll
            controllerWriter.write("    @Operation(summary = \"多筆新增 " + entityName + "\",\n");
            controllerWriter.write("               description = \"多筆新增 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"insertAll\")\n");
            controllerWriter.write("    @PostMapping(\"/insertAll\")\n");
            controllerWriter.write("    public ResponseEntity<List<" + entityName + ">> insertAll(@RequestBody List<" + entityName + "> entityList) {\n");
            controllerWriter.write("        List<" + entityName + "> savedEntityList = " + entityName.toLowerCase() + "Service.insertAll(entityList);\n");
            controllerWriter.write("        return ResponseEntity.ok(savedEntityList);\n");
            controllerWriter.write("    }\n\n");

            // 設定 update
            controllerWriter.write("    @Operation(summary = \"單筆更新 " + entityName + "\",\n");
            controllerWriter.write("               description = \"單筆新增 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"update\")\n");
            controllerWriter.write("    @PostMapping(\"/update\")\n");
            controllerWriter.write("    public ResponseEntity<Void> update(@RequestBody " + entityName + "." + entityName + "Update entityUpdate) {\n");
            controllerWriter.write("        " + entityName.toLowerCase() + "Service.update(entityUpdate.get" + capitalize(entityName) + "Ori(), entityUpdate.get" + capitalize(entityName) + "New());\n");
            controllerWriter.write("        return ResponseEntity.ok().build();\n");
            controllerWriter.write("    }\n\n");

            // 設定 deleteByEntity
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
