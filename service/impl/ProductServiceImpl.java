package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.Category;
import hu.progmasters.webshop.domain.Product;
import hu.progmasters.webshop.dto.ProductCreationDataDto;
import hu.progmasters.webshop.dto.ProductInCartDto;
import hu.progmasters.webshop.dto.ProductListItemDto;
import hu.progmasters.webshop.dto.ProductResponseDto;
import hu.progmasters.webshop.dto.mapper.ProductMapper;
import hu.progmasters.webshop.exception.NoProductWithSuchIdException;
import hu.progmasters.webshop.exception.ProductInCartException;
import hu.progmasters.webshop.repository.CategoryRepository;
import hu.progmasters.webshop.repository.ProductRepository;
import hu.progmasters.webshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;


    @Override
    public Product createNewProduct(ProductCreationDataDto productCreationDataDto) {
        return productRepository.save(productMapper.productCreationDataDtoToEntity(productCreationDataDto));
    }

    @Override
    public List<ProductListItemDto> getAll() {
        List<Product> products = productRepository.findAll();
        List<ProductListItemDto> items = new ArrayList<>();

        products.forEach(product -> items.add(productMapper.entityToDtoList(product)));

        return items;
    }

    @Override
    public ProductListItemDto getOne(Long id) {
        Optional<Product> productFromRepo = productRepository.findById(id);

        if (productFromRepo.isEmpty()) {
            throw new RuntimeException("Product not found by id: " + id);
        }
        return productMapper.entityToDtoList(productFromRepo.get());
    }
//
//    @Override
//    public List<ProductInCartDto> putProductIntoCart(Long productId, HttpSession session) {
//        Product productById = findProductById(productId);
//        List<ProductInCartDto> productsInCart = new ArrayList<>();
//
//        if (session.getAttribute("cart") == null) {
//            productsInCart.add(productMapper.entityToProductInCartDto(productById, 1));
//        } else {
//            try {
//                productsInCart = (List<ProductInCartDto>) session.getAttribute("cart");
//            } catch (ClassCastException e) {
//                throw new RuntimeException();
//            }
//
//            if (!isListContainsProduct(productId, productsInCart)) {
//                productsInCart.add(productMapper.entityToProductInCartDto(productById, 1));
//            }
//            // itt mar adott product letezik a kosarban (List<Dto> -> adott element inmmutable)
//            // productId alapjan megkeresni az adatstrukturaban
//            // ha megvan az adott element abbol kimenteni az amount-ot (valtozoba)
//            // az eredeti Dto torolheto az adatstrukturabol
//            // uj Dto mentese a novelt valtozoval
//            else {
//                int index = findIndexOfProduct(productId, productsInCart);
//                Integer amount = productsInCart.get(index).amount();
//                productsInCart.set(index, productMapper.entityToProductInCartDto(productById, ++amount));
//            }
//        }
//        session.setAttribute("cart", productsInCart);
//
//        return productsInCart;
//    }
//
//    @Override
//    public List<ProductInCartDto> getMyCart(HttpSession session) {
//        List<ProductInCartDto> products = new ArrayList<>();
//
//        try {
//            if (session.getAttribute("cart") != null) {
//                products = (List<ProductInCartDto>) session.getAttribute("cart");
//            }
//        } catch (ClassCastException e) {
//            throw new RuntimeException();
//        }
//        return products;
//    }
//
//    @Override
//    public List<ProductInCartDto> increaseQuantity(Long id, HttpSession session) {
//        List<ProductInCartDto> products;
//        Product product = findProductById(id);
//
//        try {
//            products = (List<ProductInCartDto>) session.getAttribute("cart");
//        } catch (ClassCastException e) {
//            throw new RuntimeException();
//        }
//
//        int index = findIndexOfProduct(id, products);
//        Integer amount = products.get(index).amount();
//        products.set(index, productMapper.entityToProductInCartDto(product, ++amount));
//
//        session.setAttribute("cart", products);
//        return products;
//    }
//
//    @Override
//    public List<ProductInCartDto> decreaseQuantity(Long id, HttpSession session) {
//        List<ProductInCartDto> products;
//        Product product = findProductById(id);
//
//        try {
//            products = (List<ProductInCartDto>) session.getAttribute("cart");
//        } catch (ClassCastException e) {
//            throw new RuntimeException();
//        }
//
//        int index = findIndexOfProduct(id, products);
//        Integer amount = products.get(index).amount();
//
//        if (amount <= 0) {
//            throw new RuntimeException();
//        }
//
//        products.set(index, productMapper.entityToProductInCartDto(product, --amount));
//
//
//        session.setAttribute("cart", products);
//        return products;
//    }
//
//    @Override
//    public void emptyCart(HttpSession session) {
//        if (session.getAttribute("cart") != null) {
//            session.removeAttribute("cart");
//        }
//    }

    @Override
    public ProductResponseDto editProduct(Long id, ProductCreationDataDto requestBody) {
        Optional<Product> productFromRepo = productRepository.findById(id);
        Optional<Category> categoryFromRepo = categoryRepository.findById(requestBody.categoryId());

        if (productFromRepo.isEmpty()) {
            throw new NoProductWithSuchIdException(id);
        }

        if (categoryFromRepo.isEmpty()) {
            throw new RuntimeException("Category with id: " + id + " not found");
        }


        Product product = productFromRepo.get();
        product.setPrice(requestBody.price());
        product.setName(requestBody.name());
        product.setDescription(requestBody.description());
        product.setCategory(categoryFromRepo.get());
        productRepository.save(product);

        return productMapper.entityToProductResponseDto(product);
    }

    public Product findProductById(Long id) {
        Optional<Product> productFromRepo = productRepository.findById(id);

        if (productFromRepo.isEmpty()) {
            throw new NoProductWithSuchIdException(id);
        }
        return productFromRepo.get();
    }

    private boolean isListContainsProduct(Long productId, List<ProductInCartDto> list) {
        return list.stream().anyMatch(p -> p.productId().equals(productId));
    }

    private int findIndexOfProduct(Long productId, List<ProductInCartDto> list) {
        return list.indexOf(list.stream()
                .filter(p -> p.productId().equals(productId))
                .findFirst().get());
    }

    private List<ProductInCartDto> getMyCart(HttpSession session) {
        return null;
    }

    @Override
    public void deleteProduct(Long productId, HttpSession session) {
        List<ProductInCartDto> productsInCart = getMyCart(session);
        if (productAlreadyInCart(productsInCart, productId)) {
            throw new ProductInCartException(productId);
        } else if (productRepository.findById(productId).isEmpty()) {
            throw new NoProductWithSuchIdException(productId);
        } else {
            Product p = productRepository.findById(productId).get();
            Category c = p.getCategory();
            c.removeProduct(p);
            productRepository.deleteById(productId);
        }
    }

    private boolean productAlreadyInCart(List<ProductInCartDto> products, Long productId) {
        for (ProductInCartDto product : products) {
            if (product.productId().equals(productId)) {
                return true;
            }
        }
        return false;
    }

    public Product updateProduct(Product product) {
        Optional<Product> byId = productRepository.findById(product.getId());

        if (byId.isEmpty()) {
            throw new NoProductWithSuchIdException(product.getId());
        }

        return productRepository.save(product);
    }

}
