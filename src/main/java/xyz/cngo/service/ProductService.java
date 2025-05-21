package xyz.cngo.service;

import org.springframework.transaction.annotation.Transactional;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.dto.ProductUpdateInfoDTO;
import xyz.cngo.entity.ProductStockEntity;
import xyz.cngo.model.ProductModel;
import xyz.cngo.model.ProductStockModel;
import xyz.cngo.model.UserModel;

import java.util.List;

public interface ProductService {
    List<ProductModel> getAllProducts();

    List<ProductModel> getUserProducts(UserModel userModel);

    ProductModel getProductById(Integer productId) throws BusinessException;

    void deleteProduct(UserModel userModel, Integer productId) throws BusinessException;

    String publishProduct(UserModel userModel, Integer productId, String status)  throws BusinessException;

    ProductModel createProduct(ProductModel productModel) throws BusinessException;

    void updateProduct(UserModel userModel, ProductUpdateInfoDTO dto) throws BusinessException;

    ProductStockModel addProductStock(Integer productId, Integer stock, String remark, String orderId) throws BusinessException;

    ProductStockModel reduceProductStock(Integer productId, Integer stock, String remark, String orderId) throws BusinessException;

    List<ProductModel> getProductsByPage(Integer page, Integer pageSize, String sort);

    Integer countActiveProducts();

    ProductStockEntity getProductStockEntityById(Integer productId);

    void updateProductStock(UserModel userModel, Integer productId, Integer newStock) throws BusinessException;
}
