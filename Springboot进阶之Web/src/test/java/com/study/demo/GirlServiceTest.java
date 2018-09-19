package com.study.demo;

import com.study.demo.domain.Girl;
import com.study.demo.service.GirlService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

//表示当前是在测试环节里面跑的，底层使用JUnit测试工具
@RunWith(SpringRunner.class)
//将启动整个Spring的工程
@SpringBootTest
public class GirlServiceTest {

    @Autowired
    private GirlService girlService;

    @Test
    public void findOneTest(){
        Optional<Girl> girl = girlService.findOne(4);
        //断言的方式判断，要求参数都是Object
        Assert.assertEquals(new Integer(16),girl.get().getAge());
    }
}
