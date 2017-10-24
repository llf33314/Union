package com.gt.union.brokerage.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.brokerage.vo.UnionBrokerageWithDrawalsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 佣金提现记录 Mapper 接口
 *
 * @author linweicong
 * @version 2017-10-23 15:28:54
 */
public interface UnionBrokerageWithdrawalMapper extends BaseMapper<UnionBrokerageWithdrawal> {
    /**
     * 根据商家id分页获取提现记录列表信息
     *
     * @param page  分页对象
     * @param busId 商家id
     * @return List<UnionBrokerageWithDrawalsVO>
     */
    List<UnionBrokerageWithDrawalsVO> listWithdrawals(@Param("page") Page page, @Param("busId") Integer busId);
}