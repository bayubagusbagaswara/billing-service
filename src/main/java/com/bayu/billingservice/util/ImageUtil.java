package com.bayu.billingservice.util;

import com.bayu.billingservice.dto.ImageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUtil {

    @Value("${base.path.billing.image}")
    private String folderPathImage;

    public ImageDTO getHeaderAndFooterImage() {
        return ImageDTO.builder()
                .imageUrlHeader("file:///" + folderPathImage + "/logo.png")
                .imageUrlFooter("file:///" + folderPathImage + "/footer.png")
                .build();
    }

}
