package cn.zhh.project.server.mapper;

import cn.zhh.project.api.pojo.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface SysUserMapper {

    Optional<SysUser> selectByCode(@Param("code") String code);
}
