import java.io.*;
import java.util.*;

import static utils.BasicUtil.*;
import static utils.ControllerUtil.generateController;
import static utils.EntityUtil.*;
import static utils.RepositoryUtil.generateRepository;
import static utils.RepositoryUtil.generateRepositoryImpl;
import static utils.ServiceUtil.generateServiceImpl;
import static utils.ServiceUtil.generateServiceInterface;

public class Application {
    public static void main(String[] args) {
        try {
            File inputFile = new File("file/input.txt");
            File pkFile = new File("file/primary_keys.txt");

            // 生成 共用資料夾
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
            Boolean primaryKeyExists = primaryKeys.size() > 0 ? true : false;

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
                    // 針對無主鍵者，所有欄位都變成主鍵
                    if (!primaryKeyExists) {
                        primaryKeys.add(parts[0].trim());
                    }
                }
            }
            reader.close();

            // 生成 資料夾
            new File("file/output/entity").mkdirs();
            new File("file/output/repository").mkdirs();
            new File("file/output/repository/impl").mkdirs();
            new File("file/output/controller").mkdirs();
            new File("file/output/service").mkdirs();
            new File("file/output/service/impl").mkdirs();

            // 生成 Entity, Repository, service, serviceImpl 和 Controller
            generateEntity(entityName, entityScheamName, fields, primaryKeys, primaryKeyExists);
            generateRepository(entityName, fields, primaryKeys);
            generateRepositoryImpl(entityName, fields, primaryKeys, primaryKeyExists);
            generateServiceInterface(entityName, fields, primaryKeys, primaryKeyExists);
            generateServiceImpl(entityName, fields, primaryKeys, primaryKeyExists);
            generateController(entityName, entityScheamName, fields, primaryKeys, primaryKeyExists);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
