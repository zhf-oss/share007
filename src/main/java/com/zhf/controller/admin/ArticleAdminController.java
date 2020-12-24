package com.zhf.controller.admin;

import com.zhf.entity.Article;
import com.zhf.entity.Message;
import com.zhf.lucene.ArticleIndex;
import com.zhf.service.ArticleService;
import com.zhf.service.CommentService;
import com.zhf.service.MessageService;
import com.zhf.service.UserDownloadService;
import com.zhf.util.DateUtil;
import com.zhf.util.RedisUtil;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/article")
public class ArticleAdminController {

    @Autowired
    private ArticleService articleService;

    @Value("${articleImageFilePath}")
    private String articleImageFilePath;

    @Autowired
    private ArticleIndex articleIndex;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserDownloadService userDownloadService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisUtil<Article> redisUtil;

    /**
     * lucene-生成所有帖子索引
     * @return
     */
    @ResponseBody
    @RequiresPermissions(value={"生成所有帖子索引"})
    @RequestMapping("/genAllIndex")
    public boolean genAllIndex() {
        List<Article> articleList = articleService.listAll();
        for (Article article : articleList) {
            if(!articleIndex.addIndex(article)){
                return false;
            }
        }
        return true;
    }

    /**
     * 分页查询资源帖子信息
     *
     * @param s_article
     * @param page
     * @param limit
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"分页查询资源帖子信息"})
    @RequestMapping("/list")
    public Map<String, Object> list(Article s_article, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "limit", required = false) Integer limit) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<Article> articleList = articleService.list(s_article, page, limit, Sort.Direction.DESC, "publishDate");
        Long total = articleService.getTotal(s_article);
        resultMap.put("code", 0);
        resultMap.put("count", total);
        resultMap.put("data", articleList);
        return resultMap;
    }

    /**
     * 跳转到帖子审核页面
     *
     * @param id
     * @return
     */
    @RequiresPermissions(value = {"跳转到帖子审核页面"})
    @RequestMapping("/toReViewArticlePage/{id}")
    public ModelAndView toReViewArticlePage(@PathVariable("id") Integer id) {
        ModelAndView mav = new ModelAndView();
        Article article = articleService.get(id);
        mav.addObject("article", article);
        mav.addObject("title", "审核帖子");
        mav.setViewName("admin/reviewArticle");
        return mav;
    }

    /**
     * 审核，修改状态
     *
     * @param article
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"修改状态"})
    @RequestMapping("/updateState")
    public Map<String, Object> updateState(Article article) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Article oldArticle = articleService.get(article.getId());
        // todo 添加一个消息模块-->finish
        Message message = new Message();
        message.setUser(oldArticle.getUser());
        message.setPublishDate(new Date());
        if (article.getState() == 2) {
            oldArticle.setState(2);
            articleIndex.addIndex(oldArticle);
            message.setContent("【审核通过】您发布的【"+oldArticle.getName()+"】帖子成功！");
            // todo 删除redis：首页数据的缓存
        } else {
            oldArticle.setState(3);
            oldArticle.setReason(article.getReason());
            message.setContent("【审核失败】您发布的【" + oldArticle.getName() + "】帖子审核失败，原因是" + article.getReason());
        }
        articleService.save(oldArticle);
        messageService.save(message);
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 跳转到修改帖子页面
     *
     * @param id
     * @return
     */
    @RequiresPermissions(value = {"跳转到修改帖子页面"})
    @RequestMapping("/toModifyArticlePage/{id}")
    public ModelAndView toModifyArticlePage(@PathVariable("id") Integer id) {
        ModelAndView mav = new ModelAndView();
        Article article = articleService.get(id);
        mav.addObject("article", article);
        mav.addObject("title", "修改帖子页面");
        mav.setViewName("admin/modifyArticle");
        return mav;
    }

    /**
     * Layui编辑器图片上传处理
     *
     * @param file
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/uploadImage")
    @RequiresPermissions(value = {"图片上传"})
    public Map<String, Object> uploadImage(MultipartFile file) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename(); // 获取文件名
            String suffixName = fileName.substring(fileName.lastIndexOf(".")); // 获取文件的后缀
            String newFileName = DateUtil.getCurrentDateStr() + suffixName; // 新文件名
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(articleImageFilePath + DateUtil.getCurrentDatePath() + newFileName));
            map.put("code", 0);
            map.put("msg", "上传成功");
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("src", "/image/" + DateUtil.getCurrentDatePath() + newFileName);
            map2.put("title", newFileName);
            map.put("data", map2);
        }
        return map;
    }

    /**
     * 修改帖子
     *
     * @param article
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = {"修改帖子"})
    @RequestMapping("/update")
    public ModelAndView update(Article article) throws Exception {
        ModelAndView mav = new ModelAndView();
        Article oldArticle = articleService.get(article.getId());
        oldArticle.setName(article.getName());
        oldArticle.setArcType(article.getArcType());
        oldArticle.setContent(article.getContent());
        oldArticle.setDownload1(article.getDownload1());
        oldArticle.setPassword1(article.getPassword1());
        oldArticle.setPoints(article.getPoints());
        /** 当审核通过的时候，需要更新下lucene索引. */
        if (oldArticle.getState() == 2) {
            // todo 修改Lucene索引-->finish
            articleIndex.updateIndex(oldArticle);
        }
        articleService.save(oldArticle);
        // todo 删除该帖子的redis缓存-->finish
        redisUtil.del("article_" + article.getId());

        mav.addObject("title", "修改帖子成功页面");
        mav.setViewName("admin/modifyArticleSuccess");
        return mav;
    }

    /**
     * 删除单个帖子
     * @param id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"删除帖子"})
    @RequestMapping("/delete")
    public Map<String,Object> delete(Integer id)throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        // todo 删除用户下载帖子信息-->finish
        userDownloadService.deleteByArticleId(id);
        // todo 删除该帖子下的所有评论-->finish
        commentService.deleteByCommentId(id);
        articleService.delete(id);
        // todo 删除索引-->finish
        articleIndex.deleteIndex(String.valueOf(id));
        // todo 删除该帖子的redis缓存-->finish
        redisUtil.del("article_" + id);

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
    @RequiresPermissions(value={"删除帖子"})
    @RequestMapping("/deleteSelected")
    public Map<String,Object> deleteSelected(String ids)throws Exception{
        String[] idsStr = ids.split(",");
        for(int i=0;i<idsStr.length;i++){
            // todo 删除用户下载帖子信息-->finish
            userDownloadService.deleteByArticleId(Integer.parseInt(idsStr[i]));
            // todo 删除该帖子下的所有评论-->finish
            commentService.deleteByCommentId(Integer.parseInt(idsStr[i]));
            articleService.delete(Integer.parseInt(idsStr[i]));
            // todo 删除索引-->finish
            articleIndex.deleteIndex(idsStr[i]);
            // todo 删除该帖子的redis缓存-->finish
            redisUtil.del("article_" + Integer.parseInt(idsStr[i]));
        }
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 修改热门状态
     * @param article
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value={"修改热门状态"})
    @RequestMapping("/updateHotState")
    public Map<String,Object> updateHotState(Article article)throws Exception{
        Map<String,Object> resultMap=new HashMap<>();
        Article oldArticle = articleService.get(article.getId());
        oldArticle.setHot(article.isHot());
        articleService.save(oldArticle);
        // todo 该帖子对应的redis索引
        resultMap.put("success", true);
        return resultMap;
    }
}
