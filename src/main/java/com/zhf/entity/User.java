package com.zhf.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zhf.enums.UserPointsEnum;
import lombok.Data;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体
 */
@Entity
@Data
@Table
public class User implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    /** 未查看的消息记录数. */
    @Transient
    private Integer messageCount;

    /** 用户名. */
    @NotEmpty(message = "请输入用户名！")
    private String userName;

    /** 密码. */
    @NotEmpty(message = "请输入密码！")
    private String password;

    /** 邮箱，找回密码用. */
    @Email(message="邮箱地址格式有误！")
    @NotEmpty(message="请输入邮箱地址！")
    @Column(length=100)
    private String email;

    /** 用户头像. */
    private String imageName;

    /** 积分，用于下载资源. */
    private Integer points = UserPointsEnum.START.getCode();

    /** 是否是VIP. */
    private boolean isVip = false;

    /** 是否被封禁. */
    private boolean isOff = false;

    /** 角色名称（默认会员）：管理员 会员. */
    private String roleName = "会员";

    /** 注册日期. */
    private Date registerDate;

    /** 是否签到. */
    private boolean isSign = false;

    /** 签到时间. */
    private Date signTime;

    /** 签到排序. */
    private Integer signSort;

    @JsonSerialize(using = CustomDateSerializer.class)
    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

}
