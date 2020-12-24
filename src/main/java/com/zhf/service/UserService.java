package com.zhf.service;

import com.zhf.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 用户Service接口
 */
public interface UserService {

    /** 用户添加或修改信息. */
    public void save(User user);

    /** 判断该用户名是否被使用过. */
    public User findByUserName(String userName);

    /** 判断该邮箱是否被使用过. */
    public User findByEmail(String email);

    /** 根据Id获取用户实体. */
    public User findById(Integer id);

    /** 分页查询用户信息. */
    public List<User> list(User s_user ,Integer page, Integer pageSize, Sort.Direction direction, String...properties);

    /** 查询用户总数. */
    public Long getTotal(User s_user);

    /** 根据Id删除用户 */
    public void delete(Integer id);

    /** 重置所有签到信息. */
    @Query(value="update user set is_sign=false,sign_sort=null,sign_time=null",nativeQuery=true)
    @Modifying
    public void updateAllSignInfo();
}
