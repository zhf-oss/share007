package com.zhf.service;

import com.zhf.entity.Message;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 用户消息Service接口
 */
public interface MessageService {

    /** 查询某个用户下的所有消息. */
    @Query(value="select count(*) from message where is_see=false and user_id=?1",nativeQuery=true)
    public Integer getCountByUserId(Integer userId);

    /** 修改成已查看状态. */
    @Query(value="update message set is_see=true where user_id=?1",nativeQuery=true)
    @Modifying
    public void updateState(Integer userId);

    /** 添加用户消息. */
    public void save(Message message);

    /** 根据条件分页查询用户消息信息. */
    public List<Message> list(Message s_message, Integer page, Integer pageSize, Sort.Direction direction, String...properties);

    /** 根据条件查询总记录数. */
    public Long getTotal(Message s_message);
}
