package com.senjay.archat.common.aspect;

import cn.hutool.core.util.StrUtil;

import com.senjay.archat.common.annotation.FrequencyControl;
import com.senjay.archat.common.domain.dto.FrequencyControlDTO;
import com.senjay.archat.common.util.SpElUtils;
import com.senjay.archat.common.util.UserHolder;
import com.senjay.archat.common.util.frequencycontrol.FrequencyControlUtil;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.senjay.archat.common.util.frequencycontrol.FrequencyControlStrategyFactory.TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER;


@Slf4j
@Aspect
@Component
public class FrequencyControlAspect {

    @Around("@annotation(com.senjay.archat.common.annotation.FrequencyControl)||@annotation(com.senjay.archat.common.annotation.FrequencyControlContainer)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        //        获取当前执行的方法 Method 对象；
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
//        读取方法上所有的 @FrequencyControl 注解（支持多个）。
        FrequencyControl[] annotationsByType = method.getAnnotationsByType(FrequencyControl.class);

        Map<String, FrequencyControl> keyMap = new HashMap<>();

        for (int i = 0; i < annotationsByType.length; i++) {
            FrequencyControl frequencyControl = annotationsByType[i];
            // 生成前缀（默认使用 方法签名 + 注解顺序 支持多个注解在同一个方法，防止冲突通过 :index:i 作为区分。
            String prefix = StrUtil.isBlank(frequencyControl.prefixKey()) ? SpElUtils.getMethodKey(method) + ":index:" + i : frequencyControl.prefixKey();
// key : 限流的标识符 ,区分不同用户/请求的限流规则，确保限流逻辑能“按人”、“按 IP”、“按表达式(自定义资源限流！)”生效。
            String key = "";
            // 根据限流目标类型构造 key
            switch (frequencyControl.target()) {
                case EL:
                    key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), frequencyControl.spEl());
                    break;
//                case IP:
//                    key = RequestHolder.get().getIp();
//                    break;
                case UID:
                    key = UserHolder.get().getId().toString();
            }
            keyMap.put(prefix + ":" + key, frequencyControl);
        }
        // 将注解的参数转换为编程式调用需要的参数
        List<FrequencyControlDTO> frequencyControlDTOS = keyMap.entrySet().stream().map(entrySet -> buildFrequencyControlDTO(entrySet.getKey(), entrySet.getValue())).collect(Collectors.toList());
        // 调用编程式注解
        return FrequencyControlUtil.executeWithFrequencyControlList(
                // 限流策略控制器
                TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER,
                // 所有限流规则的参数
                frequencyControlDTOS,
                // ← 传入原方法引用
                // 目标方法的执行逻辑（通过 Lambda 传递）
                joinPoint::proceed
        );
    }

    /**
     * 将注解参数转换为编程式调用所需要的参数
     *
     * @param key              频率控制Key
     * @param frequencyControl 注解
     * @return 编程式调用所需要的参数-FrequencyControlDTO
     */
    private FrequencyControlDTO buildFrequencyControlDTO(String key, FrequencyControl frequencyControl) {
        FrequencyControlDTO frequencyControlDTO = new FrequencyControlDTO();
        frequencyControlDTO.setCount(frequencyControl.count());
        frequencyControlDTO.setTime(frequencyControl.time());
        frequencyControlDTO.setUnit(frequencyControl.unit());
        frequencyControlDTO.setKey(key);
        return frequencyControlDTO;
    }
}
