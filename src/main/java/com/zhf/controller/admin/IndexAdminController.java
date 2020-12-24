package com.zhf.controller.admin;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 后台首页跳转url控制器
 */
@Controller
public class IndexAdminController {

    /**
     * 跳转到管理员主页面
     * @return
     */
    /** 登录的时候shiro会赋于管理员权限，这里进行判断，有该权限才能执行下面的方法. */
    @RequiresPermissions(value = {"进入管理员主页"})
    @RequestMapping("/toAdminUserCenterPage")
    public ModelAndView toAdminUserCenterPage(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("title", "管理员主界面");
        mav.setViewName("admin/adminUserCenter");
        return mav;
    }
}
