package xyz.cngo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.error.EmBusinessError;
import xyz.cngo.dao.UserMapper;
import xyz.cngo.dao.UserPasswordMapper;
import xyz.cngo.dto.UserUpdateProfileDTO;
import xyz.cngo.entity.UserEntity;
import xyz.cngo.entity.UserPasswordEntity;
import xyz.cngo.model.UserModel;
import xyz.cngo.service.UserService;
import xyz.cngo.common.utils.PasswordUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserPasswordMapper userPasswordMapper;
    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    private HttpServletResponse httpServletResponse;

    /**
     * 提供注册服务
     * @param userModel
     * @throws BusinessException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registerUser(UserModel userModel) throws BusinessException {
        // 尝试插入用户表
        UserEntity userEntity = UserEntity.convertFromModel(userModel);
        try {
            int status = userMapper.insert(userEntity);
            if (status == 0) {
                throw new BusinessException(EmBusinessError.USER_ALREADY_REGISTERED);
            }
        } catch (Exception e) {
            throw new BusinessException(EmBusinessError.USER_REGISTER_FAIL);
        }

        // Mybatis-Plus会自动获取自增 ID
        userModel.setUserId(userEntity.getUserId());
        UserPasswordEntity userPasswordEntity = UserPasswordEntity.convertFromModel(userModel);
        userPasswordMapper.insert(userPasswordEntity);
    }

    /**
     * 用户登陆验证服务
     * @param email
     * @param password
     * @return
     * @throws BusinessException
     */
    @Override
    public UserModel validateLoginCredentials(String email, String password) throws BusinessException {
        UserEntity userEntity = getUserEntityByEmail(email);
        if (Objects.isNull(userEntity)) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        UserPasswordEntity userPasswordEntity = getUserPasswordEntityByUserId(userEntity.getUserId());
        if (Objects.isNull(userPasswordEntity) ||
                !PasswordUtil.verifyPassword(password, userPasswordEntity.getPassword())) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return UserModel.convertFromEntity(userEntity, userPasswordEntity);
    }

    /**
     * 用户信息修改服务
     * @param userModel
     * @param userUpdateProfileDTO
     * @return
     */
    @Override
    public UserModel updateUserProfile(UserModel userModel, UserUpdateProfileDTO userUpdateProfileDTO) {
        // 更新用户模型信息
        userModel.setUsername(userUpdateProfileDTO.getUsername());
        userModel.setPhone(userUpdateProfileDTO.getPhone());
        userModel.setAge(userUpdateProfileDTO.getAge());
        userModel.setGender(userUpdateProfileDTO.getGender());
        userModel.setAvatar(userUpdateProfileDTO.getAvatar());

        UserEntity userEntity = UserEntity.convertFromModel(userModel);
        userMapper.updateById(userEntity);
        return userModel;
    }

    /**
     * 重置用户密码服务
     * @param email
     * @param newPassword
     * @return
     * @throws BusinessException
     */
    @Override
    public UserModel resetUserPassword(String email, String newPassword) throws BusinessException {
        UserEntity userEntity = getUserEntityByEmail(email);
        if(Objects.isNull(userEntity)){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        UserPasswordEntity userPasswordEntity = updateUserPasswordEntity(userEntity, newPassword);
        return UserModel.convertFromEntity(userEntity, userPasswordEntity);
    }

    /**
     * 更新用户密码
     * @param userModel
     * @param oldPassword
     * @param newPassword
     * @return
     * @throws BusinessException
     */
    @Override
    public UserModel updateUserPassword(UserModel userModel, String oldPassword, String newPassword) throws BusinessException {
        if(!PasswordUtil.verifyPassword(oldPassword, userModel.getPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserEntity userEntity = UserEntity.convertFromModel(userModel);
        UserPasswordEntity userPasswordEntity = updateUserPasswordEntity(userEntity, newPassword);
        return UserModel.convertFromEntity(userEntity, userPasswordEntity);
    }

    /**
     * 从session中获取保存的已登录用户信息
     * @return
     * @throws BusinessException
     */
    @Override
    public UserModel getCurrentLoggedInUser() throws BusinessException {
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        if(Objects.isNull(isLogin) || !isLogin || Objects.isNull(userModel)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        return userModel;
    }

    /**
     * 根据email查找用户记录
     * @param email
     * @return
     */
    private UserEntity getUserEntityByEmail(String email){
        QueryWrapper<UserEntity> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", email);
        return userMapper.selectOne(userQueryWrapper);
    }

    /**
     * 根据用户id查找密码记录
     * @param userId
     * @return
     */
    private UserPasswordEntity getUserPasswordEntityByUserId(Integer userId){
        QueryWrapper<UserPasswordEntity> passwordQueryWrapper = new QueryWrapper<>();
        passwordQueryWrapper.eq("user_id", userId);
        return userPasswordMapper.selectOne(passwordQueryWrapper);
    }

    /**
     * 私有方法，更新或者插入用户密码记录
     * @param userEntity
     * @param newPassword
     * @return
     */
    private UserPasswordEntity updateUserPasswordEntity(UserEntity userEntity, String newPassword) {
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        UserPasswordEntity userPasswordEntity = getUserPasswordEntityByUserId(userEntity.getUserId());
        if(Objects.isNull(userPasswordEntity)){
            // 插入用户密码记录
            userPasswordEntity = new UserPasswordEntity();
            userPasswordEntity.setUserId(userEntity.getUserId());
            userPasswordEntity.setPassword(hashedPassword);
            userPasswordMapper.insert(userPasswordEntity);
        }else{
            // 更新用户密码记录
            userPasswordEntity.setPassword(hashedPassword);
            userPasswordMapper.updateById(userPasswordEntity);
        }
        return userPasswordEntity;
    }
}
