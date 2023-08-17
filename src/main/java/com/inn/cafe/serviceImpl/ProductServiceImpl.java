package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.model.Category;
import com.inn.cafe.model.Product;
import com.inn.cafe.repository.ProductRepository;
import com.inn.cafe.service.ProductService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
               if(validProductMap(requestMap, false)){
                  productRepository.save(getProductFromMap(requestMap, false));
                  return CafeUtils.getResponseEntity("Product Added Successfully", HttpStatus.OK);
               }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING8WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private boolean validProductMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name")){
           if(requestMap.containsValue("id") && validateId){
               return true;
           } else if (!validateId) {
               return true;
           }
        }
        return false;
    }
    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Product product = new Product();
        if (isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else {
           product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
              return new ResponseEntity<>(productRepository.getAllProduct(), HttpStatus.OK);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validProductMap(requestMap, true)){
                 Optional<Product> optional = productRepository.findById(Integer.parseInt(requestMap.get("id")));
                 if(!optional.isEmpty()){
                     Product product = getProductFromMap(requestMap, true);
                     product.setStatus(optional.get().getStatus());
                     productRepository.save(product);
                     return CafeUtils.getResponseEntity("Product Updated Successfully", HttpStatus.OK);

                 }
                 else {
                     return CafeUtils.getResponseEntity("Product id does not exist.", HttpStatus.OK);
                 }
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING8WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if(jwtFilter.isAdmin()){
               Optional optional = productRepository.findById(id);
               if (!optional.isEmpty()){
                  productRepository.deleteById(id);
                   return CafeUtils.getResponseEntity("Product Deleted Successfully.", HttpStatus.OK);
               }else {
                   return CafeUtils.getResponseEntity("Product id does not exist.", HttpStatus.OK);
               }
            }else {
               return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING8WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
             if(jwtFilter.isAdmin()){
                 Optional optionalProduct = productRepository.findById(Integer.parseInt(requestMap.get("id")));
                 if(!optionalProduct.isEmpty()){
                    productRepository.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("Product Status Updated Successfully", HttpStatus.OK);
                 }else {
                     return CafeUtils.getResponseEntity("Product id does not exist.", HttpStatus.OK);
                 }

             }else {
                 return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
             }
        }catch (Exception ex){
           ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING8WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try {
            return new ResponseEntity<>(productRepository.getProductByCategory(id), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try {
             return new ResponseEntity<>(productRepository.getProductById(id), HttpStatus.OK);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

   /** Cette version utilise des constantes pour stocker les noms des clés dans la Map, ce qui rend le code plus lisible et évite
    les erreurs de frappe. Elle utilise également l'opérateur ternaire ? : pour définir l'identifiant du produit et le statut en fonction de la valeur de isAdd.
    Enfin, la version utilise le constructeur de Product qui prend tous les attributs comme paramètres pour créer un nouvel objet Product. Cela rend le code plus concis et facilite la compréhension du comportement de la méthode.

    private static final String CATEGORY_ID_KEY = "categoryId";
    private static final String PRODUCT_ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String DESCRIPTION_KEY = "description";
    private static final String PRICE_KEY = "price";

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        int categoryId = Integer.parseInt(requestMap.get(CATEGORY_ID_KEY));
        Category category = new Category(categoryId);

        int productId = isAdd ? Integer.parseInt(requestMap.get(PRODUCT_ID_KEY)) : 0;
        String status = isAdd ? null : "true";

        String name = requestMap.get(NAME_KEY);
        String description = requestMap.get(DESCRIPTION_KEY);
        int price = Integer.parseInt(requestMap.get(PRICE_KEY));

        return new Product(productId, category, name, description, price, status);
    }
**/