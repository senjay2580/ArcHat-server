package com.senjay.archat.common.service.Strategy.msg;

import cn.hutool.core.bean.BeanUtil;

import com.senjay.archat.common.chat.dao.MessageDao;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.enums.MessageTypeEnum;
import com.senjay.archat.common.chat.domain.vo.request.ChatMessageReq;
import com.senjay.archat.common.service.adapter.MessageAdapter;
import com.senjay.archat.common.util.AssertUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;

/**
 * Description: 消息处理器的模板抽象父类
 */
public abstract class AbstractMsgHandler<MsgType> {
//    只需要子类 也就是实现类是bean就可以了 接口和抽象类不需要被bean容器管理 !!!!!
    @Autowired
    private MessageDao messageDao;
//  泛型参数 MsgType 的 Class 类型信息

    private Class<MsgType> bodyClass;

    /**
     * Class<?>：普通类，如 String.class, Integer.class*
     * ParameterizedType：带泛型的类型，如 List<String>
     * TypeVariable：类型变量，如 T
     * WildcardType：通配符类型，如 ? extends Number
     * GenericArrayType：泛型数组，如 T[], List<String>[]
      */
//使用 @PostConstruct 注解，表示Spring 容器在创建子类 Bean 后自动执行此方法。 常用于依赖注入完成后的额外初始化操作
    @PostConstruct
    private void init() {
//        获取当前对象的父类泛型类型信息
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
//      getActualTypeArguments() 会返回一个泛型参数数组
//      从父类的泛型信息中反射获取出当前类真正的泛型类型（如 TextMsg），并保存为 Class<MsgType> 类型，以便后续类型转换用。
        this.bodyClass = (Class<MsgType>) genericSuperclass.getActualTypeArguments()[0];
//        在这里注册
        MsgHandlerFactory.register(getMsgTypeEnum().getType(), this);
    }

    /**
     * 消息类型a
     */
//    抽象方法必须实现
    abstract MessageTypeEnum getMsgTypeEnum();
//    具体方法不强制实现
    protected void checkMsg(MsgType body, Long roomId, Long uid) {

    }
//    这里进行校验和保存
// 思想 先统一再特判处理
    @Transactional
    public Long checkAndSaveMsg(ChatMessageReq request, Long uid) {
        MsgType body = this.toBean(request.getBody());
        //统一校验
        AssertUtil.allCheckValidateThrow(body);
        //子类扩展校验
        checkMsg(body, request.getRoomId(), uid);
        //构建消息实体
        Message insert = MessageAdapter.buildMsgSave(request, uid);
        //统一保存
        messageDao.save(insert);
        //子类扩展保存 就是去利用这个 insert 的 id 去更新 这个数据库记录
        saveMsg(insert, body);
        //返回消息Id
        return insert.getId();
    }
//传入的 body Object对象安全地转换为泛型类型 MsgType 的对象
//    body.getClass()：获取传入对象的运行时实际类型；
    private MsgType toBean(Object body) {
        if (bodyClass.isAssignableFrom(body.getClass())) {
            return (MsgType) body;
        }
//        将 Object 类型的 body 对象转换为泛型类型 MsgType 的实例。
        return BeanUtil.toBean(body, bodyClass);
    }

// 子类扩展·保存
    protected abstract void saveMsg(Message message, MsgType body);

//  不同的业务场景下“展示消息”
    /**
     * 展示消息
     */
    public abstract Object showMsg(Message msg);

    /**
     * 被回复时——展示的消息
     */
    public abstract Object showReplyMsg(Message msg);

    /**
     * 会话列表——展示的消息
     */
    public abstract String showContactMsg(Message msg);

}
