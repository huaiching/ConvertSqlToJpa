import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

@RestController
@RequestMapping("/api/Benf")
@Tag(name = "Benf Controller", description = "受益人檔 API 接口")
public class BenfController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BenfService benfService;

    @Operation(summary = "單筆新增 Benf",
               description = "單筆新增 Benf 資料",
               operationId = "save")
    @PostMapping("/save")
    public ResponseEntity<Benf> save(@RequestBody Benf entity) {
        Benf savedEntity = benfService.save(entity);
        return ResponseEntity.ok(savedEntity);
    }

    @Operation(summary = "多筆新增 Benf",
               description = "多筆新增 Benf 資料",
               operationId = "saveAll")
    @PostMapping("/saveAll")
    public ResponseEntity<List<Benf>> saveAll(@RequestBody List<Benf> entityList) {
        List<Benf> savedEntityList = benfService.saveAll(entityList);
        return ResponseEntity.ok(savedEntityList);
    }

    @Operation(summary = "單筆更新 Benf",
               description = "單筆新增 Benf 資料",
               operationId = "update")
    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody Benf.BenfUpdate entityUpdate) {
        benfService.update(entityUpdate.getBenfOri(), entityUpdate.getBenfNew());
        return ResponseEntity.ok().build();
    }

    // 無主鍵者，自行處理 查詢 方法

    @Operation(summary = "單筆刪除 Benf",
               description = "單筆刪除 Benf 資料",
               operationId = "deleteByEntity")
    @PostMapping("/deleteByEntity")
    public ResponseEntity<Void> deleteByEntity(@RequestBody Benf.BenfKey entity) {
        benfService.deleteByEntity(entity);
        return ResponseEntity.ok().build();
    }

}
