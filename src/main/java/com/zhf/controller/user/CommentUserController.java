package com.zhf.controller.user;

import com.zhf.entity.Article;
import com.zhf.entity.Comment;
import com.zhf.entity.User;
import com.zhf.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user/comment")
public class CommentUserController {

    @Autowired
    private CommentService commentService;

    /**
     * 保存评论信息
     * @param comment
     * @param session
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/save")
    public Map<String,Object> save(Comment comment, HttpSession session)throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        comment.setCommentDate(new Date());
        comment.setState(0);
        comment.setUser((User) session.getAttribute("currentUser"));
        commentService.save(comment);
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 跳转到评论管理页面
     * @return
     */
    @RequestMapping("/toCommentManagePage")
    public ModelAndView toCommentManagePage(){
        ModelAndView mav=new ModelAndView();
        mav.addObject("title", "评论管理");
        mav.setViewName("user/commentManage");
        return mav;
    }

    /**
     * 根据条件分页查询评论信息
     * @param s_comment
     * @param session
     * @param page
     * @param limit
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/list")
    public Map<String,Object> list(Comment s_comment,HttpSession session,@RequestParam(value="page",required=false)Integer page,@RequestParam(value="limit",required=false)Integer limit)throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        User user = (User) session.getAttribute("currentUser");
        Article article = new Article();
        article.setUser(user);
        s_comment.setArticle(article);
        s_comment.setState(1);
        List<Comment> commentList = commentService.list(s_comment, page, limit, Sort.Direction.DESC, "commentDate");
        Long count = commentService.getTotal(s_comment);
        resultMap.put("code", 0);
        resultMap.put("count", count);
        resultMap.put("data", commentList);
        return resultMap;
    }

    /**
     * 根据Id删除单个评论
     * @param id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/delete")
    public Map<String,Object> delete(Integer id)throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        commentService.delete(id);
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 多选删除
     * @param ids
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/deleteSelected")
    public Map<String,Object> deleteSelected(String ids)throws Exception{
        String[] idsStr = ids.split(",");
        for(int i=0;i<idsStr.length;i++){
            commentService.delete(Integer.parseInt(idsStr[i]));
        }
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("success", true);
        return resultMap;
    }
}
