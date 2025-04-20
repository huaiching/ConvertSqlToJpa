package utils;

/**
 * 設定共用方法
 */
public class BasicUtil {

    /**
     * 將字串轉換為駝峰式命名規範（Camel Case）
     * @param name 要轉換的字串
     * @param capitalizeFirst 是否將首字母大寫
     * @return 轉換後的字串
     */
    public static String toCamelCase(String name, boolean capitalizeFirst) {
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
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 根據資料庫欄位型別對應 Java 類型
     * @param sqlType 資料庫欄位型別
     * @return 對應的 Java 類型
     */
    public static String mapSqlType(String sqlType) {
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
