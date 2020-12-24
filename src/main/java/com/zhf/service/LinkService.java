package com.zhf.service;

import com.zhf.entity.Link;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * 友情链接Service接口
 */
public interface LinkService {

    /** 查询所有友情链接. */
    public List<Link> listAll(Sort.Direction direction, String... properties);

    /** 分页查询友情链接. */
    public List<Link> list(Integer page, Integer pageSize, Sort.Direction direction, String...properties);

    /** 查询友情链接总记录数. */
    public Long getTotal();

    /** 根据Id查询友情链接. */
    public Link findById(Integer id);

    /** 修改或添加友情链接. */
    public void save(Link link);

    /** 根据Id删除友情链接》 */
    public void delete(Integer id);
}
