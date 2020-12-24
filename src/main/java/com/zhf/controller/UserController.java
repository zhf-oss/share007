package com.zhf.controller;

import com.google.gson.Gson;
import com.zhf.entity.User;
import com.zhf.entity.VaptchaMessage;
import com.zhf.service.MessageService;
import com.zhf.service.UserService;
import com.zhf.util.CryptographyUtil;
import com.zhf.util.DateUtil;
import com.zhf.util.RedisUtil;
import com.zhf.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Resource
    private JavaMailSender mailSender;

    @Autowired
    private MessageService messageService;

    @Resource
    private RedisUtil<Integer> redisUtil;

    /** 取配置文件里的值. */
    @Value("${userImageFilePath}")
    private String userImageFilePath;

    /**
     * 用户登录请求
     *
     * @param user
     * @param vaptcha_token
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/login")
    public Map<String, Object> login(User user, String vaptcha_token, HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        User adminName = userService.findByUserName(user.getUserName());
        /** 后台管理员登录判断. */
        if (adminName.getRoleName().equals("管理员")) {
            if (StringUtil.isEmpty(user.getUserName().trim())) {
                map.put("success", false);
                map.put("errorInfo", "请输入用户名！");
            } else if (StringUtil.isEmpty(user.getPassword().trim())) {
                map.put("success", false);
                map.put("errorInfo", "请输入密码！");
            }else {
                Subject subject = SecurityUtils.getSubject();
                /** 前端用户输入的 用户名密码 存入token，通过subject.login(token)验证. */
                UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(), CryptographyUtil.md5(user.getPassword(), CryptographyUtil.SALT));
                try {
                    /** 用户名密码验证，如果错误会有异常. */
                    subject.login(token);
                    /** 获取用户名，查询该用户的所有信息，用于前端权限验证. */
                    String userName = (String) SecurityUtils.getSubject().getPrincipal();
                    User currentUser = userService.findByUserName(userName);
                    if (currentUser.isOff()) {
                        map.put("success", false);
                        map.put("errorInfo", "该用户已经被封禁，请联系管理员！");
                        /** 会清楚所有信息，包括session中的信息. */
                        subject.logout();
                    } else {
                        currentUser.setMessageCount(messageService.getCountByUserId(currentUser.getId()));
                        request.getSession().setAttribute("currentUser", currentUser);
                        map.put("success", true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    map.put("success", false);
                    map.put("errorInfo", "用户名或者密码错误！");
                }
            }
        }else{
            /** 前端用户登录判断. */
            if (StringUtil.isEmpty(user.getUserName().trim())) {
                map.put("success", false);
                map.put("errorInfo", "请输入用户名！");
            } else if (StringUtil.isEmpty(user.getPassword().trim())) {
                map.put("success", false);
                map.put("errorInfo", "请输入密码！");
            } else if (!vaptchaCheck(vaptcha_token, request.getRemoteHost())) {
                map.put("success", false);
                map.put("errorInfo", "人机验证失败！");
            } else {
                Subject subject = SecurityUtils.getSubject();
                /** 前端用户输入的 用户名密码 存入token，通过subject.login(token)验证. */
                UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(), CryptographyUtil.md5(user.getPassword(), CryptographyUtil.SALT));
                try {
                    /** 用户名密码验证，如果错误会有异常. */
                    subject.login(token);
                    /** 获取用户名，查询该用户的所有信息，用于前端权限验证. */
                    String userName = (String) SecurityUtils.getSubject().getPrincipal();
                    User currentUser = userService.findByUserName(userName);
                    if (currentUser.isOff()) {
                        map.put("success", false);
                        map.put("errorInfo", "该用户已经被封禁，请联系管理员！");
                        /** 会清楚所有信息，包括session中的信息. */
                        subject.logout();
                    } else {
                        currentUser.setMessageCount(messageService.getCountByUserId(currentUser.getId()));
                        request.getSession().setAttribute("currentUser", currentUser);
                        map.put("success", true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    map.put("success", false);
                    map.put("errorInfo", "用户名或者密码错误！");
                }
            }
        }
        return map;
    }

    /**
     * 管理员登录验证
     * @param user
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/adminLogin")
    public Map<String, Object> adminLogin(User user,HttpServletRequest request)throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtil.isEmpty(user.getUserName().trim())) {
            map.put("success", false);
            map.put("errorInfo", "请输入用户名！");
        } else if (StringUtil.isEmpty(user.getPassword().trim())) {
            map.put("success", false);
            map.put("errorInfo", "请输入密码！");
        } else {
            Subject subject = SecurityUtils.getSubject();
            /** 前端用户输入的 用户名密码 存入token，通过subject.login(token)验证. */
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(), CryptographyUtil.md5(user.getPassword(), CryptographyUtil.SALT));
            try {
                /** 用户名密码验证，如果错误会有异常. */
                subject.login(token);
                /** 获取用户名，查询该用户的所有信息，用于后端权限验证. */
                String userName = (String) SecurityUtils.getSubject().getPrincipal();
                User currentUser = userService.findByUserName(userName);
                if (!currentUser.getRoleName().equals("管理员")) {
                    map.put("success", false);
                    map.put("errorInfo", "该后台只允许帅惨了的管理员登录！");
                    /** 会清楚所有信息，包括session中的信息. */
                    subject.logout();
                } else {
                    request.getSession().setAttribute("currentUser", currentUser);
                    map.put("success", true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                map.put("success", false);
                map.put("errorInfo", "用户名或者密码错误！");
            }
        }
        return map;
    }

    /**
     * 用户注册
     * @param user
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/register")
    public Map<String,Object> register(@Valid User user,BindingResult bindingResult,String vaptcha_token,HttpServletRequest request)throws Exception{
        Map<String,Object> map=new HashMap<String,Object>();
        if(bindingResult.hasErrors()){
            map.put("success", false);
            map.put("errorInfo", bindingResult.getFieldError().getDefaultMessage());
        }else if(userService.findByUserName(user.getUserName())!=null){
            map.put("success", false);
            map.put("errorInfo", "用户名已存在，请更换！");
        }else if(userService.findByEmail(user.getEmail())!=null){
            map.put("success", false);
            map.put("errorInfo", "邮箱已存在，请更换！");
        }else if(!vaptchaCheck(vaptcha_token,request.getRemoteHost())){
            map.put("success", false);
            map.put("errorInfo", "人机验证失败！");
        }else{
            user.setPassword(CryptographyUtil.md5(user.getPassword(), CryptographyUtil.SALT));
            user.setRegisterDate(new Date());
            user.setImageName("default.jpg");
            userService.save(user);
            map.put("success", true);
        }
        return map;
    }

    /**
     * 发送邮件
     *
     * @param email
     * @param session
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/sendEmail")
    public Map<String, Object> sendEmail(String email, HttpSession session) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (StringUtil.isEmpty(email)) {
            resultMap.put("success", false);
            resultMap.put("errorInfo", "邮件不能为空！");
            return resultMap;
        }
        User u = userService.findByEmail(email);
        if (u == null) {
            resultMap.put("success", false);
            resultMap.put("errorInfo", "这个邮件不存在！");
            return resultMap;
        }
        String mailCode = StringUtil.genSixRandomNum();
        SimpleMailMessage message = new SimpleMailMessage();
        /** 发件人. */
        message.setFrom("862592396@qq.com");
        /** 收件人. */
        message.setTo(email);
        /** 主题. */
        message.setSubject("zhf下载站点-用户找回密码");
        message.setText("验证码：" + mailCode);
        /** 发送邮件. */
        mailSender.send(message);

        /** 验证码存到session中，用于用户输入验证码后来进行对比. */
        session.setAttribute("mailCode", mailCode);
        session.setAttribute("userId", u.getId());
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 邮件验证码判断
     *
     * @param yzm
     * @param session
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/checkYzm")
    public Map<String, Object> checkYzm(String yzm, HttpSession session) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (StringUtil.isEmpty(yzm)) {
            resultMap.put("success", false);
            resultMap.put("errorInfo", "验证码不能为空！");
            return resultMap;
        }
        String mailCode = (String) session.getAttribute("mailCode");
        Integer userId = (Integer) session.getAttribute("userId");
        if (!yzm.equals(mailCode)) {
            resultMap.put("success", false);
            resultMap.put("errorInfo", "验证码错误！");
            return resultMap;
        }
        User user = userService.findById(userId);
        user.setPassword(CryptographyUtil.md5("123456", CryptographyUtil.SALT));
        userService.save(user);
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 修改密码
     * @param oldpassword
     * @param password
     * @param session
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/modifyPassword")
    public Map<String, Object> modifyPassword(String oldpassword,String password,HttpSession session)throws Exception{
        Map<String, Object> resultMap = new HashMap<String, Object>();
        User user = (User) session.getAttribute("currentUser");
        if (!user.getPassword().equals(CryptographyUtil.md5(oldpassword, CryptographyUtil.SALT))) {
            resultMap.put("success", false);
            resultMap.put("errorInfo", "原密码错误！");
            return resultMap;
        }
        User oldUser = userService.findById(user.getId());
        oldUser.setPassword(CryptographyUtil.md5(password, CryptographyUtil.SALT));
        userService.save(oldUser);
        resultMap.put("success", true);
        return resultMap;
    }

    /**
     * 图片上传
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/uploadImage")
    public Map<String,Object> uploadImage(MultipartFile file,HttpSession session)throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        if (!file.isEmpty()) {
            // 获取图片名
            String fileName = file.getOriginalFilename();
            // 获取该图片后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            // 唯一的图片名
            String newFileName = DateUtil.getCurrentDateStr()+suffixName;
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(userImageFilePath + newFileName));
            map.put("code",0);
            map.put("msg","上传成功");
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("src","/userImage/"+newFileName);
            map2.put("title",newFileName);
            map.put("data",map2);

            /** 修改数据库中用户的图片名. */
            User user = (User) session.getAttribute("currentUser");
            user.setImageName(newFileName);
            userService.save(user);
            session.setAttribute("currentUser",user);
        }
        return map;
    }

    /**
     * 获取当前登录用户是否是VIP用户
     * @param session
     * @return
     */
    @ResponseBody
    @PostMapping("/isVip")
    public boolean isVip(HttpSession session){
        User user = (User) session.getAttribute("currentUser");
        return user.isVip();
    }

    /**
     * 人机验证结果判断。服务器验证
     * https://user.vaptcha.com
     *
     * @param token
     * @param ip
     * @return
     * @throws Exception
     */
    private boolean vaptchaCheck(String token, String ip) throws Exception {
        String body = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        /** API请求数据地址. */
        HttpPost httpPost = new HttpPost("http://0.vaptcha.com/verify");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        /** 到vaptcha官网注册账号后会有 VID和KEY. */
        nvps.add(new BasicNameValuePair("id", "5f3e06d1a16bb06a276bb929"));
        nvps.add(new BasicNameValuePair("secretkey", "12162ba18bd8411da76bff54109c0683"));
        nvps.add(new BasicNameValuePair("scene", ""));
        /** 前端传来的验证结果. */
        nvps.add(new BasicNameValuePair("token", token));
        nvps.add(new BasicNameValuePair("ip", ip));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        CloseableHttpResponse r = httpClient.execute(httpPost);
        HttpEntity entity = r.getEntity();

        if (entity != null) {
            body = EntityUtils.toString(entity, "utf-8");
            System.out.println(body);
        }
        r.close();
        httpClient.close();
        Gson gson = new Gson();
        VaptchaMessage message = gson.fromJson(body, VaptchaMessage.class);
        if (message.getSuccess() == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 用户签到
     * @param session
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/sign")
    public Map<String,Object> sign(HttpSession session,HttpServletRequest request)throws Exception{
        Map<String,Object> map=new HashMap<String,Object>();
        if(session.getAttribute("currentUser")==null){
            map.put("success", false);
            map.put("errorInfo", "大佬，请先登录下，才能签到;");
            return map;
        }
        User currentUser=(User) session.getAttribute("currentUser");
        if(currentUser.isSign()){
            map.put("success", false);
            map.put("errorInfo", "大佬，你已经签到了，不能重复签到;");
            return map;
        }
        ServletContext application=request.getServletContext();
        Integer signTotal=(Integer) redisUtil.get("signTotal");
        redisUtil.set("signTotal", signTotal+1);
        application.setAttribute("signTotal", signTotal+1);

        // 更新到数据库
        User user = userService.findById(currentUser.getId());
        user.setSign(true);
        user.setSignTime(new Date());
        user.setSignSort(signTotal+1);
        user.setPoints(user.getPoints()+3);
        userService.save(user);

        session.setAttribute("currentUser", user);
        map.put("success", true);
        return map;
    }
}
