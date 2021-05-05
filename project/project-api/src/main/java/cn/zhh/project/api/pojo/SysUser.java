package cn.zhh.project.api.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUser {

    private Long id;
    private String code;
    private String name;
    private String mobile;
    private LocalDateTime createTime;

}
