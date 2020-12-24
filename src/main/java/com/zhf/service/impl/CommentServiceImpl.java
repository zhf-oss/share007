package com.zhf.service.impl;

import com.zhf.entity.Comment;
import com.zhf.repository.CommentRepository;
import com.zhf.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service("commentService")
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public void deleteByCommentId(Integer commentId) {
        commentRepository.deleteByCommentId(commentId);
    }

    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public List<Comment> list(Comment s_comment, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, direction, properties);
        Page<Comment> pageArticle = commentRepository.findAll(new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if(s_comment!=null){
                    if(s_comment.getState()!=null){
                        predicate.getExpressions().add(cb.equal(root.get("state"), s_comment.getState()));
                    }
                    if(s_comment.getArticle()!=null && s_comment.getArticle().getId()!=null){
                        predicate.getExpressions().add(cb.equal(root.get("article").get("id"), s_comment.getArticle().getId()));
                    }
                    if(s_comment.getArticle()!=null && s_comment.getArticle().getUser()!=null && s_comment.getArticle().getUser().getId()!=null){
                        predicate.getExpressions().add(cb.equal(root.get("article").get("user").get("id"), s_comment.getArticle().getUser().getId()));
                    }
                }
                return predicate;
            }
        }, pageable);
        return pageArticle.getContent();
    }

    @Override
    public Long getTotal(Comment s_comment) {
        Long count=commentRepository.count(new Specification<Comment>() {

            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if(s_comment!=null){
                    if(s_comment.getState()!=null){
                        predicate.getExpressions().add(cb.equal(root.get("state"), s_comment.getState()));
                    }
                    if(s_comment.getArticle()!=null && s_comment.getArticle().getId()!=null){
                        predicate.getExpressions().add(cb.equal(root.get("article").get("id"), s_comment.getArticle().getId()));
                    }
                    if(s_comment.getArticle()!=null && s_comment.getArticle().getUser()!=null && s_comment.getArticle().getUser().getId()!=null){
                        predicate.getExpressions().add(cb.equal(root.get("article").get("user").get("id"), s_comment.getArticle().getUser().getId()));
                    }
                }
                return predicate;
            }
        });
        return count;
    }

    @Override
    public void delete(Integer id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment get(Integer id) {
        return commentRepository.findById(id).get();
    }
}
