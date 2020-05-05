package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class SSoController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";

    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model ,
                        HttpServletRequest request ,
                        HttpServletResponse response) {
        model.addAttribute("returnUrl",returnUrl);
        //1. 获取userTicket门票

        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        boolean isVerified= verifyUserTicket(userTicket);
        if (isVerified){
            String tmpTicket = createTmpTicket();
            return "redirect:"+returnUrl + "?tmpTicket="+tmpTicket;
        }

        //2.第一次:跳转到统一登录页面
        return "login";
    }

    private boolean verifyUserTicket(String userTicket){
        if (StringUtils.isBlank(userTicket)) {
            return false;
        }
        // 验证是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        //2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return false;
        }
        return true;
    }

    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                        Model model ,
                        HttpServletRequest request ,
                        HttpServletResponse response) throws Exception {
        System.out.println("caonma1sfdsfdsfsdfdsfdsfdsfdsfdsfdsfdsfdsfsdf");
        model.addAttribute("returnUrl",returnUrl);

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            model.addAttribute("errmsg","用户名或密码不能为空");
            return "login";
        }

        // 1. 实现登录
        Users userResult = userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));

        if (userResult == null) {
            model.addAttribute("errmsg","用户名或密码不正确");
            return "login";
        }

        // 2. redis 会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN+":"+userResult.getId(), JsonUtils.objectToJson(usersVO));

        //3. 生成ticket门票 ，全局门票 ，代表用户在CAS端登陆过
        String userTicket = UUID.randomUUID().toString().trim();
        //用户全局门票放入cas的cookie中
        setCookie(COOKIE_USER_TICKET,userTicket,response);

        //4.userTicket 关联用户id,并且放入到redis,代表用户有了门票
        redisOperator.set(REDIS_USER_TICKET+":"+userTicket ,userResult.getId());

        //5.生成临时票据  跳回调用端网站,是由CAS端所签发的一个一次性的临时ticket
        String tmpTicket = createTmpTicket();

        /**
         * userTicket:用于表示用户在CAS端的一个登陆状态:已经登陆
         * tmpTicket :用于临时办法给用户进行一次性的验证票据,有实效性
         */
        return  "login";
//        return "redirect:"+returnUrl + "?tmpTicket="+tmpTicket;
    }


    private String createTmpTicket(){
        String tmpTicket = UUID.randomUUID().toString().trim();
        try {
            redisOperator.set(REDIS_TMP_TICKET+":"+tmpTicket, MD5Utils.getMD5Str(tmpTicket),600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpTicket;
    }

    private void setCookie(String key,
                           String val,
                           HttpServletResponse response)
    {
        Cookie cookie = new Cookie(key, val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public IMOOCJSONResult verifyTmpTicket(String tmpTicket,
                                 HttpServletRequest request ,
                                 HttpServletResponse response) throws Exception {

        //使用一次性临时票据验证用户是否登录  如果是,将用户会话信息返回给站点
        //使用完毕需要销毁
        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)) {
            return  IMOOCJSONResult.errorUserTicket("用户票据异常");
        }
        //0. 临时票据存在 则需要销毁  并且拿到全局userTicket 以此再获取用户信息
        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))){
            return  IMOOCJSONResult.errorUserTicket("用户票据异常");
        }else {
            //销毁票据
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }

        //1. 验证并且获取用户的userTicket
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return  IMOOCJSONResult.errorUserTicket("用户票据异常");
        }
        //2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return  IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        //验证成功
        return IMOOCJSONResult.ok(JsonUtils.jsonToPojo(userRedis,UsersVO.class));
    }

    private String getCookie(HttpServletRequest request , String key){
        Cookie[] cookies = request.getCookies();
        if (cookies == null || StringUtils.isBlank(key)){
            return null;
        }

        String cookieValue =null;
        for (int i =0 ; i <cookies.length ;i++){
            if (cookies[i].getName().equals(key)){
                cookieValue = cookies[i].getValue();
                break;
            }
        }
        return cookieValue;
    }


    @GetMapping("/logout")
    @ResponseBody
    public IMOOCJSONResult logout(String tmpTicket,
                                           HttpServletRequest request ,
                                           HttpServletResponse response) throws Exception {

        return IMOOCJSONResult.ok();
    }
}
