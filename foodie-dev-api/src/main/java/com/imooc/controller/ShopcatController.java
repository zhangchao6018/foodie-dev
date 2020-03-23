package com.imooc.controller;

import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchao
 * @Date: 3/22/20 10:30 下午
 **/
@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopcatController extends BaseController{

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public IMOOCJSONResult add(   @RequestParam String userId,
                                  @RequestBody ShopcartBO shopcartBO,
                                  HttpServletRequest request,
                                  HttpServletResponse response
    ){
        System.out.println("----------------------in---------------------");
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("");
        }

        // 从redis中取出购物车
        String shopCart = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopcartBO> shopcartBOS ;
        if (StringUtils.isBlank(shopCart)){
            shopcartBOS= new ArrayList<>();
            shopcartBOS.add(shopcartBO);
        }else {
            shopcartBOS = JsonUtils.jsonToList(shopCart, ShopcartBO.class);
            boolean isHaving = false;
            for (ShopcartBO bo : shopcartBOS) {
                //判断rdis中是否已存在该规格商品
                if (shopcartBO.getSpecId().equals(bo.getSpecId())){
                    //增加购买数量
                    bo.setBuyCounts(bo.getBuyCounts()+shopcartBO.getBuyCounts());
                    isHaving = true;
                }
                if (!isHaving){
                    shopcartBOS.add(shopcartBO);
                }
            }
        }

        //写入redis
        redisOperator.set((FOODIE_SHOPCART + ":" + userId),JsonUtils.objectToJson(shopcartBOS));

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public IMOOCJSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return IMOOCJSONResult.errorMsg("参数不能为空");
        }
        String redisKey = FOODIE_SHOPCART + ":" + userId;
        String shopCarts = redisOperator.get(redisKey);
        //购物车为空直接返回成功
        if (StringUtils.isBlank(shopCarts)) {
            return IMOOCJSONResult.ok();
        }
        //遍历购物车
        List<ShopcartBO> shopcarts = JsonUtils.jsonToList(shopCarts, ShopcartBO.class);
        for (ShopcartBO shopcart : shopcarts) {
            String specId = shopcart.getSpecId();
            if (specId.equals(itemSpecId)){
                shopcarts.remove(shopcart);
                break;
            }
        }
        //重置缓存购物车
        redisOperator.set((FOODIE_SHOPCART + ":" + userId),JsonUtils.objectToJson(shopcarts));
        return IMOOCJSONResult.ok();
    }
}
