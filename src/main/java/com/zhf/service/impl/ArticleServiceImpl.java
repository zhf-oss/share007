package com.zhf.service.impl;

import com.zhf.entity.Article;
import com.zhf.repository.ArticleRepository;
import com.zhf.service.ArticleService;
import com.zhf.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service("articleService")
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public List<Article> listAll() {
        return articleRepository.findAll();
    }

    @Override
    public List<Article> list(Article s_article, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        /** 该Pageable的第一页为0，而前端传来的为1，所以需要page-1才能获取到Pageable里第0页的数据. */
        Pageable pageable = PageRequest.of(page - 1, pageSize, direction, properties);
        Page<Article> pageArticle = articleRepository.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if(s_article!=null){
                    if(s_article.getName()!=null){
                        /** 根据资源名称查询. */
                        predicate.getExpressions().add(cb.like(root.get("name"),"%"+s_article.getName().trim()+"%"));
                    }
                    if(s_article.getState()!=null){
                        /** 查询审核通过的资源. */
                        predicate.getExpressions().add(cb.equal(root.get("state"),s_article.getState()));
                    }
                    if(s_article.isHot()){
                        /** 查询热门资源. */
                        predicate.getExpressions().add(cb.equal(root.get("isHot"),1));
                    }
                    if(s_article.getArcType()!=null && s_article.getArcType().getId()!=null){
                        /** 通过类别查询资源. */
                        predicate.getExpressions().add(cb.equal(root.get("arcType").get("id"),s_article.getArcType().getId()));
                    }
                    if (!s_article.isUseful()) {
                        /** 查询没有失效的资源. */
                        predicate.getExpressions().add(cb.equal(root.get("isUseful"),false));
                    }
                    if (s_article.getUser() != null && s_article.getUser().getId() != null) {
                        /** 查询该用户下没有失效的资源. */
                        predicate.getExpressions().add(cb.equal(root.get("user").get("id"),s_article.getUser().getId()));
                    }
                    if (s_article.getUser() != null && StringUtil.isNotEmpty(s_article.getUser().getUserName())) {
                        /** 根据用户查询该用户的帖子信息. */
                        predicate.getExpressions().add(cb.like(root.get("user").get("userName"), "%" + s_article.getUser().getUserName() + "%"));
                    }
                }
                return predicate;
            }
        },pageable);
        return pageArticle.getContent();
    }

    @Override
    public Long getTotal(Article s_article) {
        Long count = articleRepository.count(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if(s_article!=null){
                    if(s_article.getName()!=null){
                        /** 根据资源名称查询. */
                        predicate.getExpressions().add(cb.like(root.get("name"),"%"+s_article.getName().trim()+"%"));
                    }
                    if(s_article.getState()!=null){
                        /** 查询审核通过的资源. */
                        predicate.getExpressions().add(cb.equal(root.get("state"),s_article.getState()));
                    }
                    if(s_article.isHot()){
                        /** 查询热门资源. */
                        predicate.getExpressions().add(cb.equal(root.get("isHot"),1));
                    }
                    if(s_article.getArcType()!=null && s_article.getArcType().getId()!=null){
                        /** 通过类别查询资源. */
                        predicate.getExpressions().add(cb.equal(root.get("arcType").get("id"),s_article.getArcType().getId()));
                    }
                    if (!s_article.isUseful()) {
                        /** 查询没有失效的资源. */
                        predicate.getExpressions().add(cb.equal(root.get("isUseful"),false));
                    }
                    if (s_article.getUser() != null && s_article.getUser().getId() != null) {
                        /** 查询该用户下没有失效的资源. */
                        predicate.getExpressions().add(cb.equal(root.get("user").get("id"),s_article.getUser().getId()));
                    }
                    if (s_article.getUser() != null && StringUtil.isNotEmpty(s_article.getUser().getUserName())) {
                        /** 根据用户查询该用户的帖子信息. */
                        predicate.getExpressions().add(cb.like(root.get("user").get("userName"), "%" + s_article.getUser().getUserName() + "%"));
                    }
                }
                return predicate;
            }
        });
        return count;
    }

    @Override
    public Article get(Integer id) {
        /** 这里findOne()用findById().get()代替. */
        Article article = articleRepository.findById(id).get();
        return article;
    }

    @Override
    public void save(Article article) {
        articleRepository.save(article);
    }

    @Override
    public void delete(Integer id) {
        articleRepository.deleteById(id);
    }

}
