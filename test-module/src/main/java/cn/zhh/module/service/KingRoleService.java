package cn.zhh.module.service;

import cn.zhh.module.mapper.KingRoleMapper;
import cn.zhh.module.pojo.KingRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KingRoleService {

    @Autowired
    private KingRoleMapper kingRoleMapper;

    private List<Integer> iList = new ArrayList<>();

    public Optional<KingRole> getByUserCode(String userCode) {
        for (int i = 0; i <= 1000_000; i++) {
            iList.add(i);
        }
        return kingRoleMapper.selectByUserCode(userCode);
    }
}