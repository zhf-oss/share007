package com.zhf.repository;

import com.zhf.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 评论Repository接口
 */
public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {

    @Query(value = "delete from comment where article_id=?1", nativeQuery = true)
    @Modifying
    /** 根据帖子Id删除该评论. */
    public void deleteByCommentId(Integer commentId);
}
