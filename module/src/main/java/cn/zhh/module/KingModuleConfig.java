package cn.zhh.module;

import cn.zhh.api.HttpAction;
import cn.zhh.api.ModuleConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;

public class KingModuleConfig extends ModuleConfig {

    public String getName() {
        return "King";
    }

    public String getDesc() {
        return "Loops King";
    }

    public String getVersion() {
        return "1.0";
    }

    @Override
    public List<HttpAction> registerServlet() {
        return Lists.newArrayList(new HttpAction(Sets.newHashSet("/king"), 1, (req, resp) -> {
            resp.getWriter().println("this is king");
        }));
    }
}
