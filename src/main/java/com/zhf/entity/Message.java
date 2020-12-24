package com.zhf.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class Message implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    /** 消息内容. */
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    /** 发布日期. */
    private Date publishDate;

    @ManyToOne
    @JoinColumn
    /** 所属用户. */
    private User user;

    /** 消息是否被查看：0.false 1.true */
  private boolean isSee = false;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public Date getPublishDate() {
        return publishDate;
    }
}
