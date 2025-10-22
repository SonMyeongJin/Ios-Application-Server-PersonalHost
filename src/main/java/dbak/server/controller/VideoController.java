package dbak.server.controller;

import dbak.server.dto.VideoDTO;
import dbak.server.service.VideoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// 통합 컨트롤러: /api/list/{artistName}, /api/script/json 두 엔드포인트 제공
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/api/list/{artistName}")
    public List<VideoDTO> getVideosByArtist(@PathVariable String artistName) {
        return videoService.getVideosByArtist(artistName);
    }

    @GetMapping(value = "/api/script/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getJsonFromS3(@RequestParam String fileName) {
        return videoService.getJsonFile(fileName);
    }
}
