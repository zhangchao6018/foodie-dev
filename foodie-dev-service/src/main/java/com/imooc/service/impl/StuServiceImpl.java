package com.imooc.service.impl;

import com.imooc.mapper.StuMapper;
import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: zhangchao
 * @Date: 2020-02-17 11:38
 **/
@Service
public class StuServiceImpl implements StuService {
    @Autowired
    public StuMapper stuMapper;

    @Override
    public Stu getStuInfo(int id) {
        return stuMapper.selectByPrimaryKey(id);
    }
}
