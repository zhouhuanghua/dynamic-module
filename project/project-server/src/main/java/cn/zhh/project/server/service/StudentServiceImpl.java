package cn.zhh.project.server.service;

import cn.zhh.project.api.pojo.Student;
import cn.zhh.project.api.service.StudentService;
import cn.zhh.project.server.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Optional<Student> queryById(Long id) {
        return studentMapper.selectById(id);
    }
}
