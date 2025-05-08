package xyz.cngo.controller;

import org.springframework.web.bind.annotation.*;
import xyz.cngo.common.checker.ParamCheck;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.error.EmBusinessError;
import xyz.cngo.common.response.CommonReturnType;
import xyz.cngo.dto.ProductUpdateInfoDTO;
import xyz.cngo.model.ProductModel;
import xyz.cngo.model.UserModel;
import xyz.cngo.service.ProductService;
import xyz.cngo.service.UserService;
import xyz.cngo.viewobject.ProductVO;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController("ProductController")
@RequestMapping("/product")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
public class ProductController extends BaseController{
    @Resource
    private ProductService productService;
    @Resource
    private UserService userService;

    /**
     * 获取产品列表
     * @param page
     * @param pageSize
     * @param sort
     * @return
     */
    @GetMapping("/list")
    public CommonReturnType listProducts(
            @RequestParam(value = "page", required = false, defaultValue = "1")  Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sort", required = false, defaultValue = "newest") String sort
    ){
        Integer total = productService.countActiveProducts();
        List<ProductModel> productModelList;
        if(Objects.isNull(page) || page.equals(0)){
            productModelList = productService.getAllProducts();
        }else{
            productModelList = productService.getProductsByPage(page, pageSize, sort);
        }

        List<ProductVO> productVOList = productModelList.stream()
                .map(ProductVO::convertFromModel)
                .collect(Collectors.toList());

//        productVOList.forEach(m -> {
//            System.out.println(m.toString());
//        });

        return CommonReturnType.create(
                Map.of("total", total,
                        "list", productVOList));
    }

    /**
     * 获取当前用户的产品列表
     * @return
     * @throws BusinessException
     */
    @GetMapping("/list-user-products")
    public CommonReturnType listUserProducts() throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        List<ProductModel> productModelList = productService.getUserProducts(userModel);
        List<ProductVO> productVOList = productModelList.stream()
                .map(ProductVO::convertFromModel)
                .collect(Collectors.toList());

        return CommonReturnType.create(productVOList);
    }

    /**
     * 查看产品详情
     * @param productId
     * @return
     */
    @GetMapping("/view")
    public CommonReturnType viewProduct(
            @RequestParam("productId") @ParamCheck(name = "商品ID", min = 1) Integer productId
    ) throws BusinessException {
        ProductModel productModel = productService.getProductById(productId);
        ProductVO productVO = ProductVO.convertFromModel(productModel);
        return CommonReturnType.create(productVO);
    }

    /**
     * 创建新产品
     * @param title
     * @param description
     * @param price
     * @param images
     * @param stock
     * @return
     * @throws BusinessException
     */
    @PostMapping("/create")
    public CommonReturnType createProduct(
            @RequestParam("title") @ParamCheck(name = "商品名称", minLength = 3) String title,
            @RequestParam("desc") @ParamCheck(name = "商品描述", minLength = 20, maxLength = 1000) String description,
            @RequestParam("price")  @ParamCheck(name = "商品价格", min = 0) BigDecimal price,
            @RequestParam("images") @ParamCheck(name = "商品图片链接", regex = BaseController.IMAGE_LINK_REGEX) String images,
            @RequestParam("stock") @ParamCheck(name = "商品库存", min = 0) Integer stock
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();

        ProductModel productModel = new ProductModel();
        productModel.setUserId(userModel.getUserId());
        productModel.setTitle(title);
        productModel.setDescription(description);
        productModel.setPrice(price);
        productModel.setImages(images); //现在这里只是单张图片的逻辑
        productModel.setStock(stock);

        productService.createProduct(productModel);

        return CommonReturnType.create(null);
    }

    /**
     * 上架和下架产品接口
     * @param productId
     * @param status
     * @return
     * @throws BusinessException
     */
    @PostMapping("/publish")
    public CommonReturnType publishProduct(
            @RequestParam("productId") @ParamCheck(name = "商品ID", min = 1) Integer productId,
            @RequestParam("status") @ParamCheck(name = "商品状态", regex = BaseController.PRODUCT_STATUS_REGEX) String status
    ) throws BusinessException {
        if(!"active".equals(status) && !"inactive".equals(status)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "状态参数错误");
        }
        UserModel userModel = userService.getCurrentLoggedInUser();
        String result = productService.publishProduct(userModel, productId, status);
        return CommonReturnType.create(result);
    }

    /**
     * 更新产品信息
     * @param productId
     * @param title
     * @param description
     * @param price
     * @param images
     * @param status
     * @return
     * @throws BusinessException
     */
    @PutMapping("/update")
    public CommonReturnType updateProduct(
           @RequestParam(name="productId") @ParamCheck(name = "商品ID", min = 1) Integer productId,
           @RequestParam(name="title") @ParamCheck(name = "商品名称", minLength = 3) String title,
           @RequestParam(name="desc") @ParamCheck(name = "商品描述", minLength = 20, maxLength = 1000) String description,
           @RequestParam(name="price") @ParamCheck(name = "商品价格", min = 0.01) BigDecimal price,
           @RequestParam(name="images") @ParamCheck(name = "商品图片链接", regex = BaseController.IMAGE_LINK_REGEX) String images,
           @RequestParam(name="status") @ParamCheck(name = "商品状态", regex = BaseController.PRODUCT_STATUS_REGEX) String status
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        ProductUpdateInfoDTO dto = new ProductUpdateInfoDTO(productId, title, description, price, images, status);
        productService.updateProduct(userModel, dto);
        return CommonReturnType.create(true);
    }

    /**
     * 删除产品
     * @param productId
     * @return
     * @throws BusinessException
     */
    @DeleteMapping("/remove")
    public CommonReturnType removeProduct(
            @RequestParam("productId") @ParamCheck(name = "商品ID", min = 1) Integer productId
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        productService.deleteProduct(userModel, productId);
        return CommonReturnType.create(null);
    }

    @PutMapping("/updateStock")
    public CommonReturnType updateStock(
            @RequestParam("productId") @ParamCheck(name = "商品ID", min = 1) Integer productId,
            @RequestParam("newStock") @ParamCheck(name = "商品库存", min = 0) Integer newStock
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        // 手动更新需要验证
        productService.updateProductStock(userModel, productId, newStock);
        return CommonReturnType.create(null);
    }

}
