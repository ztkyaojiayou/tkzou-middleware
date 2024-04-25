package com.tkzou.middleware.localmsgretry.mqretry.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 业务dto
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/10/03 00:41
 */
@Data
@AllArgsConstructor
public class BusinessDTO implements Serializable {
    private Integer id;
    private String name;
    private Integer age;
}
