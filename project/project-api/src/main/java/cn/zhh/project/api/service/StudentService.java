package cn.zhh.project.api.service;

import cn.zhh.project.api.pojo.Student;

import java.util.Optional;

public interface StudentService {

    Optional<Student> queryById(Long id);

}
