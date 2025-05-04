package xyz.cngo.service;

import xyz.cngo.common.error.BusinessException;
import xyz.cngo.dto.UserUpdateProfileDTO;
import xyz.cngo.model.UserModel;

import java.util.Map;

public interface UserService {
    void registerUser(UserModel userModel) throws BusinessException;

    UserModel validateLoginCredentials(String email, String password) throws BusinessException;

    UserModel updateUserProfile(UserModel userModel, UserUpdateProfileDTO userUpdateProfileDTO);

    UserModel resetUserPassword(String email, String newPassword) throws BusinessException;

    UserModel updateUserPassword(UserModel userModel, String oldPassword, String newPassword) throws BusinessException;

    UserModel getCurrentLoggedInUser() throws BusinessException;
}
