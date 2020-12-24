package com.zhf.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 评论实体
 */
@Entity
@Data
public class Comment implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn
    /** 评论的帖子. */
    private Article article;

    @ManyToOne
    @JoinColumn
    /** 评论人. */
    private User user;

    /** 评论内容. */
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    /** 评论日期. */
    private Date commentDate;

    /** 0.审核状态 1.审核通过 2.审核未通过 */
    private Integer state;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public Date getCommentDate() {
        return commentDate;
    }
}
