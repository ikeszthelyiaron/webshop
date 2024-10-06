package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.Image;
import hu.progmasters.webshop.domain.Product;
import hu.progmasters.webshop.dto.ImageResponseDto;
import hu.progmasters.webshop.dto.ProductListItemDto;
import hu.progmasters.webshop.dto.mapper.ImageMapper;
import hu.progmasters.webshop.dto.mapper.ProductMapper;
import hu.progmasters.webshop.exception.ImageNotFoundByIdException;
import hu.progmasters.webshop.exception.NoProductWithSuchIdException;
import hu.progmasters.webshop.exception.ProductListAlreadyContainesImageUrlException;
import hu.progmasters.webshop.repository.ImageRepository;
import hu.progmasters.webshop.repository.ProductRepository;
import hu.progmasters.webshop.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final S3Service s3Service;
    private final ImageMapper imageMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public String saveImageToDb(MultipartFile file) throws IOException {
        String fileUrl = s3Service.uploadFile(file);
        String fileName = file.getOriginalFilename();

        Image image = new Image();
        image.setName(fileName);
        image.setUrl(fileUrl);
        imageRepository.save(image);
        return "Image saved in Db with id " + imageRepository.findById(image.getId()).get().getId();
    }

    @Override
    public ImageResponseDto getImageByid(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Image not found by id:" + id));
        return imageMapper.entityToDto(image);
    }

    @Override
    public String addImageToProduct(Long productId, Long imageId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoProductWithSuchIdException(productId));
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundByIdException(imageId));

        List<String> imageUrl = product.getImageUrl();
        imageUrl.stream()
                .filter(url -> url.equals(image.getUrl()))
                .findAny()
                .ifPresent(url -> {
                    throw new ProductListAlreadyContainesImageUrlException(productId, imageId, url);
                });

        product.getImageUrl().add(image.getUrl());
        productRepository.save(product);
        image.setProduct(product);
        imageRepository.save(image);
        return "Image with id " + imageId + " was added to Product with id " + productId;
    }

    @Override
    public ProductListItemDto deleteImageFromProduct(Long productId, Long imageId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoProductWithSuchIdException(productId));

        Image image = imageRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundByIdException(imageId));
        List<String> imageUrl = product.getImageUrl();
        imageUrl.stream()
                .filter(url -> url.equals(image.getUrl()))
                .findFirst()
                .ifPresent(url -> {
                    product.getImageUrl().remove(url);
                    productRepository.save(product);
                });
        image.setProduct(null);
        imageRepository.save(image);
        return productMapper.entityToDtoList(product);
    }

}
