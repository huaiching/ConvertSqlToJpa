import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Application {
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
            List<String[]> fields = new ArrayList<>(); // 儲存欄位資訊

            // 先解析出 entityName 和 fields
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.toUpperCase().startsWith("CREATE TABLE")) {
                    String[] parts = line.split("\\s+");
                    tableName = parts[2];
                    entityName = toCamelCase(tableName, true); // 首字母大寫
                } else if (!line.isEmpty() && !line.equals(")")) {
                    String[] parts = line.trim().replace(",", "").split("\\s+");
                    String fieldName = toCamelCase(parts[0], false);
                    String sqlType = parts[1];
                    String javaType = mapSqlType(sqlType);
                    fields.add(new String[]{fieldName, javaType, parts[0]});
                }
            }
            reader.close();

            // 創建 Entity 檔案
            File entityFile = new File("file/output/" + entityName + ".java");
            BufferedWriter entityWriter = new BufferedWriter(new FileWriter(entityFile));

            // 寫入 Entity 內容（使用 jakarta.persistence）
            entityWriter.write("import jakarta.persistence.Entity;\n");
            entityWriter.write("import jakarta.persistence.Id;\n");
            entityWriter.write("import jakarta.persistence.Column;\n");
            entityWriter.write("import jakarta.persistence.Table;\n");
            if (primaryKeys.size() > 1) {
                entityWriter.write("import jakarta.persistence.IdClass;\n");
            }
            entityWriter.write("import java.io.Serializable;\n");
            entityWriter.write("import java.util.Objects;\n");
            entityWriter.write("\n");

            if (primaryKeys.size() > 1) {
                entityWriter.write("@Entity\n");
                entityWriter.write("@Table(name = \"" + tableName + "\")\n");
                entityWriter.write("@IdClass(" + entityName + ".Key.class)\n");
            } else {
                entityWriter.write("@Entity\n");
                entityWriter.write("@Table(name = \"" + tableName + "\")\n");
            }
            entityWriter.write("public class " + entityName + " implements Serializable {\n");
            entityWriter.write("    private static final long serialVersionUID = 1L;\n\n");

            // 欄位定義
            for (String[] field : fields) {
                String fieldName = field[0];
                String javaType = field[1];
                String originalName = field[2];
                boolean isPrimaryKey = primaryKeys.contains(originalName);
                if (isPrimaryKey) {
                    entityWriter.write("    @Id\n");
                }
                entityWriter.write("    @Column(name = \"" + originalName + "\")\n");
                entityWriter.write("    private " + javaType + " " + fieldName + ";\n");
            }

            entityWriter.write("\n");
            // 無參建構子
            entityWriter.write("    public " + entityName + "() {\n");
            entityWriter.write("    }\n\n");

            // Getter 和 Setter
            for (String[] field : fields) {
                String fieldName = field[0];
                String javaType = field[1];
                // Getter
                entityWriter.write("    public " + javaType + " get" + capitalize(fieldName) + "() {\n");
                entityWriter.write("        return " + fieldName + ";\n");
                entityWriter.write("    }\n\n");
                // Setter
                entityWriter.write("    public void set" + capitalize(fieldName) + "(" + javaType + " " + fieldName + ") {\n");
                entityWriter.write("        this." + fieldName + " = " + fieldName + ";\n");
                entityWriter.write("    }\n\n");
            }

            // equals 方法
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
                entityWriter.write("    public static class Key implements Serializable {\n");
                entityWriter.write("        private static final long serialVersionUID = 1L;\n\n");
                for (String[] field : fields) {
                    if (primaryKeys.contains(field[2])) {
                        entityWriter.write("        private " + field[1] + " " + field[0] + ";\n");
                    }
                }
                entityWriter.write("\n");
                // Key 的無參建構子
                entityWriter.write("        public Key() {\n");
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
                entityWriter.write("            Key that = (Key) o;\n");
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

            // 創建 Repository 檔案
            File repoFile = new File("file/output/" + entityName + "Repository.java");
            BufferedWriter repoWriter = new BufferedWriter(new FileWriter(repoFile));

            // 寫入 Repository 內容
            repoWriter.write("import org.springframework.data.jpa.repository.JpaRepository;\n");
            repoWriter.write("\n");
            String idType = primaryKeys.size() > 1 ? entityName + ".Key" : fields.stream()
                    .filter(f -> primaryKeys.contains(f[2]))
                    .findFirst()
                    .map(f -> f[1])
                    .orElse("Integer"); // 預設 Integer 如果沒有主鍵
            repoWriter.write("public interface " + entityName + "Repository extends JpaRepository<" + entityName + ", " + idType + "> {\n");
            repoWriter.write("}\n");

            repoWriter.close();

            System.out.println("JPA Entity 已生成至 file/output/" + entityName + ".java");
            System.out.println("Repository 已生成至 file/output/" + entityName + "Repository.java");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 轉換下底線為駝峰命名
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

    // 將首字母大寫（用於 getter/setter）
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // 映射 SQL 型態到 Java 型態
    private static String mapSqlType(String sqlType) {
        sqlType = sqlType.toLowerCase();
        if (sqlType.matches("char.*|varchar.*|lvarchar.*")) {
            return "String";
        } else if (sqlType.matches("int8")) {
            return "long";
        } else if (sqlType.matches("int4|int")) {
            return "int";
        } else if (sqlType.matches("smallint")) {
            return "short";
        } else if (sqlType.contains("float")) {
            return "Double"; // 將 float 映射為 Double
        }
        return sqlType; // 未定義的保持原樣
    }
}