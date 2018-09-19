package com.study.demo.service;

import com.study.demo.domain.Girl;
import com.study.demo.enums.ResultEnum;
import com.study.demo.exception.GirlException;
import com.study.demo.repository.GirlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GirlService {
    @Autowired
    private GirlRepository girlRepository;

    //添加@Transactional事务注解
    @Transactional
    public void insertTwo(){
        Girl girlA = new Girl();
        girlA.setCupSize("A");
        girlA.setAge(18);
        girlRepository.save(girlA);

        Girl girlB = new Girl();
        girlB.setCupSize("Fffffff");
        girlB.setAge(19);
        girlRepository.save(girlB);
    }

    public void getAge(Integer id) throws GirlException{
        Optional<Girl> girl = girlRepository.findById(id);
        Integer age = girl.get().getAge();
        if(age<10){
            //返回你还在上小学吧
            throw new GirlException(ResultEnum.PRIMARY_SCHOOL);
        }else if(age>=10&&age<16){
            //返回你可能在上初中
            throw new GirlException(ResultEnum.MIDDLE_SCHOOL);
        }
    }

    /**
     * 通过Id查询一个女生的信息
     */
    public Optional<Girl> findOne(Integer id){
        return girlRepository.findById(id);
    }
}
