package com.zhf.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 资源实体
 */
@Entity
@Data
@Table
public class Article implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    /** 资源名称. */
    private String name;

    /** 发布日期. */
    private Date publishDate;

    @Transient
    /** 发布日期字符串. */
    private String publishDateStr;

    /** 所属用户. */
    @ManyToOne
    @JoinColumn
    private User user;

    /** 所属资源类别. */
    @ManyToOne
    @JoinColumn
    private ArcType arcType;

    /** 积分. */
    private Integer points;

    /** 资源描述. */
    @Lob
    @Column(columnDefinition = "longtext")
    private String content;

    /** 百度云下载地址. */
    private String download1;

    /** 密码. */
    private String password1;

    /** 是否是热门资源： true是 false否. */
    private boolean isHot = false;

    /** 审核状态：1.未审核 2.审核通过 3.审核未通过. */
    private Integer state;

    /** 审核未通过原因. */
    private String reason;

    /** 审核日期. */
    private Date checkDate;

    /** 链接资源是否有效：true有效 false无效. */
    private boolean isUseful = true;

    /** 访问次数. */
    private Integer view;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public Date getPublishDate() {
        return publishDate;
    }

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public Date getCheckDate() {
        return checkDate;
    }
}
