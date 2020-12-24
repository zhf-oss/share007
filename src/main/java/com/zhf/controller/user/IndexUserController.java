package com.zhf.controller.user;

import com.zhf.entity.Article;
import com.zhf.entity.User;
import com.zhf.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * 用户页面跳转控制器
 */
@Controller
public class IndexUserController {

    @Autowired
    private ArticleService articleService;

    /**
     * 用户个人中心
     * @param session
     * @return
     */
    @RequestMapping("/toUserCenterPage")
    public ModelAndView toUserCenterPage(HttpSession session){
        ModelAndView mav = new ModelAndView();
        /** 获取当前用户的所有信息. */
        User user = (User) session.getAttribute("currentUser");

        Article s_article = new Article();
        s_article.setUser(user);
        /** false为失效的资源. */
        s_article.setUseful(false);
        Long total = articleService.getTotal(s_article);
        session.setAttribute("unUserFulArticleCount",total);
        mav.addObject("title", "个人中心");
        mav.setViewName("user/userCenter");
        return mav;
    }
}
