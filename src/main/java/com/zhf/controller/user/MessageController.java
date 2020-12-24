package com.zhf.controller.user;

import com.zhf.entity.Message;
import com.zhf.entity.User;
import com.zhf.service.MessageService;
import com.zhf.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户中心-资源控制器
 */
@Controller
@RequestMapping("/user/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 查看系统消息 状态都改成已经查看
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping("/see")
    public ModelAndView see(HttpSession session)throws Exception {
        ModelAndView mav = new ModelAndView();
        User user = (User) session.getAttribute("currentUser");
        Message s_message = new Message();
        s_message.setUser(user);
        /** 设置消息true，已查看过. */
        messageService.updateState(user.getId());
        user.setMessageCount(0);
        session.setAttribute("currentUser", user);
        List<Message> messageList = messageService.list(s_message, 1, 10, Sort.Direction.DESC, "publishDate");
        Long total = messageService.getTotal(s_message);
        mav.addObject("messageList", messageList);
        mav.addObject("pageCode", PageUtil.genPagination("/user/message/list", total, 1, 10, ""));
        mav.addObject("title", "系统消息页面");
        mav.setViewName("user/listMessage");
        return mav;
    }

    /**
     * 分页查询消息帖子
     * @param session
     * @param page
     * @return
     * @throws Exception
     */
    @RequestMapping("/list/{id}")
    public ModelAndView list(HttpSession session,@PathVariable(value="id",required=false)Integer page)throws Exception{
        ModelAndView mav=new ModelAndView();
        User user=(User)session.getAttribute("currentUser");
        Message s_message=new Message();
        s_message.setUser(user);
        List<Message> messageList = messageService.list(s_message, page, 10, Sort.Direction.DESC, "publishDate");
        Long total = messageService.getTotal(s_message);
        mav.addObject("messageList", messageList);
        mav.addObject("pageCode", PageUtil.genPagination("/user/message/list", total, page, 10, ""));
        mav.addObject("title", "系统消息页面");
        mav.setViewName("user/listMessage");
        return mav;
    }
}
