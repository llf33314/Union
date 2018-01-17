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
<template>
  <div>
    <div>
      <el-form :inline="true" class="demo-form-inline">
        <el-form-item label="选择联盟：" v-if="options.length>1">
          <el-select v-model="unionId" placeholder="请选择" @change="search">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div id="echart1">

    </div>
    <div id="echart2">

    </div>
    <div id="echart3">

    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import echarts from 'echarts';
import $ from 'jquery';
export default {
  name: 'datachart',
  data() {
    return {
      unionId: '',
      options: [],
      statisticData: {}
    };
  },
  mounted: function() {
    this.init();
  },
  methods: {
    init() {
      // 获取我参与过的联盟
      $http
        .get(`/unionMain/busUser`)
        .then(res => {
          if (res.data.data) {
            this.options = res.data.data || [];
            this.options.forEach((v, i) => {
              v.value = v.id;
              v.label = v.name;
            });
            // 给unionId 赋初始值
            this.unionId = this.options[0].value;
          } else {
            this.options = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    },
    search() {
      // 获取商机统计数据
      $http
        .get(`/unionOpportunity/unionId/${this.unionId}/statistics`)
        .then(res => {
          if (res.data.data) {
            this.statisticData = res.data.data;
          } else {
            this.statisticData = {
              incomeSum: 0,
              paidIncome: 0,
              unPaidIncome: 0,
              expenseSum: 0,
              paidExpense: 0,
              unPaidExpense: 0,
              monday: {
                paidIncome: 0,
                paidExpense: 0
              },
              tuesday: {
                paidIncome: 0,
                paidExpense: 0
              },
              wednesday: {
                paidIncome: 0,
                paidExpense: 0
              },
              thursday: {
                paidIncome: 0,
                paidExpense: 0
              },
              friday: {
                paidIncome: 0,
                paidExpense: 0
              },
              saturday: {
                paidIncome: 0,
                paidExpense: 0
              },
              sunday: {
                paidIncome: 0,
                paidExpense: 0
              }
            };
          }
        })
        .then(res => {
          // 渲染视图
          let echart1 = document.getElementById('echart1');
          let echart2 = document.getElementById('echart2');
          let echart3 = document.getElementById('echart3');
          if (echart1) {
            var resizeChart1 = function() {
              echart1.style.width = 500 + 'px';
              echart1.style.height = 300 + 'px';
            };
            resizeChart1();
            let myChart1 = echarts.init(echart1);
            $(window).on('resize', function() {
              resizeChart1();
              myChart1.resize();
            });
            myChart1.setOption({
              title: {
                text: `总佣金收入：￥ ${this.statisticData.incomeSum}`,
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
                      value: this.statisticData.paidIncome,
                      name: '已结算佣金',
                      itemStyle: {
                        normal: {
                          color: '#20A0FF'
                        }
                      }
                    },
                    {
                      value: this.statisticData.unPaidIncome,
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
          }
          if (echart2) {
            var resizeChart2 = function() {
              echart2.style.width = 500 + 'px';
              echart2.style.height = 300 + 'px';
            };
            resizeChart2();
            let myChart2 = echarts.init(echart2);
            $(window).on('resize', function() {
              resizeChart2();
              myChart2.resize();
            });
            myChart2.setOption({
              title: {
                text: `总支付佣金：￥ ${this.statisticData.expenseSum}`,
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
                      value: this.statisticData.paidExpense,
                      name: '已支付佣金',
                      itemStyle: {
                        normal: {
                          color: '#20A0FF'
                        }
                      }
                    },
                    {
                      value: this.statisticData.unPaidExpense,
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
          }
          if (echart3) {
            var resizeChart3 = function() {
              echart3.style.width = 1080 + 'px';
              echart3.style.height = 400 + 'px';
            };
            resizeChart3();
            let myChart3 = echarts.init(echart3);
            $(window).on('resize', function() {
              resizeChart3();
              myChart3.resize();
            });
            let incomeData = [];
            let payData = [];
            // 佣金收入
            incomeData.push(this.statisticData.monday.paidIncome);
            incomeData.push(this.statisticData.tuesday.paidIncome);
            incomeData.push(this.statisticData.wednesday.paidIncome);
            incomeData.push(this.statisticData.thursday.paidIncome);
            incomeData.push(this.statisticData.friday.paidIncome);
            incomeData.push(this.statisticData.saturday.paidIncome);
            incomeData.push(this.statisticData.sunday.paidIncome);
            // 支付佣金
            payData.push(this.statisticData.monday.paidExpense);
            payData.push(this.statisticData.tuesday.paidExpense);
            payData.push(this.statisticData.wednesday.paidExpense);
            payData.push(this.statisticData.thursday.paidExpense);
            payData.push(this.statisticData.friday.paidExpense);
            payData.push(this.statisticData.saturday.paidExpense);
            payData.push(this.statisticData.sunday.paidExpense);
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
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    }
  }
};
</script>




