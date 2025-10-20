package dbak.server.controller;

import com.myeongjin.DBak.service.S3Service;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/script")
public class S3Controller {
    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getJsonFromS3(@RequestParam String fileName) {
        return s3Service.getJsonFile(fileName);
    }
}
