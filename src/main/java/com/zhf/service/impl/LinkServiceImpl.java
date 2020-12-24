package com.zhf.service.impl;

import com.zhf.entity.Link;
import com.zhf.repository.LinkRepository;
import com.zhf.service.LinkService;
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

@Service("linkService")
public class LinkServiceImpl implements LinkService {

    @Autowired
    private LinkRepository linkRepository;

    @Override
    public List<Link> listAll(Sort.Direction direction, String... properties) {
        Sort sort = Sort.by(direction,properties);
        return linkRepository.findAll(sort);
    }

    @Override
    public List<Link> list(Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, direction, properties);
        Page<Link> linkPage = linkRepository.findAll(pageable);
        return linkPage.getContent();
    }

    @Override
    public Long getTotal() {
        return linkRepository.count();
    }

    @Override
    public Link findById(Integer id) {
        return linkRepository.findById(id).get();
    }

    @Override
    public void save(Link link) {
        linkRepository.save(link);
    }

    @Override
    public void delete(Integer id) {
        linkRepository.deleteById(id);
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
