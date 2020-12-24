package com.zhf.repository;

import com.zhf.entity.UserDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserDownloadRepository extends JpaRepository<UserDownload, Integer>, JpaSpecificationExecutor<UserDownload> {

    @Query(value = "select count(*) from user_download where user_id=?1 and article_id=?2", nativeQuery = true)
    /** 查询某个用户下载某个资源的次数，防止重复下载扣积分. */
    public Integer getCountByUserIdAndArticleId(Integer userId,Integer articleId);

    @Query(value = "delete from user_download where article_id=?1", nativeQuery = true)
    @Modifying
    /** 根据帖子Id删除该下载信息. */
    public void deleteByArticleId(Integer articleId);
}
