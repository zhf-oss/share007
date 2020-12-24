package com.zhf.service.impl;

import com.zhf.entity.UserDownload;
import com.zhf.repository.UserDownloadRepository;
import com.zhf.service.UserDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Service("userDownloadService")
@Transactional
public class UserDownloadServiceImpl implements UserDownloadService {

    @Autowired
    private UserDownloadRepository userDownloadRepository;

    @Override
    public Integer getCountByUserIdAndArticleId(Integer userId, Integer articleId) {
        return userDownloadRepository.getCountByUserIdAndArticleId(userId, articleId);
    }

    @Override
    public void deleteByArticleId(Integer articleId) {
        userDownloadRepository.deleteByArticleId(articleId);
    }

    @Override
    public void save(UserDownload userDownload) {
        userDownloadRepository.save(userDownload);
    }

    @Override
    public List<UserDownload> list(UserDownload s_userDownload, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, direction, properties);
        Page<UserDownload> pageUserDownload = userDownloadRepository.findAll(new Specification<UserDownload>() {

            @Override
            public Predicate toPredicate(Root<UserDownload> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if(s_userDownload!=null){
                    if(s_userDownload.getUser()!=null && s_userDownload.getUser().getId()!=null){
                        predicate.getExpressions().add(cb.equal(root.get("user").get("id"), s_userDownload.getUser().getId()));
                    }
                }
                return predicate;
            }
        },pageable);
        return pageUserDownload.getContent();
    }

    @Override
    public Long getTotal(UserDownload s_userDownload) {
        Long count=userDownloadRepository.count(new Specification<UserDownload>() {

            @Override
            public Predicate toPredicate(Root<UserDownload> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if(s_userDownload!=null){
                    if(s_userDownload.getUser()!=null && s_userDownload.getUser().getId()!=null){
                        predicate.getExpressions().add(cb.equal(root.get("user").get("id"), s_userDownload.getUser().getId()));
                    }
                }
                return predicate;
            }
        });
        return count;
    }
}
