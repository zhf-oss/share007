package com.zhf.controller;

import com.zhf.entity.Article;
import com.zhf.service.ArticleService;
import com.zhf.util.KeyUtil;
import com.zhf.util.PageUtil;
import com.zhf.util.RedisUtil;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 首页或者跳转url控制器
 */
@Controller
public class IndexController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisUtil<Article> redisUtil;

    /**
     * 网站根目录请求
     * @return
     */
    @RequestMapping("/")
    public ModelAndView root(HttpServletRequest request){
        /** 用于当单击资源类型时，显示红色标注. */
        request.getSession().setAttribute("tMenu","t_0");
        Article s_article = new Article();
        /** 查询审核通过的资源. */
        s_article.setState(2);
        /** 如果首页资源帖子在redis有缓存，则从redis取。如果没有，则从数据库. */
        List<Article> indexArticleList = null;
        String indexKey = "indexArticle_" + KeyUtil.genUniqueKey();
        if (redisUtil.hasKey(indexKey)) {
            indexArticleList = redisUtil.lGet(indexKey, 0, -1);
        }else{
            indexArticleList = articleService.list(s_article, 1, 20, Sort.Direction.DESC, "publishDate");
            redisUtil.lSet(indexKey, indexArticleList, 60 * 60 * 4);
        }

        Long total = articleService.getTotal(s_article);
        /** 添加热门条件查询. */
        s_article.setHot(true);
        /** 查询审核通过且为热门的资源. */
        /** 如果首页热门资源帖子在redis有缓存，则从redis取。如果没有，则从数据库. */
        List<Article> indexHotArticleList = null;
        String indexHotKey = "indexHotArticle_" + KeyUtil.genUniqueKey();
        if (redisUtil.hasKey(indexHotKey)) {
            indexHotArticleList = redisUtil.lGet(indexHotKey, 0, -1);
        } else {
            indexHotArticleList = articleService.list(s_article, 1, 43, Sort.Direction.DESC, "publishDate");
            redisUtil.lSet(indexHotKey, indexHotArticleList, 60 * 60 * 4);
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("title","首页");
        mav.addObject("articleList",indexArticleList);
        mav.addObject("hotArticleList",indexHotArticleList);
        /** 分页工具. */
        mav.addObject("pageCode", PageUtil.genPagination("article/list",total,1,20,""));
        mav.setViewName("index");
        return mav;
    }

}
