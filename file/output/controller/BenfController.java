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

    @Operation(summary = "根據主鍵 新增或更新 Benf",
               description = "根據主鍵，若有資料則更新，無資料則新增",
               operationId = "save")
    @PostMapping("/save")
    public ResponseEntity<Benf> save(@RequestBody Benf entity) {
        Benf savedEntity = benfService.save(entity);
        return ResponseEntity.ok(savedEntity);
    }

    @Operation(summary = "根據主鍵 大量 新增或更新 Benf",
               description = "根據主鍵，若有資料則更新，無資料則新增",
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

    @Operation(summary = "根據主鍵 查詢 Benf",
               description = "根據主鍵查詢 Benf 資料",
               operationId = "findById")
    @PostMapping("/getByIds")
    public ResponseEntity<Benf> getByIds(@RequestBody Benf.BenfKey id) {
        Benf entity = benfService.findById(id.getPolicyNo());
        if (entity == null) {
            return ResponseEntity.ok(null); // 回傳 HTTP 200 OK 且 資料為 null
        }
        return ResponseEntity.ok(entity);  // 回傳 HTTP 200 OK 和資料
    }

    @Operation(summary = "根據主鍵 刪除 Benf 資料",
               description = "根據主鍵刪除 Benf 資料",
               operationId = "deleteById")
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody Benf.BenfKey id) {
        benfService.deleteById(id.getPolicyNo());
        return ResponseEntity.ok().build();
    }
}
