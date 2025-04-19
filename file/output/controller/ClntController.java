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
@RequestMapping("/api/Clnt")
@Tag(name = "Clnt Controller")
public class ClntController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClntService clntService;

    @Operation(summary = "根據主鍵 新增或更新 Clnt",
               description = "根據主鍵，若有資料則更新，無資料則新增",
               operationId = "save")
    @PostMapping("/save")
    public ResponseEntity<Clnt> save(@RequestBody Clnt entity) {
        Clnt savedEntity = clntService.save(entity);
        return ResponseEntity.ok(savedEntity);
    }

    @Operation(summary = "根據主鍵 大量 新增或更新 Clnt",
               description = "根據主鍵，若有資料則更新，無資料則新增",
               operationId = "saveAll")
    @PostMapping("/saveAll")
    public ResponseEntity<List<Clnt>> saveAll(@RequestBody List<Clnt> entityList) {
        List<Clnt> savedEntityList = clntService.saveAll(entityList);
        return ResponseEntity.ok(savedEntityList);
    }

    @Operation(summary = "根據主鍵 查詢 Clnt",
               description = "根據主鍵查詢 Clnt 資料",
               operationId = "findById")
    @PostMapping("/getByIds")
    public ResponseEntity<Clnt> getByIds(@RequestBody Clnt.ClntKey id) {
        Clnt entity = clntService.findById(id);
        if (entity == null) {
            return ResponseEntity.ok(null); // 回傳 HTTP 200 OK 且 資料為 null
        }
        return ResponseEntity.ok(entity);  // 回傳 HTTP 200 OK 和資料
    }

    @Operation(summary = "根據主鍵 刪除 Clnt 資料",
               description = "根據主鍵刪除 Clnt 資料",
               operationId = "deleteById")
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody Clnt.ClntKey id) {
        clntService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
