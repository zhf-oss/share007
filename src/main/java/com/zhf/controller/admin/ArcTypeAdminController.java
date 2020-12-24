package com.zhf.controller.admin;

import com.zhf.entity.ArcType;
import com.zhf.init.InitSystem;
import com.zhf.service.ArcTypeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员-资源控制器
 */
@Controller
@RequestMapping("/admin/arcType")
public class ArcTypeAdminController {

    @Autowired
    private ArcTypeService arcTypeService;

    @Resource
    private InitSystem initSystem;

    /**
     * 分页查询资源类别信息
     * @param page
     * @param limit
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"分页查询资源类别信息"})
    @RequestMapping("/list")
    public Map<String,Object> list(@RequestParam(value = "page",required = false)Integer page,@RequestParam(value = "limit",required = false)Integer limit)throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        List<ArcType> arcTypeList = arcTypeService.list(page, limit, Sort.Direction.ASC, "sort");
        Long total = arcTypeService.getTotal();
        resultMap.put("code", 0);
        resultMap.put("count", total);
        resultMap.put("data", arcTypeList);
        return resultMap;
    }

    /**
     * 添加或修改资源类别信息
     * @param arcType
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"添加或者修改类别信息"})
    @RequestMapping("/save")
    public Map<String,Object> save(ArcType arcType, HttpServletRequest request)throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        arcTypeService.save(arcType);
        /** 刷新缓存. */
        initSystem.loadData(request.getServletContext());
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 删除类别信息
     * @param id
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"删除类别信息"})
    @RequestMapping("/delete")
    public Map<String,Object> delete(Integer id,HttpServletRequest request)throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        arcTypeService.delete(id);
        /** 刷新缓存. */
        initSystem.loadData(request.getServletContext());
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 根据id查询资源类别实体
     * @param id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"根据id查询资源类别实体"})
    @RequestMapping("/findById")
    public Map<String,Object> findById(Integer id)throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        ArcType arcType = arcTypeService.get(id);
        resultMap.put("arcType", arcType);
        resultMap.put("success", true);
        return resultMap;
    }
}
