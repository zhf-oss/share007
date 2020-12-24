package com.zhf.repository;

import com.zhf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 用户注册Repository接口
 */
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    /** 判断该用户名是否被使用过. */
    public User findByUserName(String userName);

    /** 判断该邮箱是否被使用过. */
    public User findByEmail(String email);

    /** 重置所有签到信息. */
    @Query(value="update user set is_sign=false,sign_sort=null,sign_time=null",nativeQuery=true)
    @Modifying
    public void updateAllSignInfo();
}
