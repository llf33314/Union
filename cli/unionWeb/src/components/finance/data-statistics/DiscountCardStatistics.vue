<template>
  <div class="DiscountCardStatistics">
    <h3>联盟折扣卡领卡统计</h3>
    <div class="btnGroup">
      <el-radio-group v-model="timeRange" @change="getData">
        <el-radio-button label="近七天" ></el-radio-button>
        <el-radio-button label="近一月" ></el-radio-button>
        <el-radio-button label="近半年" ></el-radio-button>
        <el-radio-button label="近一年" ></el-radio-button>
      </el-radio-group>
    </div>
    <div id="discountCardStatistics" style="height:400px;" :style="{width:windowWidth}">
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import echarts from 'echarts';
export default {
  name: 'discount-card-statistics',
  data() {
    return {
      windowWidth:'',
      myChart: '',
      option: '',
      timeRange: '近七天',
      beginTime: '',
      countType: '',
      unionList: [],
      xAxisData: [],
      seriesData: [],
      interval: 0
    };
  },
  mounted() {
    var that=this;
    //用于使chart自适应宽度,通过窗体计算容器高宽
    that.windowWidth= window.innerWidth-35 + 'px';
    setTimeout(function () {
      that.myChart = echarts.init(document.getElementById('discountCardStatistics'));
      that.getData();
    },10)
  },
  methods: {
    // 图表设置
    init() {
      this.option = {
        title: {
          text: '',
          subtext: '数量（张）',
          subtextStyle: {
            color: '#666'
          },
          left:'0'
        },
        grid:{
          x:25,
          borderWidth:1
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: this.unionList,
          x: 'right',
          padding: [0, 120, 0, 0]
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
        xAxis: {
          // todo
          // type: 'time',
          type: 'category',
          boundaryGap: false,
          data: this.xAxisData,
          axisLabel: {
            interval: this.interval
          }
        },
        yAxis: {
          type: 'value'
        },
        series: this.seriesData
      };
      this.myChart.setOption(this.option);
    },
    // 获取图表数据
    getData() {
      switch (this.timeRange) {
        case '近七天':
          this.beginTime = new Date().getTime() - 7 * 24 * 3600 * 1000;
          this.countType = 1;
          this.interval = 0;
          break;
        case '近一月':
          this.beginTime = new Date().getTime() - 30 * 24 * 3600 * 1000;
          this.countType = 1;
          this.interval = 2;
          break;
        case '近半年':
          this.beginTime = new Date().getTime() - 6 * 30 * 24 * 3600 * 1000;
          this.countType = 2;
          this.interval = 0;
          break;
        case '近一年':
          this.beginTime = new Date().getTime() - 12 * 30 * 24 * 3600 * 1000;
          this.countType = 2;
          this.interval = 0;
          break;
      }
      $http
        .get(`/unionCard/discountCard/statistics?type=${this.countType}&beginTime=${this.beginTime}`)
        .then(res => {
          if (res.data.data) {
            this.unionList = [];
            this.xAxisData = [];
            this.seriesData = [];
            res.data.data[0].spotList.forEach(value => {
              this.xAxisData.push(value.time);
            });
            res.data.data.forEach(v => {
              this.unionList.push(v.union.name);
              let numberData = [];
              v.spotList.forEach(val => {
                numberData.push(val.number);
              });
              setTimeout(function () {
                console.log(numberData)
              },10)
              this.seriesData.push({ name: v.union.name,type: 'line', data: numberData });
            });
          }
        })
        .then(res => {
          this.init();
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    }
  },

};
</script>
