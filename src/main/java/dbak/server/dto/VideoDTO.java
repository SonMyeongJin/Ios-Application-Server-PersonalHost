package dbak.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // ✅ JSON에 추가 필드가 있어도 무시하도록 설정
public class VideoDTO {
    private String title;
    private String youtube_url;
    private String artist;

    // 기본 생성자
    public VideoDTO() {
    }

    // 매개변수 있는 생성자
    public VideoDTO(String title, String youtube_url, String artist) {
        this.title = title;
        this.youtube_url = youtube_url;
        this.artist = artist;
    }

    // Getter & Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYoutube_url() {
        return youtube_url;
    }

    public void setYoutube_url(String youtube_url) {
        this.youtube_url = youtube_url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "VideoDTO{" +
                "title='" + title + '\'' +
                ", youtube_url='" + youtube_url + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
