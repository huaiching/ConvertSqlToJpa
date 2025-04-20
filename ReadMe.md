# Spring Data JPA Entity Generator

此工具用於將 SQL `CREATE TABLE` 語句轉換為 Spring Data JPA 的 Entity 和 Repository。

## 使用說明

### 1. 準備輸入檔案
  - 準備 SQL `CREATE TABLE` 語句
    - **檔案位置**: `file/input.txt`
      - **內容格式**: SQL `CREATE TABLE` 語句，例如：
        ```sql
        CREATE TABLE addr (     -- 客戶地址檔
          client_id  CHAR(10),  -- 客戶證號
          addr_ind   CHAR(1),   -- 地址指示
          address    CHAR(72),  -- 地址
          tel_1      CHAR(11),  -- 電話 1
          tel_2      CHAR(11)   -- 電話 2
        )
        ```
        中文說明 須放置於 ``--`` 的後面，並用 空格 分隔 <br/>
        有提供中文說明者，``entity`` 會建立 SWAGGER 相關說明
    - **支援型態**: 
      - 文字: `char`, `varchar`, `LVARCHAR` -> `String`
      - 數字: `int8` -> `long`
      - 數字: `int4`, `int` -> `int`
      - 數字: `smallint` -> `short`
      - 浮點數: `float` -> `Double`
      - 未定義型態保持原樣

  - 主鍵欄位
    - **檔案位置**: `file/primary_keys.txt`
    - **內容格式**: 每行一個主鍵欄位名稱，例如：
      ```
      client_id
      ```
      代表 單一主鍵
      ```
      client_id
      addr_ind
      ```
      代表 複合主鍵
      ```
      ```
      空白，代表 table 無主鍵，會使用 table 全部欄位 來建立 複合組鍵
    - **注意**: 若有多於一個主鍵，將生成內部 `Key` 類作為複合主鍵。

### 2. 執行程式
  - 運行(Run) `src/Application.java` 即可執行此程式
  - 運行後，將會自動產生 spring data jpa 的相關檔案
    - **entity**：``file/output/entity``資料夾
      - 有主鍵 table，依照``primary_keys.txt``的設定建立主鍵
      - 無主鍵 table，使用 table 全部欄位 來建立複合主鍵
    - **repository**：``file/output/repository``資料夾
    - **service**：``file/output/service``資料夾
    - **serviceImpl**：``file/output/serviceImpl``資料夾
    - **controller**：``file/output/controller``資料夾
      - 有主鍵：save, saveAll, update, deleteById, findById。
      - 無主鍵：save, saveAll, update, deleteByEntity。 <br>
        無主鍵者，要自己處理 查詢方法。
    
### 3. 處理輸出檔案
- 將 `file/out` 資料夾中的 所有檔案 複製到 你的 Java 專案的對應資料夾中 <br/>
  並 進行細部調整，例如：
  - `Entity` 的 未定義型態處理。
  - 各檔案之間的 import 設定。

### 4. 注意事項
- 請確保 Java 環境已正確設定。
- 輸入檔案的每一行應遵循標準 SQL 格式，欄位名稱和型態之間需有空格。
- 如果未提供 `primary_keys.txt`，或 內容空白 者，視為 無主鍵 table <br/>
  會使用 table 全部欄位 來建立 複合組鍵。
