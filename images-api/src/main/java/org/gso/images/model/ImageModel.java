package org.gso.images.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gso.images.dto.ImageResponseDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "images")
@NoArgsConstructor
@AllArgsConstructor
public class ImageModel {

    @Id
    private String id;
    private String url;
    private String title;
    private String description;

    public ImageResponseDto toDto() {
        return new ImageResponseDto(
                this.id,
                this.url,
                null,
                this.title,
                this.description
        );
    }
}
