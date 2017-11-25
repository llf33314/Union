package com.gt.union.brokerage.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 佣金收入 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
public class UnionBrokerageIncomeCacheUtil {
    public static final int TYPE_BUS_ID = 1;
    public static final int TYPE_MEMBER_ID = 2;
    public static final int TYPE_UNION_ID = 3;
    public static final int TYPE_CARD_ID = 4;
    public static final int TYPE_OPPORTUNITY_ID = 5;

    public static String getIdKey(Integer id) {
        return "unionBrokerageIncome:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }
    
    public static String getBusIdKey(Integer busId) {
        return "unionBrokerageIncome:busId:" + busId;
    }
    
    public static String getMemberIdKey(Integer memberId) {
        return "unionBrokerageIncome:memberId:" + memberId;
    }
    
    public static String getUnionIdKey(Integer unionId) {
        return "unionBrokerageIncome:unionId:" + unionId;
    }
    
    public static String getCardIdKey(Integer cardId) {
        return "unionBrokerageIncome:cardId:" + cardId;
    }
    
    public static String getOpportunityIdKey(Integer opportunityId) {
        return "unionBrokerageIncome:opportunityId:" + opportunityId;
    }
}