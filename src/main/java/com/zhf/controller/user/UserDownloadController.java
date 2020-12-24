package com.zhf.controller.user;

import com.zhf.entity.User;
import com.zhf.entity.UserDownload;
import com.zhf.service.UserDownloadService;
import com.zhf.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户-用户下载控制器
 */
@Controller
@RequestMapping("/user/userDownload")
public class UserDownloadController {

    @Autowired
    private UserDownloadService userDownloadService;

    /**
     * 判断该用户是否下载过该资源
     * @param id
     * @param session
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/exist")
    public boolean exist(Integer id, HttpSession session)throws Exception{
        User user = (User) session.getAttribute("currentUser");
        Integer count = userDownloadService.getCountByUserIdAndArticleId(user.getId(), id);
        if (count > 0) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 判断用户积分是否足够下载该资源
     * @param points
     * @param session
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/enough")
    public boolean enough(Integer points,HttpSession session)throws Exception{
        User user = (User) session.getAttribute("currentUser");
        if (user.getPoints() >= points) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 分页查询用户下载资源信息
     * @param session
     * @param page
     * @return
     * @throws Exception
     */
    @RequestMapping("/list/{id}")
    public ModelAndView list(HttpSession session, @PathVariable(value="id",required=false)Integer page)throws Exception{
        ModelAndView mav=new ModelAndView();
        User user=(User)session.getAttribute("currentUser");
        UserDownload s_userDownload=new UserDownload();
        s_userDownload.setUser(user);
        List<UserDownload> userDownloadList = userDownloadService.list(s_userDownload, page, 10, Sort.Direction.DESC, "downloadDate");
        Long total = userDownloadService.getTotal(s_userDownload);
        mav.addObject("userDownloadList", userDownloadList);
        mav.addObject("pageCode", PageUtil.genPagination("/user/userDownload/list", total, page, 10, ""));
        mav.addObject("title", "用户已下载支援页面");
        mav.setViewName("user/listUserDownload");
        return mav;
    }
}
