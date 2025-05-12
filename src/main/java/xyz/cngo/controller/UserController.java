package xyz.cngo.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.cngo.common.checker.ParamCheck;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.error.EmBusinessError;
import xyz.cngo.common.response.CommonReturnType;
import xyz.cngo.common.utils.DateTimeUtil;
import xyz.cngo.common.utils.EmailUtil;
import xyz.cngo.common.utils.GenerateUtil;
import xyz.cngo.common.utils.PasswordUtil;
import xyz.cngo.dto.UserUpdateProfileDTO;
import xyz.cngo.model.UserModel;
import xyz.cngo.service.UserService;
import xyz.cngo.viewobject.UserInfoVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

@RestController("UserController")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
public class UserController extends BaseController{
    @Resource
    UserService userService;
    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    private HttpServletResponse httpServletResponse;


    /**
     * 用户注册接口
     * @param username
     * @param email
     * @param phone
     * @param gender
     * @param age
     * @param avatar
     * @param password
     * @param verificationCode
     * @return
     */
    @PostMapping("/register")
    public CommonReturnType register(
            @RequestParam(name="username") @ParamCheck(name = "用户名", minLength = 2, maxLength = 20) String username,
            @RequestParam(name="email") @ParamCheck(name = "邮箱", regex = BaseController.EMAIL_REGEX) String email,
            @RequestParam(name="phone") @ParamCheck(name = "手机号", regex = BaseController.PHONE_REGEX) String phone,
            @RequestParam(name="age") @ParamCheck(name = "年龄", min = 3, max = 120) Integer age,
            @RequestParam(name="gender") @ParamCheck(name = "性别", regex = BaseController.GENDER_REGEX) String gender,
            @RequestParam(name="avatar") @ParamCheck(name = "头像链接", regex = BaseController.IMAGE_LINK_REGEX) String avatar,
            @RequestParam(name="password") @ParamCheck(name = "密码", minLength = 6, maxLength = 18) String password,
            @RequestParam(name="verificationCode") @ParamCheck(name = "验证码", minLength = 6, maxLength = 6) String verificationCode
    ) throws BusinessException {
        // 检查邮件验证码是否匹配，不匹配会自动抛出异常
        checkVeridationCode(verificationCode);

        UserModel userModel = new UserModel();
        userModel.setUsername(username);
        userModel.setEmail(email);
        userModel.setAge(age);
        userModel.setAvatar(avatar);
        userModel.setPassword(PasswordUtil.hashPassword(password));
        userModel.setPhone(phone);
        userModel.setGender(gender);

        userService.registerUser(userModel);

        return CommonReturnType.create(true);
    }

    /**
     * 登陆接口
     * @param email
     * @param password
     * @return
     * @throws BusinessException
     */
    @PostMapping("/login")
    public CommonReturnType login(
            @RequestParam(name="email") @ParamCheck(name = "邮箱", regex = BaseController.EMAIL_REGEX) String email,
            @RequestParam(name="password") @ParamCheck(name = "密码", minLength = 6, maxLength = 18) String password
    ) throws BusinessException {
        //用户登陆服务，用来校验用户登陆是否合法
        UserModel userModel = userService.validateLoginCredentials(email, password);

        //将登陆凭证加入到用户登陆成功的session内
        httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);
        // 设置SameSite=None的Cookie - 兼容旧版Servlet API的方式
        String sessionId = httpServletRequest.getSession().getId();
        String cookieValue = String.format("JSESSIONID=%s; Path=/; HttpOnly; Secure; SameSite=None", sessionId);
        httpServletResponse.addHeader("Set-Cookie", cookieValue);

