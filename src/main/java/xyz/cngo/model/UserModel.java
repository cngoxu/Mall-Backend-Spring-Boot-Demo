package xyz.cngo.model;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.entity.UserEntity;
import xyz.cngo.entity.UserPasswordEntity;

import java.util.Date;
import java.util.Objects;

@Data
public class UserModel {
    private Integer userId;

    private String username;

    private String email;

    private String phone;

    private Integer age;

    private String gender;

    private String avatar;

    private String status;

    private Date createdAt;

    private String password;

    public static UserModel convertFromEntity(UserEntity userEntity, UserPasswordEntity userPasswordEntity){
        if(Objects.isNull(userEntity)) return null;
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userEntity, userModel);
        userModel.setPassword(userPasswordEntity.getPassword());
        return userModel;
    }
}
