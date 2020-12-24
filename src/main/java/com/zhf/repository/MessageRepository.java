package com.zhf.repository;

import com.zhf.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 用户消息Repository接口
 */
public interface MessageRepository extends JpaRepository<Message, Integer>, JpaSpecificationExecutor<Message> {

    /** 查询某个用户下的所有消息. */
    @Query(value="select count(*) from message where is_see=false and user_id=?1",nativeQuery=true)
    public Integer getCountByUserId(Integer userId);

    /** 修改成已查看状态. */
    @Query(value="update message set is_see=true where user_id=?1",nativeQuery=true)
    @Modifying
    public void updateState(Integer userId);
}
