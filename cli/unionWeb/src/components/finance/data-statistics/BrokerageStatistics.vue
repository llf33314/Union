<template>
  <div class="BrokerageStatistics">
    <h3>商机佣金统计</h3>
    <nav>
      <div id="brokerageIncomeStatistics" style="width:500px;height:300px;"></div>
      <div id="brokeragePayStatistics" style="width:500px;height:300px;"></div>
    </nav>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import echarts from 'echarts';
export default {
  name: 'brokerage-statistics',
  data() {
    return {
      brokerageIncomeChart: '',
      brokerageIncomeOption: '',
      brokeragePayChart: '',
      brokeragePayOption: '',
      brokerageData: '',
      brokerageIncomeData: [],
      brokeragePayData: []
    };
  },
  mounted() {
    this.brokerageIncomeChart = echarts.init(document.getElementById('brokerageIncomeStatistics'));
    this.brokeragePayChart = echarts.init(document.getElementById('brokeragePayStatistics'));
    this.getData();
  },
  methods: {
    // 图表设置
    brokerageIncomeInit() {
      this.brokerageIncomeOption = {
        title: {
          text: `总佣金收入：￥ ${this.brokerageData.incomeSum}`,
          x: '230',
          top: 270,
          textStyle: {
            color: '#ff9348',
            fontWeight: 'bold'
          },
          subtext: '副标题'
        },
        tooltip: {
          trigger: 'item',
          formatter: params => {
            let statisticsType = params.name;
            let percent;
            if (statisticsType === '已结算佣金') {
              percent = (this.brokerageData.paidIncome / this.brokerageData.incomeSum * 100 || 0).toFixed(2);
            } else {
              percent = (this.brokerageData.unPaidIncome / this.brokerageData.incomeSum * 100 || 0).toFixed(2);
            }
            let tempHtml = '';
            for (let i = 0; i < params.value.length; i = i + 2) {
              tempHtml += params.value[i + 1] + ': ￥' + params.value[i].toFixed(2) + '<br />';
            }
            return `${statisticsType} 占比：${percent}% <br />` + tempHtml;
          }
        },
        legend: {
          orient: 'vertical',
          x: 'left',
          data: ['已结算佣金', '未结算佣金']
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
            data: this.brokerageIncomeData
          }
        ]
      };
      this.brokerageIncomeChart.setOption(this.brokerageIncomeOption);
    },
    brokeragePayInit() {
      this.brokeragePayOption = {
        title: {
          text: `总支付佣金：￥ ${this.brokerageData.expenseSum}`,
          x: '230',
          top: 270,
          textStyle: {
            color: '#ff9348',
            fontWeight: 'bold'
          },
          subtext: '副标题'
        },
        tooltip: {
          trigger: 'item',
          formatter: params => {
            let statisticsType = params.name;
            let percent;
            if (statisticsType === '已支付佣金') {
              percent = (this.brokerageData.paidExpense / this.brokerageData.expenseSum * 100 || 0).toFixed(2);
            } else {
              percent = (this.brokerageData.unPaidExpense / this.brokerageData.expenseSum * 100 || 0).toFixed(2);
            }
            let tempHtml = '';
            for (let i = 0; i < params.value.length; i = i + 2) {
              tempHtml += params.value[i + 1] + ': ￥' + params.value[i].toFixed(2) + '<br />';
            }
            return `${statisticsType} 占比：${percent}% <br />` + tempHtml;
          }
        },
        legend: {
          orient: 'vertical',
          x: 'left',
          data: ['已支付佣金', '未支付佣金']
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
            data: this.brokeragePayData
          }
        ]
      };
      this.brokeragePayChart.setOption(this.brokeragePayOption);
    },
    // 获取图表数据
    getData() {
      $http
        .get(`/unionOpportunity/statistics`)
        .then(res => {
          if (res.data.data) {
            this.brokerageData = res.data.data;
            this.brokerageIncomeData = [];
            this.brokeragePayData = [];
            // 佣金结算
            let value1_ = [];
            this.brokerageData.paidIncomeDetailList.forEach(v => {
              value1_.push(v.moneySum, v.union.name);
            });
            this.brokerageIncomeData.push({ name: '已结算佣金', value: value1_ });
            let value2_ = [];

            this.brokerageData.unPaidIncomeDetailList.forEach(v => {
              value2_.push(v.moneySum, v.union.name);
            });
            this.brokerageIncomeData.push({ name: '未结算佣金', value: value2_ });
            let value3_ = [];

            this.brokerageData.paidExpenseDetailList.forEach(v => {
              value3_.push(v.moneySum, v.union.name);
            });
            this.brokeragePayData.push({ name: '已支付佣金', value: value3_ });
            let value4_ = [];

            this.brokerageData.unPaidExpenseDetailList.forEach(v => {
              value4_.push(v.moneySum, v.union.name);
            });
            this.brokeragePayData.push({ name: '未支付佣金', value: value4_ });
          }
        })
        .then(res => {
          this.brokerageIncomeInit();
          this.brokeragePayInit();
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    }
  }
};
</script>


<style lang='less' rel="stylesheet/less" scoped>

</style>
