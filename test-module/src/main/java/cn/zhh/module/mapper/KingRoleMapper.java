package cn.zhh.module.mapper;

import cn.zhh.module.pojo.KingRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface KingRoleMapper {

    Optional<KingRole> selectByUserCode(@Param("userCode") String userCode);

}