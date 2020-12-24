package com.zhf.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 友情链接实体
 */
@Entity
@Data
@Table
public class Link implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    /** 名称. */
    private String name;

    /** 链接地址. */
    private String url;

    /** 排序. */
    private Integer sort;
}
