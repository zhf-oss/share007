package com.zhf.controller;

import com.zhf.entity.Comment;
import com.zhf.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 评论控制器
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 分页查询某个帖子的评论信息
     * @param s_comment
     * @param page
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/list")
    public List<Comment> list(Comment s_comment, @RequestParam(value = "page",required = false)Integer page)throws Exception{
        s_comment.setState(1);
        return commentService.list(s_comment, page, 6, Sort.Direction.DESC, "commentDate");
    }
}
