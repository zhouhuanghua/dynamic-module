package cn.zhh.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.HttpRequestHandler;

import java.util.Set;

@Data
@AllArgsConstructor
public class HttpAction {

    private Set<String> urlMappings;
    private int loadOnStartup;
    private HttpRequestHandler httpRequestHandler;

}