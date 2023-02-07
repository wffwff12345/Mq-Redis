package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("stock")
@Data
public class Stock {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 产品名称
     */
    @TableField("name")
    private String name;


    /**
     * 存货
     */
    @TableField("stock")
    private Integer stock;
}