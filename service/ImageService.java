package hu.progmasters.webshop.service;

import hu.progmasters.webshop.dto.ImageResponseDto;
import hu.progmasters.webshop.dto.ProductListItemDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String saveImageToDb(MultipartFile file) throws IOException;

    ImageResponseDto getImageByid(Long id);

    String addImageToProduct(Long productId, Long imageId);

    ProductListItemDto deleteImageFromProduct(Long productId, Long imageId);

}
