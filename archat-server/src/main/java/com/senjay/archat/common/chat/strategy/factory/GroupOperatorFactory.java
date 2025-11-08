package com.senjay.archat.common.chat.strategy.factory;

import com.senjay.archat.common.chat.domain.enums.RoleEnum;
import com.senjay.archat.common.chat.strategy.Abstract.AbstractGroupOperator;
import com.senjay.archat.common.exception.errorEnums.CommonErrorEnum;
import com.senjay.archat.common.util.AssertUtil;

import java.util.HashMap;
import java.util.Map;

public class GroupOperatorFactory {
    public static final Map<Integer, AbstractGroupOperator> ROLE_OPERATOR_MAP = new HashMap<>();

    public static void registerOperator(Integer roleCode,AbstractGroupOperator operator) {
        ROLE_OPERATOR_MAP.put(roleCode,operator);
    }

    public static AbstractGroupOperator getOperator(RoleEnum roleEnum) {
        AbstractGroupOperator operator = ROLE_OPERATOR_MAP.get(roleEnum.getCode());
//        非空断言校验
        AssertUtil.isNotEmpty(operator, CommonErrorEnum.PARAM_VALID);
        return operator;
    }
}
