package xyz.cngo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.model.UserModel;

import java.util.Date;
import java.util.Objects;

@Data
@TableName("t_user")
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Integer userId;

    @TableField(condition = SqlCondition.LIKE)
    private String username;

    @TableField(condition = SqlCondition.LIKE)
    private String email;

    private String phone;

    private Integer age;

    private String gender;

    private String avatar;

    private String status;

//    去掉反而可以插入
//    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    // 枚举类型
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    public static UserEntity convertFromModel(UserModel userModel) {
        if(Objects.isNull(userModel)){
            return null;
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userModel, userEntity);
        return userEntity;
    }
}
