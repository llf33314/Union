<template>
  <div>
    <div>
      <el-form :inline="true" class="demo-form-inline">
        <el-form-item label="选择联盟：">
          <el-select v-model="value" placeholder="请选择" @change="change">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div id="echart1">
      echart1
    </div>
    <div id="echart2">
      echart2
    </div>
    <div id="echart3">
      echart3
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import echarts from 'echarts';
export default {
  name: 'datachart',
  data() {
    return {
      value: '',
      options: [],
      statisticData: {}
    };
  },
  computed: {
    initUnionId() {
      return this.$store.state.unionId;
    }
  },
  watch: {
    initUnionId: function() {
      this.myInit();
    }
  },
  mounted: function() {
    this.myInit();
  },
  methods: {
    myInit() {
      $(document).ready(function() {
        if (this.initUnionId) {
          // 获取联盟列表
          $http
            .get(`/unionMember/listMap`)
            .then(res => {
              if (res.data.data && res.data.data.length > 0) {
                this.options = res.data.data;
                res.data.data.forEach((v, i) => {
                  this.options[i].value = v.unionMember.id;
                  this.options[i].label = v.unionMain.name;
                });
                this.value = this.options[0].value;
                // 获取商机统计数据
                $http
                  .get(`/unionOpportunity/memberId/${this.value}/statistics`)
                  .then(res => {
                    if (res.data.data) {
                      this.statisticData = res.data.data;
                      // 渲染视图
                      let myChart1 = echarts.init(document.getElementById('echart1'));
                      myChart1.setOption({
                        title: {
                          text: `总佣金收入：￥ ${this.statisticData.brokerageIncomeSum}`,
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
                          formatter: '{a} <br/>{b}: {c} ({d}%)'
                        },
                        legend: {
                          orient: 'vertical',
                          x: 'left',
                          data: ['已结算佣金', '未结算佣金']
                        },
                        color: ['#20A0FF', '#20A0FF'],
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
                            data: [
                              {
                                value: this.statisticData.paidBrokerageIncome,
                                name: '已结算佣金',
                                itemStyle: {
                                  normal: {
                                    color: '#20A0FF'
                                  }
                                }
                              },
                              {
                                value: this.statisticData.unPaidBrokerageIncome,
                                name: '未结算佣金',
                                itemStyle: {
                                  normal: {
                                    color: '#2EC38A'
                                  }
                                }
                              }
                            ]
                          }
                        ]
                      });
                      let myChart2 = echarts.init(document.getElementById('echart2'));
                      myChart2.setOption({
                        title: {
                          text: `总支付佣金：￥ ${this.statisticData.brokerageExpenseSum}`,
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
                          formatter: '{a} <br/>{b}: {c} ({d}%)'
                        },
                        legend: {
                          orient: 'vertical',
                          x: '50',
                          data: ['已支付佣金', '未支付佣金']
                        },
                        color: ['#20A0FF', '#20A0FF'],
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
                            data: [
                              {
                                value: this.statisticData.paidBrokerageExpense,
                                name: '已支付佣金',
                                itemStyle: {
                                  normal: {
                                    color: '#20A0FF'
                                  }
                                }
                              },
                              {
                                value: this.statisticData.unPaidBrokerageExpense,
                                name: '未支付佣金',
                                itemStyle: {
                                  normal: {
                                    color: '#2EC38A'
                                  }
                                }
                              }
                            ]
                          }
                        ]
                      });
                      let myChart3 = echarts.init(document.getElementById('echart3'));
                      let incomeData = [];
                      let payData = [];
                      // 佣金收入
                      incomeData.push(this.statisticData.brokerageInWeek[0]['Monday'].paidBrokerageIncome);
                      incomeData.push(this.statisticData.brokerageInWeek[1]['Tuesday'].paidBrokerageIncome);
                      incomeData.push(this.statisticData.brokerageInWeek[2]['Wednesday'].paidBrokerageIncome);
                      incomeData.push(this.statisticData.brokerageInWeek[3]['Thursday'].paidBrokerageIncome);
                      incomeData.push(this.statisticData.brokerageInWeek[4]['Friday'].paidBrokerageIncome);
                      incomeData.push(this.statisticData.brokerageInWeek[5]['Saturday'].paidBrokerageIncome);
                      incomeData.push(this.statisticData.brokerageInWeek[6]['Sunday'].paidBrokerageIncome);
                      // 支付佣金
                      payData.push(this.statisticData.brokerageInWeek[0]['Monday'].paidBrokerageExpense);
                      payData.push(this.statisticData.brokerageInWeek[1]['Tuesday'].paidBrokerageExpense);
                      payData.push(this.statisticData.brokerageInWeek[2]['Wednesday'].paidBrokerageExpense);
                      payData.push(this.statisticData.brokerageInWeek[3]['Thursday'].paidBrokerageExpense);
                      payData.push(this.statisticData.brokerageInWeek[4]['Friday'].paidBrokerageExpense);
                      payData.push(this.statisticData.brokerageInWeek[5]['Saturday'].paidBrokerageExpense);
                      payData.push(this.statisticData.brokerageInWeek[6]['Sunday'].paidBrokerageExpense);
                      myChart3.setOption({
                        title: {
                          text: '商机一周统计表',
                          subtext: '单位：元',
                          subtextStyle: {
                            //副标题内容的样式
                            color: '#666666',
                            fontStyle: 'normal', //主标题文字字体风格，默认normal，有italic(斜体),oblique(斜体)
                            fontWeight: 'lighter', //可选normal(正常)，bold(加粗)，bolder(加粗)，lighter(变细)，100|200|300|400|500...
                            fontSize: 12 //主题文字字体大小，默认为12px
                          },
                          itemGap: 20, //主标题和副标题之间的距离，可自行设置
                          left: 'left'
                        },
                        tooltip: {
                          trigger: 'axis',
                          axisPointer: {
                            // 坐标轴指示器，坐标轴触发有效
                            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
                          }
                        },
                        legend: {
                          data: ['佣金收入', '支付佣金'],
                          left: 'right'
                        },
                        grid: {
                          left: '0%',
                          right: '0%',
                          top: '30%',
                          bottom: '0%',
                          height: '252px',
                          containLabel: true
                        },
                        xAxis: [
                          {
                            type: 'category',
                            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                          }
                        ],
                        yAxis: [
                          {
                            type: 'value'
                          }
                        ],
                        series: [
                          {
                            name: '佣金收入',
                            type: 'bar',
                            data: incomeData,
                            itemStyle: {
                              normal: {
                                color: '#20A0FF'
                              }
                            },
                            barWidth: 20 //条形的宽度
                          },
                          {
                            name: '支付佣金',
                            type: 'bar',
                            data: payData,
                            itemStyle: {
                              normal: {
                                color: '#2EC38A'
                              }
                            },
                            barWidth: 20
                          }
                        ]
                      });
                    } else {
                      this.statisticData = {};
                    }
                  })
                  .catch(err => {
                    this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
                  });
              } else {
                this.options = [];
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        }
      });
    },
    change() {
      this.myInit();
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
#echart1 {
  width: 500px;
  height: 300px;
  float: left;
  margin-right: 100px;
}

#echart2 {
  width: 500px;
  height: 300px;
  float: left;
}

#echart3 {
  width: 1080px;
  height: 400px;
  float: left;
  margin-top: 50px;
}
</style>


