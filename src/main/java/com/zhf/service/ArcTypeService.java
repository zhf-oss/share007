package com.zhf.service;

import com.zhf.entity.ArcType;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * 资源类型Service接口
 */
public interface ArcTypeService {

    /** 查询所有资源类别. */
    public List<ArcType> listAll(Sort.Direction direction,String... properties);

    public ArcType findByOne(Integer id);

    /**
     * 根据条件分页查询资源类别信息
     * @param page
     * @param pageSize
     * @param direction
     * @param properties
     * @return
     */
    public List<ArcType> list(Integer page, Integer pageSize, Sort.Direction direction, String...properties);

    /**
     * 根据条件查询总记录数
     * @return
     */
    public Long getTotal();

    /**
     * 根据id获取资源类别
     * @param id
     * @return
     */
    public ArcType get(Integer id);

    /**
     * 添加或者修改资源类别
     * @param arcType
     */
    public void save(ArcType arcType);

    /**
     * 根据id删除资源类别
     * @param id
     */
    public void delete(Integer id);
}
