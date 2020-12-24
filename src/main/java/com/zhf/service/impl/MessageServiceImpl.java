package com.zhf.service.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.zhf.entity.Message;
import com.zhf.repository.MessageRepository;
import com.zhf.service.MessageService;


@Service("messageService")
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Integer getCountByUserId(Integer userId) {
        return messageRepository.getCountByUserId(userId);
    }

    @Override
    public void updateState(Integer userId) {
        messageRepository.updateState(userId);
    }

    @Override
    public void save(Message message) {
        messageRepository.save(message);
    }

    @Override
    public List<Message> list(Message s_message, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Pageable pageable=PageRequest.of(page-1, pageSize, direction, properties);
        Page<Message> pageMessage = messageRepository.findAll(new Specification<Message>() {

            @Override
            public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if(s_message!=null){
                    if(s_message.getUser()!=null && s_message.getUser().getId()!=null){
                        predicate.getExpressions().add(cb.equal(root.get("user").get("id"), s_message.getUser().getId()));
                    }
                }
                return predicate;
            }
        },pageable);
        return pageMessage.getContent();
    }

    @Override
    public Long getTotal(Message s_message) {
        Long count=messageRepository.count(new Specification<Message>() {

            @Override
            public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if(s_message!=null){
                    if(s_message.getUser()!=null && s_message.getUser().getId()!=null){
                        predicate.getExpressions().add(cb.equal(root.get("user").get("id"), s_message.getUser().getId()));
                    }
                }
                return predicate;
            }
        });
        return count;
    }

}
