# Spring Data JPA Entity Generator

此工具用於將 SQL `CREATE TABLE` 語句轉換為 Spring Data JPA 的 Entity 和 Repository。

## 使用說明

### 1. 準備輸入檔案
- **檔案位置**: `file/input.txt`
- **內容格式**: SQL `CREATE TABLE` 語句，例如：
  ```sql
  CREATE TABLE user_info (
      user_id int,
      group_id int,
      first_name varchar(50),
      last_name LVARCHAR,
      age_group int4,
      height_value float
  )
  ```

- **支援型態**: 
  - 文字: `char`, `varchar`, `LVARCHAR` -> `String`
  - 數字: `int8` -> `long`
  - 數字: `int4`, `int` -> `int`
  - 數字: `smallint` -> `short`
  - 浮點數: `float` -> `Double`
  - 未定義型態保持原樣

### 2. 準備主鍵定義檔案（可選）
- **檔案位置**: `file/primary_keys.txt`
- **內容格式**: 每行一個主鍵欄位名稱，例如：
  ```
  user_id
  group_id
  ```
- **注意**: 若有多於一個主鍵，將生成內部 `Key` 類作為複合主鍵。

### 3. 執行程式
- Run `Application.java` 即可執行此程式

### 4. 檢查輸出檔案
- **Entity 檔案**: `file/output/UserInfo.java`
  - 包含完整的 JPA 註解、getter/setter、equals 和 hashCode。
  - 若有多主鍵，包含內部 `Key` 類。
- **Repository 檔案**: `file/output/UserInfoRepository.java`
  - 繼承 `JpaRepository`，支援基本的 CRUD 操作。

### 5. 處理輸出檔案
- 將 `Entity` 及 `Repository` 複製到 你的 Java 專案的對應資料夾中，並 進行細部調整，例如：
  - `Entity` 的 未定義型態處理
  - `Repository` 的 import 設定

### 6. 注意事項
- 請確保 Java 環境已正確設定。
- 輸入檔案的每一行應遵循標準 SQL 格式，欄位名稱和型態之間需有空格。
- 如果未提供 `primary_keys.txt`，則不設定主鍵，Repository 的 ID 型態預設為 `Integer`。
