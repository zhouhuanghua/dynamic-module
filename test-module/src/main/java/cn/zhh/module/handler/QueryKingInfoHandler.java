package cn.zhh.module.handler;

import cn.zhh.dynamic_module.GsonUtils;
import cn.zhh.dynamic_module.Handler;
import cn.zhh.module.service.KingRoleService;
import cn.zhh.module.pojo.KingInfo;
import cn.zhh.project.api.pojo.GetSysUserByCodeReq;
import cn.zhh.project.api.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryKingInfoHandler implements Handler {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private KingRoleService kingRoleService;

    @Override
    public Object execute(String params) {
        GetSysUserByCodeReq req = GsonUtils.fromJson(params, GetSysUserByCodeReq.class);
        return sysUserService.getByCode(req.getCode())
                .map(sysUser -> {
                    KingInfo kingInfo = new KingInfo();
                    kingInfo.setUserId(sysUser.getId());
                    kingInfo.setUserCode(sysUser.getCode());
                    kingInfo.setUserName(sysUser.getName());
                    kingRoleService.getByUserCode(sysUser.getCode()).ifPresent(kingRole -> kingInfo.setUserRole(kingRole.getRole()));
                    return kingInfo;
                })
                .orElse(null);
    }

    @Override
    public String name() {
        return "QueryKingInfo";
    }

}