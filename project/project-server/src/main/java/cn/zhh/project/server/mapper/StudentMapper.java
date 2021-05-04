package cn.zhh.project.server.mapper;

import cn.zhh.project.api.pojo.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface StudentMapper {

    Optional<Student> selectById(Long id);
}
