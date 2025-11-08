package com.senjay.archat.common.util.frequencycontrol;


import com.senjay.archat.common.domain.dto.FrequencyControlDTO;
import com.senjay.archat.common.exception.FrequencyControlException;
import com.senjay.archat.common.exception.errorEnums.CommonErrorEnum;
import com.senjay.archat.common.util.AssertUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public abstract class AbstractFrequencyControlService<K extends FrequencyControlDTO> {

//    使用 @PostConstruct 注解，表示Spring 容器在创建子类 Bean 后自动执行此方法。
//    作用是：自动将限流策略实现类注册到工厂 FrequencyControlStrategyFactory 中，按策略名绑定起来。
    @PostConstruct
    protected void registerMyselfToFactory() {
        FrequencyControlStrategyFactory.registerFrequencyController(getStrategyName(), this);
    }

    /**
     *
     * @param frequencyControlMap
     * @param supplier
     * @return
     * @param <T>
     * @throws Throwable
     */
    private <T> T executeWithFrequencyControlMap(Map<String, K> frequencyControlMap, SupplierThrowWithoutParam<T> supplier) throws Throwable {
        if (reachRateLimit(frequencyControlMap)) {
            throw new FrequencyControlException(CommonErrorEnum.FREQUENCY_LIMIT);
        }
        try {
//            执行传入的函数，并将它的返回值返回出去
            return supplier.get();
        } finally {
            //不管限流接口是成功还是失败，都增加次数
            addFrequencyControlStatisticsCount(frequencyControlMap);
        }
    }



    @SuppressWarnings("unchecked")
    public <T> T executeWithFrequencyControlList(List<K> frequencyControlList, SupplierThrowWithoutParam<T> supplier) throws Throwable {
//        如果列表中有任意一个 frequencyControl 的 key 为空，则为 true；否则为 false。
        boolean existsFrequencyControlHasNullKey = frequencyControlList.stream().anyMatch(frequencyControl -> ObjectUtils.isEmpty(frequencyControl.getKey()));
        AssertUtil.isFalse(existsFrequencyControlHasNullKey, "限流策略的Key字段不允许出现空值");

        Map<String, FrequencyControlDTO> frequencyControlDTOMap = frequencyControlList.stream()
                .collect(Collectors.groupingBy(FrequencyControlDTO::getKey,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.get(0))
                ));
        return executeWithFrequencyControlMap((Map<String, K>) frequencyControlDTOMap, supplier);
    }

    public <T> T executeWithFrequencyControl(K frequencyControl, SupplierThrowWithoutParam<T> supplier) throws Throwable {
        return executeWithFrequencyControlList(Collections.singletonList(frequencyControl), supplier);
    }


    @FunctionalInterface
    public interface SupplierThrowWithoutParam<T> {
        T get() throws Throwable;
    }

    @FunctionalInterface
    public interface Executor {


        void execute() throws Throwable;
    }


    protected abstract boolean reachRateLimit(Map<String, K> frequencyControlMap);


    protected abstract void addFrequencyControlStatisticsCount(Map<String, K> frequencyControlMap);


    protected abstract String getStrategyName();

}
