package com.zhf.service;

import com.zhf.entity.Article;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * 资源Serivice接口
 */
public interface ArticleService {

    /** 生成所有帖子信息. */
    public List<Article> listAll();

    /** 根据条件分页查询资源信息. */
    public List<Article> list(Article s_article, Integer page, Integer pageSize, Sort.Direction direction, String... properties);

    /** 根据条件查询总记录数. */
    public Long getTotal(Article s_article);

    /** 根据Id获取实体. */
    public Article get(Integer id);

    /** 用户发布资源. */
    public void save(Article article);

    /** 根据Id删除帖子. */
    public void delete(Integer id);
}
