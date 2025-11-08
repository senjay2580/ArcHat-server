package com.senjay.archat.common.service.Strategy.msg;



import com.senjay.archat.common.exception.errorEnums.CommonErrorEnum;
import com.senjay.archat.common.util.AssertUtil;

import java.util.HashMap;
import java.util.Map;

//  策略模式（Strategy Pattern）注册中心实现 根据 key（消息类型） ：value（相应消息类型的策略处理器）
public class MsgHandlerFactory {
    private static final Map<Integer, AbstractMsgHandler> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer code, AbstractMsgHandler strategy) {
        STRATEGY_MAP.put(code, strategy);
    }

    public static AbstractMsgHandler getStrategyNoNull(Integer code) {
        AbstractMsgHandler strategy = STRATEGY_MAP.get(code);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}
