<template>
  <div class="ActivityCardStatstics">
    <h3>联盟活动卡发售统计</h3>
    <nav>
      <div class="publishCountStatisticsParent">
        <h3>售卡量统计</h3>
        <p>单位：张</p>
        <div id="publishCountStatistics" style="width:500px;height:300px;"></div>
      </div>

      <div class="sellBrokerageStatisticsParent">
        <div>
          <h3>售卡佣金统计</h3>
          <p>单位：元</p>
        </div>
        <div id="sellBrokerageStatistics" style="width:500px;height:300px;"></div>
      </div>
    </nav>
  </div>
</template>


<script>
import $http from '@/utils/http.js';
import echarts from 'echarts';
export default {
  name: 'activity-card-statistics',
  data() {
    return {
      publishCountChart: '',
      publishCountOption: '',
      sellBrokerageChart: '',
      sellBrokerageOption: '',
      unionList: [],
      publishCountData: [],
      sellBrokerageData: []
    };
  },
  mounted() {
    this.publishCountChart = echarts.init(document.getElementById('publishCountStatistics'));
    this.sellBrokerageChart = echarts.init(document.getElementById('sellBrokerageStatistics'));
    this.getData();
  },
  methods: {
    // 图表设置
    publishCountInit() {
      this.publishCountOption = {
        tooltip: {
          trigger: 'item',
          formatter: params => {
            let unionName = params.name;
            let publishCount = params.value[0];
            let sellCount = params.value[1];
            return `${unionName} <br />历史发布张数：${publishCount} 张 <br />累计售出：${sellCount} 张 `;
          }
        },
        legend: {
          orient: 'vertical',
          x: 'left',
          data: this.unionList
        },
        color: [
          '#20a0ff',
          '#2ec38a',
          '#507baf',
          '#e24c61',
          '#fe6d6c',
          '#ff9348',
          '#ffbf4c',
          '#fdd451',
          '#b177f2',
          '#47d09c'
        ],
        series: [
          {
            name: '',
            type: 'pie',
            radius: ['35%', '80%'],
            center: ['70%', '45%'],
            avoidLabelOverlap: false,
            label: {
              normal: {
                show: false,
                position: 'center'
              },
              emphasis: {
                show: true,
                textStyle: {
                  fontSize: '20',
                  fontWeight: 'bold'
                }
              }
            },
            labelLine: {
              normal: {
                show: false
              }
            },
            data: this.publishCountData
          }
        ]
      };
      this.publishCountChart.setOption(this.publishCountOption);
    },
    sellBrokerageInit() {
      this.sellBrokerageOption = {
        tooltip: {
          trigger: 'item',
          formatter: params => {
            let unionName = params.name;
            let sharingSum = params.value.toFixed(2);
            return `${unionName} <br />售卡佣金累计：￥ ${sharingSum} `;
          }
        },
        legend: {
          orient: 'vertical',
          x: 'left',
          data: this.unionList
        },
        color: [
          '#20a0ff',
          '#2ec38a',
          '#507baf',
          '#e24c61',
          '#fe6d6c',
          '#ff9348',
          '#ffbf4c',
          '#fdd451',
          '#b177f2',
          '#47d09c'
        ],
        series: [
          {
            name: '',
            type: 'pie',
            radius: ['35%', '80%'],
            center: ['70%', '45%'],
            avoidLabelOverlap: false,
            label: {
              normal: {
                show: false,
                position: 'center'
              },
              emphasis: {
                show: true,
                textStyle: {
                  fontSize: '20',
                  fontWeight: 'bold'
                }
              }
            },
            labelLine: {
              normal: {
                show: false
              }
            },
            data: this.sellBrokerageData
          }
        ]
      };
      this.sellBrokerageChart.setOption(this.sellBrokerageOption);
    },
    // 获取图表数据
    getData() {
      $http
        .get(`/unionCard/activityCard/statistics`)
        .then(res => {
          if (res.data.data) {
            this.unionList = [];
            this.publishCountData = [];
            this.sellBrokerageData = [];
            res.data.data.forEach(v => {
              this.unionList.push(v.union.name);
              let value_ = [];
              value_.push(v.publishCount, v.sellCount);
              this.publishCountData.push({ name: v.union.name, value: value_ });
              this.sellBrokerageData.push({ name: v.union.name, value: v.sharingSum });
            });
          }
        })
        .then(res => {
          this.publishCountInit();
          this.sellBrokerageInit();
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    }
  }
};
</script>
