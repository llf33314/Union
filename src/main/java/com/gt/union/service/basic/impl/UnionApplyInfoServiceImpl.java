package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.mapper.basic.UnionApplyInfoMapper;
import com.gt.union.service.basic.IUnionApplyInfoService;
import com.gt.union.service.basic.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟成员申请信息 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionApplyInfoServiceImpl extends ServiceImpl<UnionApplyInfoMapper, UnionApplyInfo> implements IUnionApplyInfoService {

	@Autowired
	private IUnionMemberService unionMemberService;

	@Override
	public void updateUnionApplyInfo(UnionApplyInfo unionApplyInfo, Integer busId, Integer unionId) throws Exception{
		unionMemberService.isMemberValid(busId,unionId);
		this.updateById(unionApplyInfo);
	}

	@Override
	public Page pageSellDivideProportion(Page page, final Integer unionId) throws Exception {
		if (page == null) {
			throw new Exception("UnionApplyInfoServiceImpl.pageSellDivideProportion():参数page不能为空!");
		}
		if (unionId == null) {
			throw new Exception("UnionApplyInfoServiceImpl.pageSellDivideProportion():参数unionId不能为空!");
		}
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" i");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a ON a.id = i.union_apply_id ")
                        .append(" LEFT JOIN t_union_member m ON m.id = a.union_member_id ")
                        .append(" WHERE a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND (m.out_staus = ").append(UnionMemberConstant.OUT_STATUS_NORMAL)
                        .append("        OR m.out_staus = ").append(UnionMemberConstant.OUT_STATUS_UNCHECKED)
                        .append("        ) ")
                        .append("    AND a.union_id = ").append(unionId)
                        .append(" ORDER BY m.is_nuion_owner DESC ");
                return sbSqlSegment.toString();
            }
        };
		wrapper.setSqlSelect(" i.enterprise_name enterpriseName, i.sell_divide_proportion sellDivideProportion, m.is_nuion_owner isUnionOwner ");

		return this.selectMapsPage(page, wrapper);
	}

	@Override
	public List<Map<String,Object>> listSellDivideProportion(final Integer unionId) throws Exception {
        if (unionId == null) {
            throw new Exception("UnionApplyInfoServiceImpl.listSellDivideProportion():参数unionId不能为空!");
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" i");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a ON a.id = i.union_apply_id ")
                        .append(" LEFT JOIN t_union_member m ON m.id = a.union_member_id ")
                        .append(" WHERE a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND (m.out_staus = ").append(UnionMemberConstant.OUT_STATUS_NORMAL)
                        .append("        OR m.out_staus = ").append(UnionMemberConstant.OUT_STATUS_UNCHECKED)
                        .append("        ) ")
                        .append("    AND a.union_id = ").append(unionId)
                        .append(" ORDER BY m.is_nuion_owner DESC ");
                return sbSqlSegment.toString();
            }
        };
        wrapper.setSqlSelect(" i.id infoId, i.enterprise_name enterpriseName, i.sell_divide_proportion sellDivideProportion, m.is_nuion_owner isUnionOwner ");

        return this.selectMaps(wrapper);
	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSellDivideProportion(List<UnionApplyInfo> unionApplyInfoList) throws Exception {
	    if (ListUtil.isNotEmpty(unionApplyInfoList)) {
	        List<UnionApplyInfo> unionApplyInfos = new LinkedList<>();
            BigDecimal sellDivideProportionSum = BigDecimal.valueOf(0.0);
	        for (int i = 0, size = unionApplyInfoList.size(); i < size; i++) {
	            UnionApplyInfo fromUnionApplyInfo = unionApplyInfoList.get(i);
	            if (fromUnionApplyInfo.getId() == null) {
	                throw new Exception("UnionApplyInfoServiceImpl.updateSellDivideProportion():更新列表中存在id为空的对象!");
                }
                if (fromUnionApplyInfo.getSellDivideProportion() == null) {
                    throw new Exception("UnionApplyInfoServiceImpl.updateSellDivideProportion():更新列表中存在比例为空的对象!");
                }
                sellDivideProportionSum = BigDecimalUtil.add(sellDivideProportionSum, fromUnionApplyInfo.getSellDivideProportion());
                UnionApplyInfo toUnionApplyInfo = new UnionApplyInfo();
	            toUnionApplyInfo.setId(fromUnionApplyInfo.getId());
	            toUnionApplyInfo.setSellDivideProportion(fromUnionApplyInfo.getSellDivideProportion());
	            unionApplyInfos.add(toUnionApplyInfo);
            }
            if (BigDecimalUtil.subtract(sellDivideProportionSum, BigDecimal.valueOf(100.0)).doubleValue() != 0.0) {
                throw new Exception("UnionApplyInfoServiceImpl.updateSellDivideProportion():更新列表中的比例之和必须等于100!");
            }
	        this.updateBatchById(unionApplyInfos);
        }
    }
}