        Map<String, Object> map = Map.of(
                "status", userModel.getStatus(),
                "avatar", userModel.getAvatar());
        return CommonReturnType.create(map);
    }

    /**
     * 退出登录接口
     * @return
     */
    @PostMapping("/logout")
    public CommonReturnType logout() throws BusinessException {
        httpServletRequest.getSession().removeAttribute("IS_LOGIN");
        httpServletRequest.getSession().removeAttribute("LOGIN_USER");
        httpServletResponse.setHeader("Set-Cookie", "");
        return CommonReturnType.create(true);
    }

    /**
     * 用户查看账户信息接口
     * @return
     */
    @GetMapping("/profile")
    public CommonReturnType getProfile() throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        UserInfoVO userInfoVO = UserInfoVO.convertFromModel(userModel);
        return CommonReturnType.create(userInfoVO);
    }

    /**
     * 用户修改账户信息接口
     * @param username
     * @param phone
     * @param age
     * @param gender
     * @param avatar
     * @return
     */
    @PutMapping("/profile")
    public CommonReturnType updateProfile(
           @RequestParam(name="username") @ParamCheck(name = "用户名", minLength = 2, maxLength = 20) String username,
           @RequestParam(name="phone") @ParamCheck(name = "手机号", regex = BaseController.PHONE_REGEX) String phone,
           @RequestParam(name="age") @ParamCheck(name = "年龄", min = 3, max = 120) Integer age,
           @RequestParam(name="gender") @ParamCheck(name = "性别", regex = BaseController.GENDER_REGEX) String gender,
           @RequestParam(name="avatar") @ParamCheck(name = "头像链接", regex = BaseController.IMAGE_LINK_REGEX) String avatar
    ) throws BusinessException {
        UserUpdateProfileDTO dto = new UserUpdateProfileDTO(username, phone, age, gender, avatar);
        UserModel userModel = userService.getCurrentLoggedInUser();
        userModel = userService.updateUserProfile(userModel, dto);
        httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);
        UserInfoVO userInfoVO = UserInfoVO.convertFromModel(userModel);
        return CommonReturnType.create(userInfoVO);
    }

    /**
     * 用户修改密码接口
     * @param oldPassword
     * @param newPassword
     * @return
     * @throws BusinessException
     */
    @PutMapping("/password")
    public CommonReturnType updatePassword(
            @RequestParam("oldPassword") @ParamCheck(name = "旧密码", minLength = 6, maxLength = 18) String oldPassword,
            @RequestParam("newPassword") @ParamCheck(name = "新密码", minLength = 6, maxLength = 18) String newPassword
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        userModel = userService.updateUserPassword(userModel, oldPassword, newPassword);
        httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);
        //修改密码会退出登陆
        return logout();
    }

    /**
     * 用户重置密码接口
     * @param verificationCode
     * @param newPassword
     * @return
     */
    @PostMapping("/reset-password")
    public CommonReturnType resetPassword(
            @RequestParam("email") @ParamCheck(name = "邮箱", regex = BaseController.EMAIL_REGEX) String email,
            @RequestParam("verificationCode") @ParamCheck(name = "验证码", minLength = 6, maxLength = 6) String verificationCode,
            @RequestParam("newPassword") @ParamCheck(name = "密码", minLength = 6, maxLength = 18) String newPassword
    ) throws BusinessException {
        // 检查邮件验证码是否匹配，不匹配会自动抛出异常
        checkVeridationCode(verificationCode);
        userService.resetUserPassword(email, newPassword);
        return CommonReturnType.create(null);
    }

    /**
     * 发送验证码接口
     * @param email
     * @return
     * @throws BusinessException
     */
    @PostMapping("/send-verification-code")
    public CommonReturnType sendVerificationCode(
            @RequestParam(name="email") @ParamCheck(name = "邮箱", regex = BaseController.EMAIL_REGEX) String email
    ) throws BusinessException {
        if(StringUtils.isEmpty(email)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        // 这步本来应该在service中调用
        String code = GenerateUtil.generateVerificationCode();
        // 获取当前时间
        String currentTime = DateTimeUtil.getNowTimeStr();

        // 验证码和session绑定
        httpServletRequest.getSession().setAttribute("VerificationCode", code);
        httpServletRequest.getSession().setAttribute("currentTime", currentTime);
        String sessionId = httpServletRequest.getSession().getId();
        String cookieValue = String.format("JSESSIONID=%s; Path=/; HttpOnly; Secure; SameSite=None", sessionId);
        httpServletResponse.addHeader("Set-Cookie", cookieValue);

        // 这步本来也该在service中调用
        EmailUtil.sendVerificationCode(email, code, currentTime);
        System.out.println(email + "=>" + code);
        return CommonReturnType.create(true);
    }

    /**
     * 用户查看验证码
     * 本来此接口不应存在，但是由于邮件api经常出问题，使用此方法作为备份
     * @return
     * @throws BusinessException
     */
    @GetMapping("/get-verification-code")
    public CommonReturnType getVerificationCode() throws BusinessException {
        String code = (String) httpServletRequest.getSession().getAttribute("VerificationCode");
        String time = (String) httpServletRequest.getSession().getAttribute("currentTime");
        return CommonReturnType.create(Map.of("verificationCode", code, "createTime", time));
    }

    /**
     * 校验验证码的有效性
     * @param veridationCode
     * @throws BusinessException
     */
    private void checkVeridationCode(String veridationCode) throws BusinessException {
        String insessionVeridationCode = (String) httpServletRequest.getSession().getAttribute("VerificationCode");
        String createTimeStr = (String) httpServletRequest.getSession().getAttribute("currentTime");

        if(DateTimeUtil.isVerficationCodeExpired(createTimeStr)){
            throw new BusinessException(EmBusinessError.VERIFICATION_CODE_EXPIRED);
        }

        if(Objects.isNull(insessionVeridationCode) ||
                !insessionVeridationCode.equals(veridationCode)){
            throw new BusinessException(EmBusinessError.VERIFICATION_CODE_ERROR);
        }
    }
}
