package com.senjay.archat.common.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.constant.RedisConstant;
import com.senjay.archat.common.exception.UserException;
import com.senjay.archat.common.exception.errorEnums.UserErrorEnum;
import com.senjay.archat.common.properties.JwtProperties;
import com.senjay.archat.common.user.constant.UserConstant;
import com.senjay.archat.common.user.dao.UserDao;
import com.senjay.archat.common.user.domain.dto.*;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.user.domain.vo.request.ModifyNameReq;
import com.senjay.archat.common.user.domain.vo.request.ModifyPwdReq;
import com.senjay.archat.common.user.domain.vo.request.RequestInfo;
import com.senjay.archat.common.user.domain.vo.request.TokenReq;
import com.senjay.archat.common.user.domain.vo.response.UserProfileResp;
import com.senjay.archat.common.user.service.UserService;
import com.senjay.archat.common.util.JwtUtil;
import com.senjay.archat.common.util.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDAO;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        String username = userRegisterDTO.getUsername();
        User existUser = userDAO.lambdaQuery()
                .eq(User::getUsername, username)
                .one();
        if (existUser != null) {
            throw new UserException(UserErrorEnum.USERNAME_EXIST);
        }
        
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            throw new UserException(UserErrorEnum.ACCESS_ERROR);
        }
        
        User user = new User();
        BeanUtil.copyProperties(userRegisterDTO, user);
        userDAO.save(user);
    }

    @Override
    public UserProfileResp login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        
        User user = userDAO.lambdaQuery()
                .eq(User::getUsername, username)
                .one();
        
        if (user == null || !password.equals(user.getPassword())) {
            throw new UserException(UserErrorEnum.ACCESS_ERROR);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userInfo", new RequestInfo(user.getId(), user.getUsername()));
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);
        String tokenKey = RedisConstant.LOGIN_USER_KEY + token;

        stringRedisTemplate.opsForValue().set(tokenKey, token, RedisConstant.LOGIN_USER_TTL, TimeUnit.MILLISECONDS);
        
        return UserProfileResp.builder()
                .uid(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .exp(user.getExp())
                .createTime(user.getCreateTime())
                .token(token)
                .build();
    }

    @Override
    public String logout(TokenReq tokenReq) {
        String tokenKey = RedisConstant.LOGIN_USER_KEY + tokenReq.getToken();
        stringRedisTemplate.delete(tokenKey);
        return "";
    }

    @Override
    public Page<User> listUser(UserSearchDTO userSearchDTO) {
        return userDAO.getBySearch(userSearchDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteById(Long id) {
        boolean isDelete = userDAO.removeById(id);
        return isDelete ? "删除成功" : "";
    }

    @Override
    public String deleteBatch(List<Long> ids) {
        boolean isDelete = userDAO.removeByIds(ids);
        return isDelete ? "删除成功" : "";
    }

    @Override
    public String updateById(UserEditFormDTO userEditFormDTO) {
        User user = new User();
        BeanUtil.copyProperties(userEditFormDTO, user);
        userDAO.saveOrUpdate(user);
        boolean isUpdate = user.getId() != null;
        
        boolean isSuccess = userDAO.saveOrUpdate(user);
        return isSuccess ? (isUpdate ? "更新成功" : "新增成功") : "操作失败";
    }

    @Override
    public List<User> getAll() {
        return userDAO.lambdaQuery().list();
    }

    @Override
    public String modifyName(ModifyNameReq modifyNameReq) {
        String username = modifyNameReq.getUsername();
        if (StringUtils.isBlank(username)) {
            return "";
        }
        Long userId = UserHolder.get().getId();
        userDAO.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getUsername, username)
                .update();
        return username;
    }

    @Override
    public String modifyPwd(ModifyPwdReq modifyPwdReq, String token) {
        String oldPassword = modifyPwdReq.getOldPassword();
        String newPassword = modifyPwdReq.getNewPassword();
        String confirmPassword = modifyPwdReq.getRePassword();

        if (oldPassword.equals(newPassword)) {
            throw new UserException(UserConstant.PWD_REPEAT);
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new UserException(UserConstant.PWD_IS_NOT_CONSISTENT);
        }
        
        Long userId = UserHolder.get().getId();
        String currentPassword = userDAO.lambdaQuery()
                .eq(User::getId, userId)
                .one()
                .getPassword();
        if (!currentPassword.equals(oldPassword)) {
            throw new UserException(UserConstant.PWD_ERROR);
        }
        
        userDAO.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getPassword, newPassword)
                .update();
        stringRedisTemplate.delete(RedisConstant.LOGIN_USER_KEY + token);
        return UserConstant.PWD_MODIFY_SUCCESS;
    }

    @Override
    public void modifyAvatar(ModifyAvatarDTO modifyAvatarDTO) {
        String avatarUrl = modifyAvatarDTO.getAvatar();
        Long userId = UserHolder.get().getId();
        userDAO.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getAvatar, avatarUrl)
                .update();
    }
}
