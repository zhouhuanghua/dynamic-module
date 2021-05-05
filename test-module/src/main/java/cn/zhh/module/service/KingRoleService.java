package cn.zhh.module.service;

import cn.zhh.module.mapper.KingRoleMapper;
import cn.zhh.module.pojo.KingRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KingRoleService {

    @Autowired
    private KingRoleMapper kingRoleMapper;

    public Optional<KingRole> getByUserCode(String userCode) {
        return kingRoleMapper.selectByUserCode(userCode);
    }
}