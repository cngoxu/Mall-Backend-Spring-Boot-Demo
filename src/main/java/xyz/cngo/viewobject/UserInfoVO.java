package xyz.cngo.viewobject;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.model.UserModel;

import java.util.Date;
import java.util.Objects;

@Data
public class UserInfoVO {
    private String username;

    private String email;

    private String phone;

    private Integer age;

    private String gender;

    private String avatar;

    private Date createdAt;

    public static UserInfoVO convertFromModel(UserModel userModel){
        if(Objects.isNull(userModel)){
            return null;
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userModel, userInfoVO);
        return userInfoVO;
    }
}
