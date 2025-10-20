package dbak.server.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {
    private final S3Client s3;
    private final String bucketName = "dbakbucket"; // 👉 S3 버킷 이름 입력

    public S3Service() {
        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_2) // AWS 리전 설정
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    // ✅ S3 버킷의 파일 목록을 가져오는 메서드 추가
    public List<String> listFiles() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response result = s3.listObjectsV2(request);

        return result.contents()
                .stream()
                .map(S3Object::key) // S3 객체의 key(파일명)만 추출
                .collect(Collectors.toList());
    }

    public String getJsonFile(String fileName) {
        try (InputStream inputStream = s3.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build());
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            return reader.lines().collect(Collectors.joining("\n")); // JSON 파일 내용을 그대로 반환
        } catch (Exception e) {
            throw new RuntimeException("S3에서 JSON 파일을 가져오는 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
