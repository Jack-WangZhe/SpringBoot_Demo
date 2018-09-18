package com.study.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GirlRepository extends JpaRepository<Girl,Integer>{
    //通过年龄来查询,方法名需要按照格式去书写
    public List<Girl> findByAge(Integer age);
}