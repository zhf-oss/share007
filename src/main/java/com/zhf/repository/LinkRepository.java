package com.zhf.repository;

import com.zhf.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 友情链接
 */
public interface LinkRepository extends JpaRepository<Link, Integer>, JpaSpecificationExecutor<Link> {

}
