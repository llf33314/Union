package com.gt.union.main.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * 联盟设置申请填写信息
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@TableName("t_union_main_dict")
public class UnionMainDict extends Model<UnionMainDict> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 联盟id
     */
    @TableField("union_id")
    private Integer unionId;
    /**
     * 字典关键key
     */
    @TableField("item_key")
    private String itemKey;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
