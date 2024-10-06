/*
 * Copyright © Progmasters (QTC Kft.), 2018.
 * All rights reserved. No part or the whole of this Teaching Material (TM) may be reproduced, copied, distributed,
 * publicly performed, disseminated to the public, adapted or transmitted in any form or by any means, including
 * photocopying, recording, or other electronic or mechanical methods, without the prior written permission of QTC Kft.
 * This TM may only be used for the purposes of teaching exclusively by QTC Kft. and studying exclusively by QTC Kft.’s
 * students and for no other purposes by any parties other than QTC Kft.
 * This TM shall be kept confidential and shall not be made public or made available or disclosed to any unauthorized person.
 * Any dispute or claim arising out of the breach of these provisions shall be governed by and construed in accordance with the laws of Hungary.
 */

package hu.progmasters.webshop.service;

import hu.progmasters.webshop.domain.Product;
import hu.progmasters.webshop.dto.ProductCreationDataDto;
import hu.progmasters.webshop.dto.ProductInCartDto;
import hu.progmasters.webshop.dto.ProductListItemDto;
import hu.progmasters.webshop.dto.ProductResponseDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {

    Product createNewProduct(ProductCreationDataDto productCreationDataDto);

    List<ProductListItemDto> getAll();

    ProductListItemDto getOne(Long id);


//    List<ProductInCartDto> putProductIntoCart(Long productId, HttpSession session);
//
//    List<ProductInCartDto> getMyCart(HttpSession session);
//
//    List<ProductInCartDto> increaseQuantity(Long id, HttpSession session);
//
//    List<ProductInCartDto> decreaseQuantity(Long id, HttpSession session);
//
//    void emptyCart(HttpSession session);
//
    void deleteProduct(Long productId, HttpSession session);

    ProductResponseDto editProduct(Long id, ProductCreationDataDto requestBody);

    Product updateProduct(Product product);

    Product findProductById(Long id);

}
