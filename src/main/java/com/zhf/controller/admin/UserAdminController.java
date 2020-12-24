package com.zhf.controller.admin;

import com.zhf.entity.User;
import com.zhf.service.UserService;
import com.zhf.util.CryptographyUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/user")
public class UserAdminController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户信息
     * @param s_user
     * @param page
     * @param limit
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"分页查询用户信息"})
    @RequestMapping("/list")
    public Map<String, Object> list(User s_user, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "limit", required = false) Integer limit) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<User> userList = userService.list(s_user, page, limit, Sort.Direction.DESC, "registerDate");
        Long total = userService.getTotal(s_user);
        resultMap.put("code", 0);
        resultMap.put("count", total);
        resultMap.put("data", userList);
        return resultMap;
    }

    /**
     * 重置用户密码
     * @param id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"重置用户密码"})
    @RequestMapping("/resetPassword")
    public Map<String,Object> resetPassword(Integer id)throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        User oldUser = userService.findById(id);
        oldUser.setPassword(CryptographyUtil.md5("123456", CryptographyUtil.SALT));
        userService.save(oldUser);
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 用户积分充值
     * @param user
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"用户积分充值"})
    @RequestMapping("/addPoints")
    public Map<String,Object> addPoints(User user)throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        User oldUser = userService.findById(user.getId());
        oldUser.setPoints(oldUser.getPoints() + user.getPoints());
        userService.save(oldUser);
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 修改用户VIP状态
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"修改用户VIP状态"})
    @RequestMapping("/updateVipState")
    public Map<String,Object> updateVipState(User user)throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        User oldUser = userService.findById(user.getId());
        oldUser.setVip(user.isVip());
        userService.save(oldUser);
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 修改用户状态
     * @param user
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value={"修改用户状态"})
    @RequestMapping("/updateUserState")
    public Map<String,Object> updateUserState(User user)throws Exception{
        User oldUser = userService.findById(user.getId());
        oldUser.setOff(user.isOff());
        userService.save(oldUser);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 修改管理员密码
     * @param oldpassword
     * @param password
     * @param session
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequiresPermissions(value = {"修改管理员密码"})
    @RequestMapping("/modifyPassword")
    public Map<String, Object> modifyPassword(String oldpassword, String password, HttpSession session) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        User user = (User) session.getAttribute("currentUser");
        if (!user.getPassword().equals(CryptographyUtil.md5(oldpassword, CryptographyUtil.SALT))) {
            resultMap.put("success", false);
            resultMap.put("errorInfo", "原密码错误！");
            return resultMap;
        }else{
            User oldUser = userService.findById(user.getId());
            oldUser.setPassword(CryptographyUtil.md5(password, CryptographyUtil.SALT));
            userService.save(oldUser);
            resultMap.put("success", true);
            return resultMap;
        }
    }

    /**
     * 安全退出
     * @param session
     * @return
     */
    @RequiresPermissions(value={"安全退出"})
    @RequestMapping("/logout")
    public String logout(HttpSession session){
        /** 清除管理员登录的信息. */
        SecurityUtils.getSubject().logout();
        return "adminLogin";
    }
}
