package dbak.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myeongjin.DBak.dto.VideoDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ListService {
    private final S3Service s3Service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ListService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public List<VideoDTO> getVideosByArtist(String artistName) {
        // ✅ S3에서 모든 파일 목록 가져오기
        List<String> allFiles = s3Service.listFiles();

        // ✅ 가져온 파일 목록 확인 (디버깅용)
        System.out.println("📂 S3에서 가져온 파일 목록: " + allFiles);

        // ✅ artistName + "_"로 시작하는 파일만 필터링
        List<String> artistFiles = allFiles.stream()
                .filter(fileName -> fileName.startsWith(artistName + "_"))
                .collect(Collectors.toList());

        // ✅ 필터링된 파일 목록 확인
        System.out.println("🎵 필터링된 파일 목록: " + artistFiles);

        // ✅ 파일명에서 숫자 부분(예: "BTS_17.json"에서 17)을 추출하여 오름차순 자연 정렬 적용
        List<String> sortedArtistFiles = artistFiles.stream()
                .sorted(Comparator.comparingInt(this::extractNumber))
                .collect(Collectors.toList());

        // ✅ 정렬된 파일 목록 확인
        System.out.println("🎵 정렬된 파일 목록: " + sortedArtistFiles);

        List<VideoDTO> videos = new ArrayList<>();

        for (String fileName : sortedArtistFiles) {
            String jsonContent = s3Service.getJsonFile(fileName);

            // ✅ JSON 내용 확인 (디버깅용)
            System.out.println("📜 " + fileName + " 내용: " + jsonContent);

            try {
                VideoDTO parsedVideo = objectMapper.readValue(jsonContent, VideoDTO.class);
                videos.add(parsedVideo);
            } catch (IOException e) {
                System.out.println("❌ JSON 파싱 중 오류 발생: " + fileName + " | 오류 메시지: " + e.getMessage());
            }
        }

        // ✅ 최종 반환할 videos 리스트 확인
        System.out.println("🎬 최종 반환 리스트: " + videos);

        return videos;
    }

    /**
     * 파일명에서 언더스코어("_")와 ".json" 사이의 숫자만 추출합니다.
     * 예: "BTS_17.json" → 17
     */
    private int extractNumber(String fileName) {
        // 정규 표현식: (?<=_)(\d+)(?=\.json)
        Pattern pattern = Pattern.compile("(?<=_)(\\d+)(?=\\.json)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                // 파싱 실패 시 0 반환
                return 0;
            }
        }
        return 0;
    }
}
