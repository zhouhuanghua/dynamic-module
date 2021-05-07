package cn.zhh.module.pojo;

import lombok.Data;

@Data
public class KingInfo {

    private Long userId;
    private String userCode;
    private String userName;
    private Byte userRole;

}