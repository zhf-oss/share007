package com.zhf.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 资源类型
 */
@Entity
@Table
@Data
public class ArcType implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    /** 资源类型名称. */
    private String name;

    /** 描述. */
    private String remark;

    /** 排序.（从小到大） */
    private Integer sort;

}
