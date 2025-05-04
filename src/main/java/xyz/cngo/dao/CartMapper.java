package xyz.cngo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xyz.cngo.entity.CartEntity;
import xyz.cngo.model.CartItemModel;

import java.util.List;

public interface CartMapper extends BaseMapper<CartEntity> {
    @Select("SELECT " +
            "p.title, p.description, p.price, p.images, p.status, p.sales, " +
            "c.*, s.stock " +
            "FROM t_cart c " +
            "JOIN t_product p ON c.product_id = p.product_id " +
            "JOIN t_product_stock s ON p.product_id = s.product_id " +
            "WHERE c.user_id = #{userId}")
    List<CartItemModel> getUserCartItems(@Param("userId") Integer userId);
}
