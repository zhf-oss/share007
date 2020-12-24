package com.zhf.service.impl;

import com.zhf.entity.ArcType;
import com.zhf.repository.ArcTypeRepository;
import com.zhf.service.ArcTypeService;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("arcTypeService")
public class ArcTypeServiceImpl implements ArcTypeService {

    @Autowired
    private ArcTypeRepository arcTypeRepository;

    @Override
    public List<ArcType> listAll(Sort.Direction direction, String... properties) {
        Sort sort = Sort.by(direction,properties);
        return arcTypeRepository.findAll(sort);
    }

    @Override
    public ArcType findByOne(Integer id) {
        return arcTypeRepository.findById(id).get();
    }

    @Override
    public List<ArcType> list(Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Pageable pageable=PageRequest.of(page-1, pageSize, direction, properties);
        Page<ArcType> pageArcType = arcTypeRepository.findAll(pageable);
        return pageArcType.getContent();
    }

    @Override
    public Long getTotal() {
        return arcTypeRepository.count();
    }

    @Override
    public ArcType get(Integer id) {
        return arcTypeRepository.findById(id).get();
    }

    @Override
    public void save(ArcType arcType) {
        arcTypeRepository.save(arcType);
    }

    @Override
    public void delete(Integer id) {
        arcTypeRepository.deleteById(id);
    }

    /** 解决Sort排序不能实例化问题. */
    public static Sort by(Sort.Direction direction, String... properties) {
        Assert.notNull(direction, "Direction must not be null!");
        Assert.notNull(properties, "Properties must not be null!");
        Assert.isTrue(properties.length>0,"At least one property must be given!");
        return Sort.by((Sort.Order) Arrays.stream(properties)
                .map(it -> new Sort.Order(direction,it))
                .collect(Collectors.toList()));
    }

}
