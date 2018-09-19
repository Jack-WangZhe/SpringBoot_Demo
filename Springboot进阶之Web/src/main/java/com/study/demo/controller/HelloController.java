package com.study.demo.controller;

import com.study.demo.domain.Girl;
import com.study.demo.domain.Result;
import com.study.demo.repository.GirlRepository;
import com.study.demo.service.GirlService;
import com.study.demo.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class HelloController {

    public final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private GirlRepository girlRepository;
    @Autowired
    public GirlService girlService;

    //查询所有女生列表
    @GetMapping(value="/girls")
    public List<Girl> girlList(){
        logger.info("执行到GirlList");
        return girlRepository.findAll();
    }

    //新增女生
    @PostMapping(value="/girls")
    public Result girlAdd(@Valid Girl girl, BindingResult bindingResult){//@Valid注解表示要验证的对象为girl,验证结果会返回到参数bindingResult对象中
        //通过对象的hasErrors()方法获得是否发生错误
        if(bindingResult.hasErrors()){
            return ResultUtil.error(1,bindingResult.getFieldError().getDefaultMessage());
        }
        girl.setCupSize(girl.getCupSize());
        girl.setAge(girl.getAge());
        return ResultUtil.success(girlRepository.save(girl));//方法返回的就是添加的对象
    }

    //查询一个女生
    @GetMapping(value="/girls/{id}")
    public Optional<Girl> girlFindOne(@PathVariable("id")Integer id){
        Optional<Girl> girl = girlRepository.findById(id);
        return girl;
    }

    //更新
    @PutMapping(value="/girls/{id}")
    public Girl girlUpdate(@PathVariable("id")Integer id,@RequestParam("cupSize")String cupSize,@RequestParam("age")Integer age){
        Girl girl = new Girl();
        girl.setId(id);
        girl.setCupSize(cupSize);
        girl.setAge(age);
        return girlRepository.save(girl);
    }

    //删除
    @DeleteMapping(value="/girls/{id}")
    public void girlDelete(@PathVariable("id")Integer id){
        girlRepository.deleteById(id);
    }

    //通过年龄查询女生列表
    @GetMapping(value="/girls/age/{age}")
    public List<Girl>girlListByAge(@PathVariable("age")Integer age){
        return girlRepository.findByAge(age);
    }

    //添加两条数据
    @PostMapping(value="/girls/two")
    public void girlTwo(){
        girlService.insertTwo();
    }

    //判断对象年龄并抛出异常测试方法
    @GetMapping(value = "girls/getAge/{id}")
    public void getAge(@PathVariable("id")Integer id) throws Exception{
        girlService.getAge(id);
    }
}
