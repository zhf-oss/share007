package com.zhf.repository;

import com.zhf.entity.ArcType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 资源类型Repository接口
 */
public interface ArcTypeRepository extends JpaRepository<ArcType, Integer>, JpaSpecificationExecutor<ArcType> {

}
