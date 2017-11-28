package com.gt.union.union.main.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * 联盟入盟申请必填信息
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
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
     * 字典关键字
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