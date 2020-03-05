package com.imooc.service;

import com.imooc.pojo.Stu;

/**
 * @Description:
 * @Author: zhangchao
 * @Date: 2020-02-17 11:38
 **/
public interface StuService {
    public Stu getStuInfo(int id);

    public void saveStu();

    public void updateStu(int id);

    public void deleteStu(int id);

    public void saveParent();
    public void saveChildren();
}
