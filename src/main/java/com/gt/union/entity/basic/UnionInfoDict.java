package com.gt.union.entity.basic;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 联盟设置申请填写信息
 * </p>
 *
 * @author linweicong
 * @since 2017-08-21
 */
@TableName("t_union_info_dict")
@ApiModel( value = "UnionInfoDict", description = "申请/推荐收集信息主体" )
@Data
public class UnionInfoDict extends Model<UnionInfoDict> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 联盟id
     */
    @NotNull(message = "请选择联盟")
	@ApiModelProperty( value = "联盟id", required = true)
	@TableField("union_id")
	private Integer unionId;
    /**
     * 字典详情id
     */
	@TableField("dict_item_id")
	private Integer dictItemId;
    /**
     * 字典关键key
     */
	@NotBlank(message = "请选择收集信息")
	@ApiModelProperty( value = "收集信息item_key", required = true)
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

	public Integer getDictItemId() {
		return dictItemId;
	}

	public void setDictItemId(Integer dictItemId) {
		this.dictItemId = dictItemId;
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
