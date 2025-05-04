package xyz.cngo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.model.UserModel;

import java.util.Objects;

@Data
@TableName("t_user_password")
public class UserPasswordEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(condition = SqlCondition.EQUAL)
    private Integer userId;

    private String password;

    public static UserPasswordEntity convertFromModel(UserModel userModel) {
        if(Objects.isNull(userModel)){
            return null;
        }
        UserPasswordEntity userPasswordEntity = new UserPasswordEntity();
        BeanUtils.copyProperties(userModel, userPasswordEntity);
        return userPasswordEntity;
    }
}