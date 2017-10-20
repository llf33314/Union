package com.gt.union.main.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 联盟公告
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@TableName("t_union_main_notice")
public class UnionMainNotice extends Model<UnionMainNotice> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 创建时间
     */
    private Date createtime;
    /**
     * 删除状态（0：未删除 1：删除）
     */
    @TableField("del_status")
    private Integer delStatus;
    /**
     * 联盟id
     */
    @TableField("union_id")
    private Integer unionId;
    /**
     * 公告内容
     */
    private String content;
    /**
     * 修改时间
     */
    private Date modifytime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getModifytime() {
        return modifytime;
    }

    public void setModifytime(Date modifytime) {
        this.modifytime = modifytime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
