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
 * 產生 entity 的相關方法
 */
public class EntityUtil {
    /**
     * 建立 entity 類別，根據資料庫表格欄位生成對應的 Java 實體類別
     * @param entityName 實體名稱 (如：User)
     * @param entityScheamName 實體的中文註解（可選）
     * @param fields 欄位名稱、型別等資料
     * @param primaryKeys 主鍵集合
     * @param primaryKeyExists 主鍵是否存在
     * @throws IOException 讀取或寫入檔案時的錯誤
     */
    public static void generateEntity(String entityName, String entityScheamName, List<String[]> fields, Set<String> primaryKeys, Boolean primaryKeyExists) throws IOException {
        File entityFile = new File("file/output/entity/" + entityName + ".java");
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
        entityWriter.write("@Entity\n");
        entityWriter.write("@Table(name = \"" + toCamelCase(entityName, true).toLowerCase() + "\")\n");
        if (primaryKeys.size() > 1) {
            entityWriter.write("@IdClass(" + entityName + "." + entityName + "Key.class)\n"); // 使用內部類別 Key
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

        // 針對無主鍵者，要生成 update 類
        if (!primaryKeyExists) {
            entityWriter.write("    public static class " + entityName + "Update implements Serializable {\n");
            entityWriter.write("        private static final long serialVersionUID = 1L;\n\n");
            entityWriter.write("        private " + entityName + " " + entityName.toLowerCase() + "Ori;\n");
            entityWriter.write("        private " + entityName + " " + entityName.toLowerCase() + "New;\n");
            entityWriter.write("\n");

            // Key 的 getter 和 setter
            entityWriter.write("        public " + entityName + " get" + capitalize(entityName) + "Ori() {\n");
            entityWriter.write("            return " + entityName.toLowerCase() + "Ori;\n");
            entityWriter.write("        }\n\n");
            entityWriter.write("        public void set" + capitalize(entityName) + "Ori(" + entityName + " " + entityName.toLowerCase() + "Ori) {\n");
            entityWriter.write("            this." + entityName.toLowerCase() + "Ori = " + entityName.toLowerCase() + "Ori;\n");
            entityWriter.write("        }\n\n");
            entityWriter.write("        public " + entityName + " get" + capitalize(entityName) + "New() {\n");
            entityWriter.write("            return " + entityName.toLowerCase() + "New;\n");
            entityWriter.write("        }\n\n");
            entityWriter.write("        public void set" + capitalize(entityName) + "New(" + entityName + " " + entityName.toLowerCase() + "New) {\n");
            entityWriter.write("            this." + entityName.toLowerCase() + "New = " + entityName.toLowerCase() + "New;\n");
            entityWriter.write("        }\n\n");
            entityWriter.write("    }\n");
        }

        entityWriter.write("}\n");
        entityWriter.close();
        System.out.println("生成 Entity 檔案，位於 " + "file/output/entity/" + entityName + ".java");
    }


}
