package xyz.cngo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xyz.cngo.entity.ProductEntity;
import xyz.cngo.model.ProductModel;

import java.util.List;

public interface ProductMapper extends BaseMapper<ProductEntity> {

    @Select("SELECT p.*, ps.stock, ps.updated_at " +
            "FROM t_product p " +
            "JOIN t_product_stock ps " +
            "ON p.product_id = ps.product_id")
    List<ProductModel> selectAllProductModels();

    @Select("SELECT p.*, ps.stock, ps.updated_at " +
            "FROM t_product p " +
            "JOIN t_product_stock ps " +
            "ON p.product_id = ps.product_id " +
            "WHERE p.user_id = #{userId} " +
            "ORDER BY p.created_at DESC")
    List<ProductModel> selectProductModelsByUserId(@Param("userId") Integer userId);

    @Select("SELECT p.*, ps.stock " +
            "FROM t_product p " +
            "LEFT JOIN " +
            "t_product_stock ps " +
            "ON p.product_id = ps.product_id " +
            "WHERE p.status = 'active' " +
            "ORDER BY p.${sort} DESC, p.product_id DESC " +
            "LIMIT #{start}, #{pageSize}")
    List<ProductModel> selectProductModelsByPage(@Param("start") int start,
                                             @Param("pageSize") int pageSize,
                                             @Param("sort") String sort);
}
