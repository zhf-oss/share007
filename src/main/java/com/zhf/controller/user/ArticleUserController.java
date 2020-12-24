package com.zhf.controller.user;

import com.zhf.entity.Article;
import com.zhf.entity.User;
import com.zhf.entity.UserDownload;
import com.zhf.lucene.ArticleIndex;
import com.zhf.service.ArticleService;
import com.zhf.service.CommentService;
import com.zhf.service.UserDownloadService;
import com.zhf.service.UserService;
import com.zhf.util.DateUtil;
import com.zhf.util.RedisUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户中心
 */
@Controller
@RequestMapping("/user/article")
public class ArticleUserController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDownloadService userDownloadService;

    @Value("${articleImageFilePath}")
    private String articleImageFilePath;

    @Autowired
    private ArticleIndex articleIndex;

    @Autowired
    private RedisUtil<Article> redisUtil;

    @Autowired
    private CommentService commentService;

    /**
     * 发布资源
     * @return
     */
    @RequestMapping("/toPublishArticlePage")
    public ModelAndView toPublishArticlePage() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("title","发布帖子");
        mav.setViewName("user/publishArticle");
        return mav;
    }

    /**
     * 用户添加帖子
     * @param article
     * @param session
     * @return
     */
    @RequestMapping("/add")
    public ModelAndView add(Article article, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        User user = (User) session.getAttribute("currentUser");
        article.setPublishDate(new Date());
        /** 哪个用户发布的资源. */
        article.setUser(user);
        /** 未审核状态. */
        article.setState(1);
        /** 帖子查看次数. */
        article.setView(0);
        articleService.save(article);
        mav.addObject("title","发布帖子成功页面");
        mav.setViewName("user/publishArticleSuccess");
        return mav;
    }

    /**
     * Layui编辑器图片上传处理
     * @param file
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/uploadImage")
    public Map<String,Object> uploadImage(MultipartFile file)throws Exception{
        Map<String,Object> map=new HashMap<String,Object>();
        if(!file.isEmpty()){
            /** 获取文件名. */
            String fileName=file.getOriginalFilename();
            /** 获取文件的后缀. */
            String suffixName=fileName.substring(fileName.lastIndexOf("."));
            /** 新文件名，唯一的图片名. */
            String newFileName= DateUtil.getCurrentDateStr()+suffixName;
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(articleImageFilePath+DateUtil.getCurrentDatePath()+newFileName));
            map.put("code", 0);
            map.put("msg", "上传成功");
            Map<String,Object> map2=new HashMap<String,Object>();
            map2.put("src", "/image/"+DateUtil.getCurrentDatePath()+newFileName);
            map2.put("title", newFileName);
            map.put("data", map2);
        }
        return map;
    }

    /**
     * 用户资源管理
     * @return
     */
    @RequestMapping("/toArticleManagePage")
    public ModelAndView toArticleManagePage() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("title","资源管理");
        mav.setViewName("user/articleManage");
        return mav;
    }

    /**
     * 用户资源管理请求接口
     * @param s_article
     * @param page
     * @param limit
     * @param session
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/list")
    public Map<String,Object> list(@RequestParam(value = "page",required = false)String page, @RequestParam(value = "limit",required = false)String limit,Article s_article,HttpSession session)throws Exception{
        Map<String,Object> resultMap=new HashMap<String,Object>();
        User user=(User) session.getAttribute("currentUser");
        s_article.setUser(user);
        List<Article> articleList = articleService.list(s_article, Integer.parseInt(page), Integer.parseInt(limit), Sort.Direction.DESC, "publishDate");
        Long count = articleService.getTotal(s_article);
        resultMap.put("code", 0);
        resultMap.put("count", count);
        resultMap.put("data", articleList);
        return resultMap;
    }

    /**
     * 帖子修改页面
     * @return
     */
    @RequestMapping("/toModifyArticlePage/{id}")
    public ModelAndView toModifyArticlePage(@PathVariable("id")Integer id){
        ModelAndView mav = new ModelAndView();
        mav.addObject("title", "帖子修改页面");
        mav.addObject("article",articleService.get(id));
        mav.setViewName("user/modifyArticle");
        return mav;
    }

    /**
     * 更新帖子信息
     * @param article
     * @return
     * @throws Exception
     */
    @RequestMapping("/update")
    public ModelAndView update(Article article) throws Exception {
        ModelAndView mav = new ModelAndView();
        /** 修改前的帖子信息. */
        Article oldArticle = articleService.get(article.getId());
        oldArticle.setName(article.getName());
        oldArticle.setArcType(article.getArcType());
        oldArticle.setContent(article.getContent());
        oldArticle.setDownload1(article.getDownload1());
        oldArticle.setPassword1(article.getPassword1());
        oldArticle.setPoints(article.getPoints());
        /** 如果修改审核未通过的帖子，则重新由站长审核. */
        if(oldArticle.getState() == 3){
            /** 设置该帖子为审核状态. */
            oldArticle.setState(1);
        }
        articleService.save(oldArticle);
        /** redis缓存功能. */
        if (oldArticle.getState() == 2) {
            // todo 修改Lucene索引-->finish
            articleIndex.updateIndex(oldArticle);
            // todo redis缓存删除这个缓存-->finish
            redisUtil.del("article_" + oldArticle.getId());
        }
        mav.addObject("title", "修改帖子成功");
        mav.setViewName("user/modifyArticleSuccess");
        return mav;
    }

    /**
     * 根据Id删除帖子
     * @param id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/delete")
    public Map<String, Object> delete(Integer id) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        // todo 删除用户下载帖子信息-->finish
        userDownloadService.deleteByArticleId(id);
        // todo 删除该帖子下的所有评论-->finish
        commentService.deleteByCommentId(id);
        articleService.delete(id);
        // todo 删除索引-->finish
        articleIndex.deleteIndex(String.valueOf(id));
        // todo 删除redis索引
        // todo redis缓存删除这个缓存-->finish
        redisUtil.del("article_" + id);
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 批量删除帖子资源
     * @param ids
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/deleteSelected")
    public Map<String,Object> deleteSelected(String ids)throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        String[] idsStr = ids.split(",");
        for(int i=0;i<idsStr.length;i++){
            // todo 删除用户下载帖子信息-->finish
            userDownloadService.deleteByArticleId(Integer.parseInt(idsStr[i]));
            // todo 删除该帖子下的所有评论-->finish
            commentService.deleteByCommentId(Integer.parseInt(idsStr[i]));
            articleService.delete(Integer.parseInt(idsStr[i]));
            // todo 删除索引-->finish
            articleIndex.deleteIndex(String.valueOf(idsStr[i]));
            // todo 删除redis索引
            // todo redis缓存删除这个缓存-->finish
            redisUtil.del("article_" + Integer.parseInt(idsStr[i]));
        }
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 跳转到资源下载页面
     * @param id
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping("/toDownLoadPage/{id}")
    public ModelAndView toDownLoadPage(@PathVariable("id") Integer id, HttpSession session) throws Exception {
        ModelAndView mav = new ModelAndView();
        UserDownload userDownload = new UserDownload();
        /** 获取当前下载的帖子信息. */
        Article article = articleService.get(id);
        /** 获取当前下载的用户. */
        User user = (User) session.getAttribute("currentUser");
        /** 判断是否下载过. */
        boolean isDownload = false;
        Integer count = userDownloadService.getCountByUserIdAndArticleId(user.getId(), id);
        if (count > 0) {
            isDownload = true;
        }else{
            isDownload = false;
        }

        /** 用户第一次下载，则需要扣除用户积分和给分享帖子的人加积分. */
        if (!isDownload) {
            if (user.getPoints() - article.getPoints() < 0) {
                return null;
            }

            // 扣积分
            user.setPoints(user.getPoints() - article.getPoints());
            userService.save(user);

            // 给分享人加积分
            User articleUser = article.getUser();
            articleUser.setPoints(articleUser.getPoints() + article.getPoints());
            userService.save(articleUser);

            // 保存用户下载信息
            userDownload.setArticle(article);
            userDownload.setUser(user);
            userDownload.setDownloadDate(new Date());
            userDownloadService.save(userDownload);

        }
        mav.addObject("article", articleService.get(id));
        mav.setViewName("user/downloadPage");
        return mav;
    }

    /**
     * 跳转到VIP资源下载页面
     * @param id
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping("/toVipDownLoadPage/{id}")
    public ModelAndView toVipDownLoadPage(@PathVariable("id")Integer id,HttpSession session)throws Exception{
        UserDownload userDownload=new UserDownload();
        Article article=articleService.get(id);

        User user = (User) session.getAttribute("currentUser");

        /** 判断是否为Vip. */
        if(!user.isVip()){
            return null;
        }

        /** 是否下载过. */
        boolean isDownload = false;
        Integer count = userDownloadService.getCountByUserIdAndArticleId(user.getId(), id);
        if (count > 0) {
            isDownload = true;
        } else {
            isDownload = false;
        }

        /** 用户第一次下载，则需要扣除用户积分和给分享帖子的人加积分. */
        if (!isDownload) {
            /** 保存用户下载信息. */
            userDownload.setArticle(article);
            userDownload.setUser(user);
            userDownload.setDownloadDate(new Date());
            userDownloadService.save(userDownload);
        }

        ModelAndView mav=new ModelAndView();
        mav.addObject("article",articleService.get(id));
        mav.setViewName("user/downloadPage");
        return mav;
    }
}
