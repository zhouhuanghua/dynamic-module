package cn.zhh.project.api.service;

import cn.zhh.project.api.pojo.SysUser;

import java.util.Optional;

public interface SysUserService {

    Optional<SysUser> getByCode(String code);

}