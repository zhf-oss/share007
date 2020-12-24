package com.zhf.service.impl;

import com.zhf.entity.User;
import com.zhf.repository.UserRepository;
import com.zhf.service.UserService;
import com.zhf.util.StringUtil;
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
import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).get();
    }

    @Override
    public List<User> list(User s_user, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, direction, properties);
        Page<User> pageUser = userRepository.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (s_user != null) {
                    if (StringUtil.isNotEmpty(s_user.getUserName())) {
                        predicate.getExpressions().add(cb.like(root.get("userName"), "%" + s_user.getUserName().trim() + "%"));
                    }
                    if(StringUtil.isNotEmpty(s_user.getEmail())){
                        predicate.getExpressions().add(cb.like(root.get("email"), "%"+s_user.getEmail()+"%"));
                    }
                }
                return predicate;
            }
        }, pageable);
        return pageUser.getContent();
    }

    @Override
    public Long getTotal(User s_user) {
        Long count=userRepository.count(new Specification<User>() {

            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if(s_user!=null){
                    if(StringUtil.isNotEmpty(s_user.getUserName())){
                        predicate.getExpressions().add(cb.like(root.get("userName"), "%"+s_user.getUserName().trim()+"%"));
                    }
                    if(StringUtil.isNotEmpty(s_user.getEmail())){
                        predicate.getExpressions().add(cb.like(root.get("email"), "%"+s_user.getEmail()+"%"));
                    }
                }
                return predicate;
            }
        });
        return count;
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateAllSignInfo() {
        userRepository.updateAllSignInfo();
    }

}
