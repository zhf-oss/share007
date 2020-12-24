package com.zhf.controller;

import com.zhf.entity.ArcType;
import com.zhf.entity.Article;
import com.zhf.entity.Comment;
import com.zhf.init.InitSystem;
import com.zhf.lucene.ArticleIndex;
import com.zhf.service.ArticleService;
import com.zhf.service.CommentService;
import com.zhf.util.PageUtil;
import com.zhf.util.RedisUtil;
import com.zhf.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 资源帖子控制器
 */
@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleIndex articleIndex;

    @Autowired
    private RedisUtil<Article> redisUtil;

    /**
     * 分页查询帖子信息
     * @return
     */
    @RequestMapping("/list/{id}")
    public ModelAndView list(@RequestParam(value = "typeId", required = false) Integer typeId, @PathVariable(value = "id", required = false) Integer page, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        Article s_article = new Article();
        /** 审核通过的资源. */
        s_article.setState(2);
        if (typeId == null) {
            mav.addObject("title", "第" + page + "页");
        } else {
            /** 前端传来查询的类型Id，然后去服务器获取到的所有资源类型相匹配. */
            ArcType arcType = InitSystem.arcTypeMap.get(typeId);
            s_article.setArcType(arcType);
            mav.addObject("title", arcType.getName() + "-第" + page + "页");
            /** 用于当单击资源类型时，显示红色标注. */
            request.getSession().setAttribute("tMenu","t_"+typeId);
        }
        /** 获取所有审核通过的资源，且分页显示. */
        List<Article> indexArticleList = articleService.list(s_article, page, 20, Sort.Direction.DESC, "publishDate");
        Long total = articleService.getTotal(s_article);
        mav.addObject("articleList", indexArticleList);
        /** 查询热门资源. */
        s_article.setHot(true);
        mav.addObject("hotArticleList",articleService.list(s_article, 1, 43, Sort.Direction.DESC, "publishDate"));
        StringBuffer param = new StringBuffer();
        if (typeId != null) {
            /** 如果typeId不为空，则查询条件增加 类型. */
            param.append("?typeId=" + typeId);
        }
        mav.addObject("pageCode", PageUtil.genPagination("/article/list", total, page, 20, param.toString()));
        mav.setViewName("index");
        return mav;
    }

    /**
     * 根据id查询帖子详细信息
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/{id}")
    public ModelAndView view(@PathVariable("id")Integer id)throws Exception{
        ModelAndView mav=new ModelAndView();
        Article article=null;
        String key="article_"+id;
        if(redisUtil.hasKey(key)){
            article=(Article) redisUtil.get(key);
        }else{
            article=articleService.get(id);
            redisUtil.set(key, article, 60*60);
        }
        mav.addObject("article", article);
        mav.addObject("title", article.getName());

        List<Article> hotArticleList=null;
        String hKey="hotArticleList_type_"+article.getArcType().getId();
        if(redisUtil.hasKey(hKey)){
            hotArticleList=redisUtil.lGet(hKey, 0, -1);
        }else{
            Article s_article=new Article();
            s_article.setHot(true);
            s_article.setArcType(article.getArcType());
            hotArticleList = articleService.list(s_article, 1, 43, Sort.Direction.DESC,"publishDate");
            redisUtil.lSet(hKey, hotArticleList, 60*60);
        }
        mav.addObject("hotArticleList", hotArticleList);
        Comment s_comment=new Comment();
        s_comment.setArticle(article);
        s_comment.setState(1); // 审核通过的评论信息
        mav.addObject("commentCount", commentService.getTotal(s_comment));
        mav.setViewName("article");
        return mav;
    }

    /**
     * 关键字分词搜索
     * @return
     */
    @RequestMapping("/search")
    public ModelAndView search(String q,@RequestParam(value="page",required=false)String page,HttpServletRequest request)throws Exception{
        request.getSession().setAttribute("tMenu", "t_0");
        if(StringUtil.isEmpty(page)){
            page="1";
        }
        Article s_article=new Article();
        s_article.setHot(true);
        List<Article> hotArticleList = articleService.list(s_article, 1, 43, Sort.Direction.DESC,"publishDate");
        List<Article> articleList = articleIndex.search(q);
        Integer toIndex=articleList.size()>=Integer.parseInt(page)*10?Integer.parseInt(page)*10:articleList.size();
        ModelAndView mav=new ModelAndView();
        mav.addObject("title", q);
        mav.addObject("q",q);
        mav.addObject("articleList",articleList.subList((Integer.parseInt(page)-1)*10, toIndex));
        mav.addObject("resultTotal",articleList.size());
        mav.addObject("hotArticleList", hotArticleList);
        mav.addObject("pageCode", this.genUpAndDownPageCode(Integer.parseInt(page), articleList.size(), q, 10));
        mav.setViewName("result");
        return mav;
    }

    /**
     * 生成上一页，下一页代码
     * @param page
     * @param totalNum
     * @param q
     * @param pageSize
     * @return
     */
    private String genUpAndDownPageCode(Integer page,Integer totalNum,String q,Integer pageSize){
        long totalPage=totalNum%pageSize==0?totalNum/pageSize:totalNum/pageSize+1;
        StringBuffer pageCode=new StringBuffer();
        if(totalPage==0){
            return "";
        }else{
            pageCode.append("<div class='layui-box layui-laypage layui-laypage-default'>");
            if(page>1){
                pageCode.append("<a href='/article/search?page="+(page-1)+"&q="+q+"' class='layui-laypage-prev'>上一页</a>");
            }else{
                pageCode.append("<a href='#' class='layui-laypage-prev layui-disabled'>上一页</a>");
            }
            if(page<totalPage){
                pageCode.append("<a href='/article/search?page="+(page+1)+"&q="+q+"' class='layui-laypage-next'>下一页</a>");
            }else{
                pageCode.append("<a href='#' class='layui-laypage-next layui-disabled'>下一页</a>");
            }
            pageCode.append("</div>");
        }
        return pageCode.toString();
    }

    /**
     * 加载相关资源
     * @param q
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/loadRelatedResources")
    public List<Article> loadRelatedResources(String q)throws Exception{
        if(StringUtil.isEmpty(q)){
            return null;
        }
        List<Article> articleList = articleIndex.searchNoHighLighter(q);
        return articleList;
    }

    /**
     * 帖子查看次数+1
     * @param id
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/updateView")
    public void updateView(Integer id, HttpSession session)throws Exception{
        Article article = articleService.get(id);
        article.setView(article.getView() + 1);
        articleService.save(article);
        int articleView = articleService.get(id).getView();
        session.setAttribute("articleView", articleView);
    }
}
