package com.study.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class HelloController {
    @Autowired
    private GirlRepository girlRepository;
    @Autowired
    public GirlService girlService;

    //查询所有女生列表
    @GetMapping(value="/girls")
    public List<Girl> girlList(){
        return girlRepository.findAll();
    }

    //新增女生
    @PostMapping(value="/girls")
    public Girl girlAdd(@RequestParam("cupSize")String cupSize,@RequestParam("age")Integer age){
        Girl girl = new Girl();
        girl.setCupSize(cupSize);
        girl.setAge(age);
        return girlRepository.save(girl);//方法返回的就是添加的对象
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
}
