package com.zhf.init;

import com.zhf.entity.ArcType;
import com.zhf.entity.Link;
import com.zhf.service.ArcTypeService;
import com.zhf.service.LinkService;
import com.zhf.util.RedisUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 初始化加载数据
 */
@Component
public class InitSystem implements ServletContextListener, ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static Map<Integer, ArcType> arcTypeMap = new HashMap<Integer, ArcType>();

    @Resource
    private RedisUtil<Integer> redisUtil;

    /** 加载数据到application缓存中. */
    public void loadData(ServletContext application){
        ArcTypeService arcTypeService = (ArcTypeService) applicationContext.getBean("arcTypeService");
        LinkService linkService = (LinkService) applicationContext.getBean("linkService");
        List<ArcType> arcTypeList = arcTypeService.listAll(Sort.Direction.DESC, "sort");
        List<Link> allLinkList = linkService.listAll(Sort.Direction.DESC,"sort");
        for (ArcType arcType : arcTypeList) {
            arcTypeMap.put(arcType.getId(), arcType);
        }
        application.setAttribute("allArcTypeList",arcTypeList);
        application.setAttribute("allLinkList",allLinkList);

        /** redis操作. */
        if (redisUtil.get("signTotal") != null) {
            Integer signTotal = (Integer) redisUtil.get("signTotal");
            application.setAttribute("signTotal", signTotal);
        }else{
            redisUtil.set("signTotal", 0);
            application.setAttribute("signTotal", 0);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.loadData(sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
