package com.zhf.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户下载实体
 */
@Entity
@Data
public class UserDownload implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn
    /** 下载的资源 */
    private Article article;

    @ManyToOne
    @JoinColumn
    /** 下载的用户. */
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    /** 下载日期. */
    private Date downloadDate;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public Date getDownloadDate() {
        return downloadDate;
    }
}
