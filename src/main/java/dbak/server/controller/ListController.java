package dbak.server.controller;

import com.myeongjin.DBak.dto.VideoDTO;
import com.myeongjin.DBak.service.ListService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/list")
public class ListController {
    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    @GetMapping("/{artistName}")
    public List<VideoDTO> getVideosByArtist(@PathVariable String artistName) {
        return listService.getVideosByArtist(artistName);
    }
}
