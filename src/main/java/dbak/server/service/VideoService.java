package dbak.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dbak.server.dto.VideoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

@Service
public class VideoService {
    private final Path baseDir;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 기존 S3Client 제거, 로컬 경로 주입
    public VideoService(@Value("${dbak.json.base-path:${user.home}/document/dbak}") String basePath) {
        this.baseDir = Paths.get(basePath).toAbsolutePath().normalize();
        if (!Files.isDirectory(this.baseDir)) {
            throw new IllegalStateException("JSON 디렉토리가 존재하지 않습니다: " + this.baseDir);
        }
    }

    // 로컬 디렉토리 내 모든 .json 파일 이름 목록 반환
    public List<String> listFiles() {
        try (Stream<Path> stream = Files.list(baseDir)) {
            return stream
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("JSON 파일 목록을 읽는 중 오류: " + e.getMessage(), e);
        }
    }

    // 단일 JSON 파일 내용 문자열 반환
    public String getJsonFile(String fileName) {
        Path file = baseDir.resolve(fileName).normalize();
        if (!file.startsWith(baseDir)) {
            throw new IllegalArgumentException("경로 탈출 감지: " + fileName);
        }
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + file);
        }
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("JSON 파일을 읽는 중 오류: " + e.getMessage(), e);
        }
    }

    // 아티스트명으로 필터한 VideoDTO 리스트 반환 (artist_번호.json 패턴 기준)
    public List<VideoDTO> getVideosByArtist(String artistName) {
        List<String> artistFiles = listFiles().stream()
                .filter(name -> name.startsWith(artistName + "_"))
                .sorted(Comparator.comparingInt(this::extractNumber))
                .collect(Collectors.toList());
        List<VideoDTO> videos = new ArrayList<>();
        for (String fileName : artistFiles) {
            String json = getJsonFile(fileName);
            try {
                videos.add(objectMapper.readValue(json, VideoDTO.class));
            } catch (Exception e) {
                System.out.println("❌ 파싱 오류: " + fileName + " | " + e.getMessage());
            }
        }
        return videos;
    }

    // 파일명에서 숫자 추출 (예: BTS_17.json -> 17)
    private int extractNumber(String fileName) {
        Pattern pattern = Pattern.compile("(?<=_)(\\d+)(?=\\.json)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            try { return Integer.parseInt(matcher.group(1)); } catch (NumberFormatException e) { return 0; }
        }
        return 0;
    }
}
