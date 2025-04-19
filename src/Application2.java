import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Application2 {
    public static void main(String[] args) {
        try {
            File inputFile = new File("file/input.txt");
            File pkFile = new File("file/primary_keys.txt");

            // 確保 file 和 file/output 資料夾存在
            new File("file").mkdirs();
            new File("file/output").mkdirs();

            // 讀取主鍵定義
            Set<String> primaryKeys = new HashSet<>();
            if (pkFile.exists()) {
                BufferedReader pkReader = new BufferedReader(new FileReader(pkFile));
                String pkLine;
                while ((pkLine = pkReader.readLine()) != null) {
                    if (!pkLine.trim().isEmpty()) {
                        primaryKeys.add(pkLine.trim());
                    }
                }
                pkReader.close();
            }

            // 讀取 input.txt
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String entityName = "";
            String tableName = "";
            String entityScheamName = "";
            List<String[]> fields = new ArrayList<>(); // 儲存欄位資訊

            // 先解析出 entityName 和 fields
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.toUpperCase().startsWith("CREATE TABLE")) {
                    String[] parts = line.split("\\s+");
                    tableName = parts[2];
                    entityName = toCamelCase(tableName, true); // 首字母大寫
                    // 取得 中文註解
                    String schemaName = "";
                    for (int i = 0; i < parts.length; i++) {
                        if ("--".equals(parts[i])) {
                            entityScheamName = parts[i+1];
                        }
                    }
                } else if (!line.isEmpty() && !line.equals(")")) {
                    String[] parts = line.trim().replace(",", "").split("\\s+");
                    String fieldName = toCamelCase(parts[0], false);
                    String sqlType = parts[1];
                    String javaType = mapSqlType(sqlType);
                    // 取得 中文註解
                    String schemaName = "";
                    for (int i = 0; i < parts.length; i++) {
                        if ("--".equals(parts[i])) {
                            schemaName = parts[i+1];
                        }
                    }
                    fields.add(new String[]{fieldName, javaType, parts[0], schemaName});
                }
            }
            reader.close();

            // 生成 Entity, Repository 和 Controller
            generateEntity(entityName, entityScheamName, fields, primaryKeys);
            generateRepository(entityName, fields, primaryKeys);
            generateController(entityName, fields, primaryKeys);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立 entity 類別，根據資料庫表格欄位生成對應的 Java 實體類別
     * @param entityName 實體名稱 (如：User)
     * @param entityScheamName 實體的中文註解（可選）
     * @param fields 欄位名稱、型別等資料
     * @param primaryKeys 主鍵集合
     * @throws IOException 讀取或寫入檔案時的錯誤
     */
    private static void generateEntity(String entityName, String entityScheamName, List<String[]> fields, Set<String> primaryKeys) throws IOException {
        File entityFile = new File("file/output/" + entityName + ".java");
        BufferedWriter entityWriter = new BufferedWriter(new FileWriter(entityFile));

        // 寫入 Entity 類別需要的 import 依賴
        entityWriter.write("import javax.persistence.Entity;\n");
        entityWriter.write("import javax.persistence.Id;\n");
        entityWriter.write("import javax.persistence.Column;\n");
        entityWriter.write("import javax.persistence.Table;\n");
        entityWriter.write("import io.swagger.v3.oas.annotations.media.Schema;\n");
        if (primaryKeys.size() > 1) {
            entityWriter.write("import javax.persistence.IdClass;\n");
        }
        entityWriter.write("import java.io.Serializable;\n");
        entityWriter.write("import java.util.Objects;\n");
        entityWriter.write("\n");

        // 根據是否有複合主鍵來決定@Entity 和 @IdClass 的使用
        if (primaryKeys.size() > 1) {
            entityWriter.write("@Entity\n");
            entityWriter.write("@Table(name = \"" + toCamelCase(entityName, true).toLowerCase() + "\")\n");
            entityWriter.write("@IdClass(" + entityName + "." + entityName + "Key.class)\n"); // 使用內部類別 Key
        } else {
            entityWriter.write("@Entity\n");
            entityWriter.write("@Table(name = \"" + toCamelCase(entityName, true).toLowerCase() + "\")\n");
        }
        if (entityScheamName.length() > 0) {
            entityWriter.write("@Schema(description = \"" + entityScheamName + "\")\n");
        }
        entityWriter.write("public class " + entityName + " implements Serializable {\n");
        entityWriter.write("    private static final long serialVersionUID = 1L;\n\n");

        // 欄位定義，根據資料庫欄位生成對應的實體類別欄位
        for (String[] field : fields) {
            String fieldName = field[0];
            String javaType = field[1];
            String originalName = field[2];
            String schemaName = field[3];
            boolean isPrimaryKey = primaryKeys.contains(originalName);
            if (isPrimaryKey) {
                entityWriter.write("    @Id\n");
            }
            if (schemaName.length() > 0) {
                entityWriter.write("    @Schema(description = \"" + schemaName + "\")\n");
            }
            entityWriter.write("    @Column(name = \"" + originalName + "\")\n");
            entityWriter.write("    private " + javaType + " " + fieldName + ";\n\n");
        }

        entityWriter.write("\n");

        // 生成無參數建構子
        entityWriter.write("    public " + entityName + "() {\n");
        entityWriter.write("    }\n\n");

        // 生成 Getter 和 Setter 方法
        for (String[] field : fields) {
            String fieldName = field[0];
            String javaType = field[1];
            // Getter
            entityWriter.write("    public " + javaType + " get" + capitalize(fieldName) + "() {\n");
            // String 要去除尾部空白
            if (javaType.equals("String")) {
                entityWriter.write("        return " + fieldName + "!= null ? " + fieldName + ".trim() : null;\n");

            } else {
                entityWriter.write("        return " + fieldName + ";\n");
            }
//            entityWriter.write("        return " + fieldName + ";\n");

            entityWriter.write("    }\n\n");
            // Setter
            entityWriter.write("    public void set" + capitalize(fieldName) + "(" + javaType + " " + fieldName + ") {\n");
            entityWriter.write("        this." + fieldName + " = " + fieldName + ";\n");
            entityWriter.write("    }\n\n");
        }

        // 生成 equals 和 hashCode 方法，基於主鍵欄位進行比較
        entityWriter.write("    @Override\n");
        entityWriter.write("    public boolean equals(Object o) {\n");
        entityWriter.write("        if (this == o) return true;\n");
        entityWriter.write("        if (o == null || getClass() != o.getClass()) return false;\n");
        entityWriter.write("        " + entityName + " that = (" + entityName + ") o;\n");
        entityWriter.write("        return ");
        boolean first = true;
        for (String[] field : fields) {
            if (primaryKeys.contains(field[2])) {
                if (!first) entityWriter.write(" && ");
                entityWriter.write("Objects.equals(" + field[0] + ", that." + field[0] + ")");
                first = false;
            }
        }
        if (first) entityWriter.write("true");
        entityWriter.write(";\n");
        entityWriter.write("    }\n\n");

        // hashCode 方法
        entityWriter.write("    @Override\n");
        entityWriter.write("    public int hashCode() {\n");
        entityWriter.write("        return Objects.hash(");
        first = true;
        for (String[] field : fields) {
            if (primaryKeys.contains(field[2])) {
                if (!first) entityWriter.write(", ");
                entityWriter.write(field[0]);
                first = false;
            }
        }
        entityWriter.write(");\n");
        entityWriter.write("    }\n\n");

        // 如果有多主鍵，生成內部 Key 類
        if (primaryKeys.size() > 1) {
            entityWriter.write("    public static class " + entityName + "Key implements Serializable {\n");
            entityWriter.write("        private static final long serialVersionUID = 1L;\n\n");
            for (String[] field : fields) {
                if (primaryKeys.contains(field[2])) {
                    entityWriter.write("        private " + field[1] + " " + field[0] + ";\n");
                }
            }
            entityWriter.write("\n");

            // Key 的無參建構子
            entityWriter.write("        public " + entityName + "Key() {\n");
            entityWriter.write("        }\n\n");

            // Key 的 getter 和 setter
            for (String[] field : fields) {
                if (primaryKeys.contains(field[2])) {
                    String fieldName = field[0];
                    String javaType = field[1];
                    entityWriter.write("        public " + javaType + " get" + capitalize(fieldName) + "() {\n");
                    entityWriter.write("            return " + fieldName + ";\n");
                    entityWriter.write("        }\n\n");
                    entityWriter.write("        public void set" + capitalize(fieldName) + "(" + javaType + " " + fieldName + ") {\n");
                    entityWriter.write("            this." + fieldName + " = " + fieldName + ";\n");
                    entityWriter.write("        }\n\n");
                }
            }

            // Key 的 equals 和 hashCode
            entityWriter.write("        @Override\n");
            entityWriter.write("        public boolean equals(Object o) {\n");
            entityWriter.write("            if (this == o) return true;\n");
            entityWriter.write("            if (o == null || getClass() != o.getClass()) return false;\n");
            entityWriter.write("            " + entityName +"Key that = (" + entityName + "Key) o;\n");
            entityWriter.write("            return ");
            first = true;
            for (String[] field : fields) {
                if (primaryKeys.contains(field[2])) {
                    if (!first) entityWriter.write(" && ");
                    entityWriter.write("Objects.equals(" + field[0] + ", that." + field[0] + ")");
                    first = false;
                }
            }
            entityWriter.write(";\n");
            entityWriter.write("        }\n\n");

            entityWriter.write("        @Override\n");
            entityWriter.write("        public int hashCode() {\n");
            entityWriter.write("            return Objects.hash(");
            first = true;
            for (String[] field : fields) {
                if (primaryKeys.contains(field[2])) {
                    if (!first) entityWriter.write(", ");
                    entityWriter.write(field[0]);
                    first = false;
                }
            }
            entityWriter.write(");\n");
            entityWriter.write("        }\n");
            entityWriter.write("    }\n");
        }

        entityWriter.write("}\n");
        entityWriter.close();
        System.out.println("生成 Entity 檔案，位於 " + "file/output/" + entityName + ".java");
    }

    /**
     * 建立 repository 類別，自動根據 entity 設定 JpaRepository
     * @param entityName 實體名稱 (如：User)
     * @param fields 欄位名稱、型別等資料
     * @param primaryKeys 主鍵集合
     * @throws IOException 讀取或寫入檔案時的錯誤
     */
    private static void generateRepository(String entityName, List<String[]> fields, Set<String> primaryKeys) throws IOException {
        File repoFile = new File("file/output/" + entityName + "Repository.java");
        BufferedWriter repoWriter = new BufferedWriter(new FileWriter(repoFile));

        // 檢查主鍵的型態
        String idType = primaryKeys.size() > 1 ? entityName + "." + entityName + "Key" : fields.stream()
                .filter(f -> primaryKeys.contains(f[2]))
                .findFirst()
                .map(f -> f[1])
                .orElse("Integer");

        repoWriter.write("import org.springframework.data.jpa.repository.JpaRepository;\n");
        repoWriter.write("\n");
        repoWriter.write("public interface " + entityName + "Repository extends JpaRepository<" + entityName + ", " + idType + "> {\n");
        repoWriter.write("}\n");

        repoWriter.close();
        System.out.println("生成 Repository 檔案，位於 " + "file/output/" + entityName + "Repository.java");
    }

    /**
     * 建立 controller 類別，自動根據 單主鍵 或 多組件 封裝 save, findById, delete
     * @param entityName 實體名稱 (如：User)
     * @param fields 欄位名稱、型別等資料
     * @param primaryKeys 主鍵集合
     * @throws IOException 讀取或寫入檔案時的錯誤
     */
    private static void generateController(String entityName, List<String[]> fields, Set<String> primaryKeys) throws IOException {
        File controllerFile = new File("file/output/" + entityName + "Controller.java");
        BufferedWriter controllerWriter = new BufferedWriter(new FileWriter(controllerFile));

        controllerWriter.write("import org.slf4j.Logger;\n");
        controllerWriter.write("import org.slf4j.LoggerFactory;\n");
        controllerWriter.write("import org.springframework.beans.factory.annotation.Autowired;\n");
        controllerWriter.write("import org.springframework.http.ResponseEntity;\n");
        controllerWriter.write("import org.springframework.web.bind.annotation.*;\n");
        controllerWriter.write("import io.swagger.v3.oas.annotations.Operation;\n");
        controllerWriter.write("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        controllerWriter.write("import io.swagger.v3.oas.annotations.Parameter;\n");
        controllerWriter.write("\n");

        controllerWriter.write("@RestController\n");
        controllerWriter.write("@RequestMapping(\"/api/" + toCamelCase(entityName, true) + "\")\n");
        controllerWriter.write("@Tag(name = \"" + entityName + " Controller\")\n");
        controllerWriter.write("public class " + entityName + "Controller {\n");
        // Logger 定義
        controllerWriter.write("    private Logger logger = LoggerFactory.getLogger(this.getClass());\n\n");
        controllerWriter.write("\n");

        // 依據主鍵數量決定方法簽名
        if (primaryKeys.size() == 1) {
            // 單主鍵的情況
            controllerWriter.write("    @Autowired\n");
            controllerWriter.write("    private " + entityName + "Repository " + toCamelCase(entityName, false) + "Repository;\n");
            controllerWriter.write("\n");

            String primaryKeyType = fields.stream()
                    .filter(f -> primaryKeys.contains(f[2]))
                    .findFirst()
                    .map(f -> f[1])
                    .orElse("Integer"); // 預設 Integer

            controllerWriter.write("    @Operation(summary = \"根據主鍵 新增或更新 " + entityName + "\",\n");
            controllerWriter.write("               description = \"根據主鍵，若有資料則更新，無資料則新增\",\n");
            controllerWriter.write("               operationId = \"save\")\n");
            controllerWriter.write("    @PostMapping(\"/save\")\n");
            controllerWriter.write("    public ResponseEntity<" + entityName + "> save(@RequestBody " + entityName + " entity) {\n");
            controllerWriter.write("        " + entityName + " savedEntity = " + toCamelCase(entityName, false) + "Repository.save(entity);\n");
            controllerWriter.write("        return ResponseEntity.ok(savedEntity);\n");
            controllerWriter.write("    }\n");

            controllerWriter.write("\n");
            controllerWriter.write("    @Operation(summary = \"根據主鍵 查詢 " + entityName + "\",\n");
            controllerWriter.write("               description = \"根據主鍵查詢 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"findById\")\n");
            controllerWriter.write("    @GetMapping(\"/{id}\")\n");
            controllerWriter.write("    public ResponseEntity<" + entityName + "> findById(\n");
            controllerWriter.write("        @Parameter(description = \"主鍵\") @PathVariable(\"id\") " + primaryKeyType + " id) {\n");
            controllerWriter.write("        " + entityName + " entity = " + entityName.toLowerCase() + "Repository.findById(id)\n");
            controllerWriter.write("            .orElse(null);\n");
            controllerWriter.write("        if (entity == null) {\n");
            controllerWriter.write("            return ResponseEntity.ok().build();\n");
            controllerWriter.write("        }\n");
            controllerWriter.write("        return ResponseEntity.ok(entity);  // 回傳 HTTP 200 OK 和資料\n");
            controllerWriter.write("    }\n\n");

            // Delete (單主鍵)
            controllerWriter.write("    @Operation(summary = \"根據主鍵 刪除 " + entityName + " 資料\",\n");
            controllerWriter.write("               description = \"根據主鍵刪除 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"deleteById\")\n");
            controllerWriter.write("    @DeleteMapping(\"/{id}\")\n");
            controllerWriter.write("    public ResponseEntity<Void> deleteById(\n");
            controllerWriter.write("        @Parameter(description = \"主鍵\") @PathVariable(\"id\") " + primaryKeyType + " id) {\n");
            controllerWriter.write("        if (" + entityName.toLowerCase() + "Repository.existsById(id)) {\n");
            controllerWriter.write("            " + entityName.toLowerCase() + "Repository.deleteById(id);\n");
            controllerWriter.write("            return ResponseEntity.ok(null); // 回傳 HTTP 200 OK 且 資料為 null\n");
            controllerWriter.write("        }\n");
            controllerWriter.write("        return ResponseEntity.ok().build();  // 回傳 HTTP 200 OK 和資料\n");
            controllerWriter.write("    }\n\n");
        } else {
            // 多主鍵的情況
            controllerWriter.write("    @Autowired\n");
            controllerWriter.write("    private " + entityName + "Repository " + toCamelCase(entityName, false) + "Repository;\n");
            controllerWriter.write("\n");

            // 使用複合主鍵類型
            String idType = entityName + "." + entityName + "Key"; // 假設複合主鍵類型為 Key

            controllerWriter.write("    @Operation(summary = \"根據主鍵 新增或更新 " + entityName + "\",\n");
            controllerWriter.write("               description = \"根據主鍵，若有資料則更新，無資料則新增\",\n");
            controllerWriter.write("               operationId = \"save\")\n");
            controllerWriter.write("    @PostMapping(\"/save\")\n");
            controllerWriter.write("    public ResponseEntity<" + entityName + "> save(@RequestBody " + entityName + " entity) {\n");
            controllerWriter.write("        " + entityName + " savedEntity = " + toCamelCase(entityName, false) + "Repository.save(entity);\n");
            controllerWriter.write("        return ResponseEntity.ok(savedEntity);\n");
            controllerWriter.write("    }\n");

            controllerWriter.write("\n");
            controllerWriter.write("    @Operation(summary = \"根據主鍵 查詢 " + entityName + "\",\n");
            controllerWriter.write("               description = \"根據主鍵查詢 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"findById\")\n");
            controllerWriter.write("    @PostMapping(\"/getByIds\")\n");
            controllerWriter.write("    public ResponseEntity<" + entityName + "> getByIds(@RequestBody " + idType + " key) {\n");
            controllerWriter.write("        " + entityName + " entity = " + toCamelCase(entityName, false) + "Repository.findById(key)\n");
            controllerWriter.write("            .orElse(null);\n");
            controllerWriter.write("        if (entity == null) {\n");
            controllerWriter.write("            return ResponseEntity.ok(null); // 回傳 HTTP 200 OK 且 資料為 null\n");
            controllerWriter.write("        }\n");
            controllerWriter.write("        return ResponseEntity.ok(entity);  // 回傳 HTTP 200 OK 和資料\n");
            controllerWriter.write("    }\n\n");


            controllerWriter.write("\n");

            controllerWriter.write("    @Operation(summary = \"根據主鍵 刪除 " + entityName + " 資料\",\n");
            controllerWriter.write("               description = \"根據主鍵刪除 " + entityName + " 資料\",\n");
            controllerWriter.write("               operationId = \"deleteById\")\n");
            controllerWriter.write("    @PostMapping(\"/delete\")\n");
            controllerWriter.write("    public ResponseEntity<Void> delete(@RequestBody " + idType + " key) {\n");
            controllerWriter.write("        if(" + toCamelCase(entityName, false) + "Repository.existsById(key)) {\n");
            controllerWriter.write("            " + toCamelCase(entityName, false) + "Repository.deleteById(key);\n");
            controllerWriter.write("        }\n");
            controllerWriter.write("        return ResponseEntity.ok().build();\n");
            controllerWriter.write("    }\n");
        }

        controllerWriter.write("}\n");
        controllerWriter.close();
        System.out.println("生成 Controller 檔案，位於 " + "file/output/" + entityName + "Controller.java");
    }

    /**
     * 將字串轉換為駝峰式命名規範（Camel Case）
     * @param name 要轉換的字串
     * @param capitalizeFirst 是否將首字母大寫
     * @return 轉換後的字串
     */
    private static String toCamelCase(String name, boolean capitalizeFirst) {
        String[] parts = name.split("_");
        StringBuilder camelCase = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i == 0 && !capitalizeFirst) {
                camelCase.append(parts[i].toLowerCase());
            } else {
                camelCase.append(parts[i].substring(0, 1).toUpperCase())
                        .append(parts[i].substring(1).toLowerCase());
            }
        }
        return camelCase.toString();
    }

    /**
     * 將首字母大寫（用於 getter/setter）
     * @param str 原始文字
     * @return 首字母大寫文字
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 根據資料庫欄位型別對應 Java 類型
     * @param sqlType 資料庫欄位型別
     * @return 對應的 Java 類型
     */
    private static String mapSqlType(String sqlType) {
        sqlType = sqlType.toLowerCase();
        if (sqlType.matches("char.*|varchar.*|lvarchar.*")) {
            return "String";
        } else if (sqlType.matches("int8")) {
            return "Long";
        } else if (sqlType.matches("int4|int|integer")) {
            return "Integer";
        } else if (sqlType.matches("smallint")) {
            return "Short";
        } else if (sqlType.contains("float")) {
            return "Double"; // 將 float 映射為 Double
        }
        return sqlType; // 未定義的保持原樣
    }
}
