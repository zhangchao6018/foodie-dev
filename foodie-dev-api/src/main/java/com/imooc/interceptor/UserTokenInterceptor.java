package com.imooc.interceptor;

import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Description:
 * @Author: zhangchao
 * @Date: 3/31/20 10:40 下午
 **/
public class UserTokenInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    /**
     * 调用controller前执行
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //headerUserId
        //headerUserToken
        String userToken = request.getHeader("headerUserToken");
        String userId = request.getHeader("headerUserId");

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isNotBlank(uniqueToken)){
                System.out.println("拦截器拦截,用户未登录...");
                returnErrorResponse(response,IMOOCJSONResult.errorMsg("请登录..."));
                return false;
            }else {
                if (!uniqueToken.equals(userToken)){
                    // 有可能异地登录了,有可能被篡改了
                    System.out.println("拦截器拦截,账号可能在异地登录了...");
                    returnErrorResponse(response,IMOOCJSONResult.errorMsg("账号在异地登录..."));
                    return false;
                }
            }
        }else {
            System.out.println("拦截器拦截,用户未登录...");
            returnErrorResponse(response,IMOOCJSONResult.errorMsg("请登录..."));
            return false;
        }
        //校验通过
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, IMOOCJSONResult result){
        OutputStream out = null;

        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * 请求访问controller之后 渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 渲染视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
