package com.zhf.service;

import com.zhf.entity.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 评论Service接口
 */
public interface CommentService {

    @Query(value = "delete from comment where article_id=?1", nativeQuery = true)
    @Modifying
    /** 根据帖子Id删除该评论. */
    public void deleteByCommentId(Integer commentId);

    /** 添加或修改评论. */
    public void save(Comment comment);

    /** 分页查询评论. */
    public List<Comment> list(Comment s_comment, Integer page, Integer pageSize, Sort.Direction direction, String... properties);

    /** 获取评论记录数. */
    public Long getTotal(Comment s_comment);

    /** 删除评论. */
    public void delete(Integer id);

    /** 根据Id获取实体. */
    public Comment get(Integer id);
}
