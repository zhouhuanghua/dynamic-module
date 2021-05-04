package cn.zhh.project.api.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Student {

    private Long id;
    private String code;
    private String name;
    private LocalDateTime createTime;

}
