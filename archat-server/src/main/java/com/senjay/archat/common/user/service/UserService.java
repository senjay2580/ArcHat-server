package com.senjay.archat.common.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.user.domain.dto.*;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.user.domain.vo.request.ModifyNameReq;
import com.senjay.archat.common.user.domain.vo.request.ModifyPwdReq;
import com.senjay.archat.common.user.domain.vo.request.TokenReq;
import com.senjay.archat.common.user.domain.vo.response.UserProfileResp;

import java.util.List;

public interface UserService  {

    void register(UserRegisterDTO userRegisterDTO);

    UserProfileResp login(UserLoginDTO userLoginDTO);

    Page<User> listUser(UserSearchDTO userSearchDTO);


    String deleteById(Long id);

    String deleteBatch(List<Long> ids);

    String updateById(UserEditFormDTO userEditFormDTO);

    List<User> getAll();

    String logout(TokenReq tokenReq);

    String modifyName(ModifyNameReq modifyNameReq);

    String modifyPwd(ModifyPwdReq modifyPwdReq, String token);

    void modifyAvatar(ModifyAvatarDTO modifyAvatarDTO);
}
