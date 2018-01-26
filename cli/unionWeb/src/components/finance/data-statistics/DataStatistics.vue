<template>
  <div>
    数据统计
    <brokerage-statistics></brokerage-statistics>
    <discount-card-statistics></discount-card-statistics>
    <activity-card-statstics></activity-card-statstics>
  </div>
</template>

<script>
import BrokerageStatistics from '@/components/finance/data-statistics/BrokerageStatistics';
import DiscountCardStatistics from '@/components/finance/data-statistics/DiscountCardStatistics';
import ActivityCardStatstics from '@/components/finance/data-statistics/ActivityCardStatstics';
export default {
  name: 'data-statistics',
  components: {
    BrokerageStatistics,
    DiscountCardStatistics,
    ActivityCardStatstics
  },
  data() {
    return {};
  }
};
</script>
