package com.example.DummyTalk.Chat.Channel.Dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ImageDto {
    private Long imageId;
    private int channelId;
    private String originalFileName;
    private String filePath;
    private String savedFileName;
    @JsonIgnore
    private MultipartFile[] fileInfo;
    @JsonIgnore
    private List<EmbeddingImageDto> embeddingImageDtoList ;

    public ImageDto(String awsUrl, String originalFileName, String savedFileName) {
        this.filePath = awsUrl;
        this.originalFileName = originalFileName;
        this.savedFileName = savedFileName;
    }
}
