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
@RequestMapping("/api/Addr")
@Tag(name = "Addr Controller", description = "客戶地址檔 API 接口")
public class AddrController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AddrService addrService;

    @Operation(summary = "根據主鍵 新增或更新 Addr",
               description = "根據主鍵，若有資料則更新，無資料則新增",
               operationId = "save")
    @PostMapping("/save")
    public ResponseEntity<Addr> save(@RequestBody Addr entity) {
        Addr savedEntity = addrService.save(entity);
        return ResponseEntity.ok(savedEntity);
    }

    @Operation(summary = "根據主鍵 大量 新增或更新 Addr",
               description = "根據主鍵，若有資料則更新，無資料則新增",
               operationId = "saveAll")
    @PostMapping("/saveAll")
    public ResponseEntity<List<Addr>> saveAll(@RequestBody List<Addr> entityList) {
        List<Addr> savedEntityList = addrService.saveAll(entityList);
        return ResponseEntity.ok(savedEntityList);
    }

    @Operation(summary = "單筆更新 Addr",
               description = "單筆新增 Addr 資料",
               operationId = "update")
    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody Addr.AddrUpdate entityUpdate) {
        addrService.update(entityUpdate.getAddrOri(), entityUpdate.getAddrNew());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "根據主鍵 查詢 Addr",
               description = "根據主鍵查詢 Addr 資料",
               operationId = "findById")
    @PostMapping("/getByIds")
    public ResponseEntity<Addr> getByIds(@RequestBody Addr.AddrKey id) {
        Addr entity = addrService.findById(id);
        if (entity == null) {
            return ResponseEntity.ok(null); // 回傳 HTTP 200 OK 且 資料為 null
        }
        return ResponseEntity.ok(entity);  // 回傳 HTTP 200 OK 和資料
    }

    @Operation(summary = "根據主鍵 刪除 Addr 資料",
               description = "根據主鍵刪除 Addr 資料",
               operationId = "deleteById")
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody Addr.AddrKey id) {
        addrService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
