package cn.zhh.project.server.service;

import cn.zhh.project.api.pojo.SysUser;
import cn.zhh.project.api.service.SysUserService;
import cn.zhh.project.server.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public Optional<SysUser> getByCode(String code) {
        return sysUserMapper.selectByCode(code);
    }
}
