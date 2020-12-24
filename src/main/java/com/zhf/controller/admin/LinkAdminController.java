package com.zhf.controller.admin;

import com.zhf.entity.Link;
import com.zhf.init.InitSystem;
import com.zhf.service.LinkService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/link")
public class LinkAdminController {

    @Autowired
    private LinkService linkService;

    @Resource
    private InitSystem initSystem;

    /**
     * 分页查询友情链接
     * @param page
     * @param limit
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"分页查询友情链接"})
    @RequestMapping("/list")
    public Map<String,Object> list(@RequestParam(value = "page",required = false)Integer page, @RequestParam(value = "limit",required = false)Integer limit)throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        List<Link> linkList = linkService.list(page, limit, Sort.Direction.ASC, "sort");
        Long total = linkService.getTotal();
        resultMap.put("code", 0);
        resultMap.put("count", total);
        resultMap.put("data", linkList);
        return resultMap;
    }

    /**
     * 添加或修改友情链接
     * @param link
     * @param request
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = {"添加或者修改友情链接"})
    @ResponseBody
    @RequestMapping("/save")
    public Map<String,Object> save(Link link,HttpServletRequest request)throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        linkService.save(link);
        /** 刷新缓存. */
        initSystem.loadData(request.getServletContext());
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 删除友情链接
     * @param id
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"删除友情链接"})
    @RequestMapping("/delete")
    public Map<String,Object> delete(Integer id,HttpServletRequest request)throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        linkService.delete(id);
        /** 刷新缓存. */
        initSystem.loadData(request.getServletContext());
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 根据id查询友情链接实体
     * @param id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"根据id查询友情链接实体"})
    @RequestMapping("/findById")
    public Map<String,Object> findById(Integer id)throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        Link link = linkService.findById(id);
        resultMap.put("link", link);
        resultMap.put("success", true);
        return resultMap;
    }
}
