<template>
  <div class="unionMap">
    <div id="container">

    </div>
    <div id="myPageTop">
      <input id="tipinput" type="text" v-model="searchValue" placeholder="请输入关键字" @keyup.enter="search"/>
      <el-button id="search" type="primary" icon="search" @click="search">搜索</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'union-map',
  props: ['address'],
  data() {
    return {
      searchValue: '',
      unionMap: '',
      geocoder: '',
      citySearch: '',
      autoOptions: '',
      auto: '',
      placeSearch: ''
    };
  },
  mounted: function() {
    this.init();
  },
  methods: {
    init() {
      // 初始化地图
      this.unionMap = new AMap.Map('container', {
        resizeEnable: true
      });
      this.unionMap.plugin(
        ['AMap.ToolBar', 'AMap.CitySearch', 'AMap.Geocoder', 'AMap.Autocomplete', 'AMap.PlaceSearch'],
        () => {
          this.unionMap.addControl(new AMap.ToolBar());
          this.autoOptions = {
            input: 'tipinput'
          };
          this.auto = new AMap.Autocomplete(this.autoOptions);
          // 构造地点查询类
          this.placeSearch = new AMap.PlaceSearch({
            map: this.unionMap
          });
          this.geocoder = new AMap.Geocoder({
            radius: 1000,
            extensions: 'all'
          });
          this.citysearch = new AMap.CitySearch();
          if (this.address) {
            this.getGeocoder();
          } else {
            this.getCity();
          }
          this.onSelect();
          this.exchange();
        }
      );
      this.unionMap.setFitView();
    },
    // 地理编码,返回地理编码结果
    getGeocoder() {
      this.geocoder.getLocation(this.address, (status, result) => {
        if (status === 'complete' && result.info === 'OK') {
          let lng = result.geocodes[0].location.lng;
          let lat = result.geocodes[0].location.lat;
          // 设置缩放级别和中心点
          this.unionMap.setZoomAndCenter(14, [lng, lat]);
          // 在新中心点添加 marker
          let marker = new AMap.Marker({
            map: this.unionMap,
            position: [lng, lat]
          });
        }
      });
    },
    // 定位到当前城市
    getCity() {
      this.citysearch.getLocalCity((status, result) => {
        if (status === 'complete' && result.info === 'OK') {
          if (result && result.city && result.bounds) {
            let cityinfo = result.city;
            let citybounds = result.bounds;
            // 地图显示当前城市
            this.unionMap.setBounds(citybounds);
          }
        }
      });
    },
    // 注册监听，当选中某条记录时会触发
    onSelect() {
      AMap.event.addListener(this.auto, 'select', e => {
        this.placeSearch.setCity(e.poi.adcode);
        this.placeSearch.search(e.poi.name);
      });
    },
    // 关键字搜索
    search() {
      if (this.searchValue) {
        this.unionMap.getCity(res => {
          let city = res.city || res.province;
          this.geocoder.getLocation(city, (status, result) => {
            if (status === 'complete' && result.info === 'OK') {
              this.placeSearch.setCity(result.geocodes[0].adcode);
              this.placeSearch.search(this.searchValue);
            }
          });
        });
      }
    },
    // 交换数据
    exchange() {
      AMap.event.addListener(this.placeSearch, 'markerClick', e => {
        let lng = e.data.location.lng;
        let lat = e.data.location.lat;
        let enterpriseAddress = e.data.pname + e.data.cityname + e.data.adname + e.data.address + e.data.name;
        this.$store.commit('longitudeChange', lng);
        this.$store.commit('latitudeChange', lat);
        this.$store.commit('enterpriseAddressChange', enterpriseAddress);
        setTimeout(() => {
          this.$emit('mapClick');
        }, 0);
      });
      AMap.event.addListener(this.unionMap, 'click', e => {
        this.unionMap.clearMap(); // 清除地图覆盖物
        let lng = e.lnglat.getLng();
        let lat = e.lnglat.getLat();
        this.$store.commit('longitudeChange', lng);
        this.$store.commit('latitudeChange', lat);
        // 点击添加标记
        let marker = new AMap.Marker({
          map: this.unionMap,
          position: [lng, lat]
        });
        // 逆向地理编码解析
        this.geocoder.getAddress([lng, lat], (status, result) => {
          if (status === 'complete' && result.info === 'OK') {
            let enterpriseAddress = result.regeocode.formattedAddress;
            this.$store.commit('enterpriseAddressChange', enterpriseAddress);
            setTimeout(() => {
              this.$emit('mapClick');
            }, 0);
          }
        });
      });
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less">
.unionMap {
  position: relative;
  width: 800px;
}
#container {
  width: 800px;
  height: 500px;
}
#myPageTop {
  position: absolute;
  top: 8px;
  right: 6px;
  > input {
    height: 32px;
    width: 174px;
  }
  .el-button {
    margin-left: -4px;
    border-radius: 0 4px 4px 0;
  }
}
#tipinput::-webkit-input-placeholder {
  /* WebKit browsers */
  color: #97a8be;
}
#tipinput:-moz-placeholder {
  /* Mozilla Firefox 4 to 18 */
  color: #97a8be;
}
#tipinput::-moz-placeholder {
  /* Mozilla Firefox 19+ */
  color: #97a8be;
}
#tipinput::-ms-input-placeholder {
  /* Internet Explorer 10+ */
  color: #97a8be;
}
</style>
