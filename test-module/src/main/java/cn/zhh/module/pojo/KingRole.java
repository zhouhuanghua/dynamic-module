package cn.zhh.module.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KingRole {

    private Long id;
    private String userCode;
    private Byte role;
    private LocalDateTime createTime;

}