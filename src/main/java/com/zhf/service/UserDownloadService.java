package com.zhf.service;

import com.zhf.entity.UserDownload;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 用户下载Service接口
 */
public interface UserDownloadService {

    @Query(value = "select count(*) from user_download where user_id=?1 and article_id=?2", nativeQuery = true)
    /** 查询某个用户下载某个资源的次数，防止重复下载扣积分. */
    public Integer getCountByUserIdAndArticleId(Integer userId, Integer articleId);

    @Query(value = "delete from user_download where article_id=?1", nativeQuery = true)
    @Modifying
    /** 根据帖子Id删除该下载信息. */
    public void deleteByArticleId(Integer articleId);

    /** 添加或修改用户下载信息. */
    public void save(UserDownload userDownload);

    /** 根据条件分页查询用户下载信息. */
    public List<UserDownload> list(UserDownload s_userDownload, Integer page, Integer pageSize, Sort.Direction direction, String...properties);

    /** 根据条件查询总记录数. */
    public Long getTotal(UserDownload s_userDownload);
}
