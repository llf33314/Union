<template>
  <div class="unionMap">
    <div id="container">

    </div>
    <div id="myPageTop">
      <input id="tipinput" placeholder="请输入关键字" />
      <el-button id="search" type="primary" icon="search">搜索</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'union-map',
  props: ['address'],
  data() {
    return {};
  },
  mounted: function() {
    this.init();
  },
  methods: {
    init() {
      // 初始化地图
      let map = new AMap.Map('container', {
        resizeEnable: true
      });
      map.plugin(['AMap.ToolBar', 'AMap.CitySearch', 'AMap.Geocoder', 'AMap.Autocomplete', 'AMap.PlaceSearch'], () => {
        map.addControl(new AMap.ToolBar());
        let geocoder = new AMap.Geocoder({
          radius: 1000,
          extensions: 'all'
        });
        let citysearch = new AMap.CitySearch();
        let autoOptions = {
          input: 'tipinput'
        };
        let auto = new AMap.Autocomplete(autoOptions);
        // 构造地点查询类
        let placeSearch = new AMap.PlaceSearch({
          map: map
        });
        if (this.address) {
          // 地理编码,返回地理编码结果
          geocoder.getLocation(this.address, (status, result) => {
            if (status === 'complete' && result.info === 'OK') {
              let lng = result.geocodes[0].location.lng;
              let lat = result.geocodes[0].location.lat;
              // 设置缩放级别和中心点
              map.setZoomAndCenter(14, [lng, lat]);
              // 在新中心点添加 marker
              let marker = new AMap.Marker({
                map: map,
                position: [lng, lat]
              });
            }
          });
        } else {
          // 定位到当前城市
          citysearch.getLocalCity((status, result) => {
            if (status === 'complete' && result.info === 'OK') {
              if (result && result.city && result.bounds) {
                let cityinfo = result.city;
                let citybounds = result.bounds;
                // 地图显示当前城市
                map.setBounds(citybounds);
              }
            }
          });
        }
        // 注册监听，当选中某条记录时会触发
        AMap.event.addListener(auto, 'select', e => {
          placeSearch.setCity(e.poi.adcode);
          placeSearch.search(e.poi.name); // 关键字查询查询
        });
        // 点击搜索
        let searchButton = document.getElementById('search');
        AMap.event.addDomListener(searchButton, 'click', () => {
          let value = document.getElementById('tipinput').value;
          placeSearch.search(value, (status, result) => {
            if (!result.poiList.pois.length) {
              this.$message({ showClose: true, message: '请缩小搜索范围', type: 'error', duration: 5000 });
            }
          }); // 关键字查询查询
        });
        // 交换数据
        AMap.event.addListener(placeSearch, 'markerClick', e => {
          let lng = e.data.location.lng;
          let lat = e.data.location.lat;
          let enterpriseAddress = e.data.pname + e.data.cityname + e.data.adname + e.data.address + e.data.name;
          this.$store.commit('longitudeChange', lng);
          this.$store.commit('latitudeChange', lat);
          this.$store.commit('enterpriseAddress', enterpriseAddress);
          setTimeout(() => {
            this.$emit('mapClick');
          }, 0);
        });
        AMap.event.addListener(map, 'click', e => {
          map.clearMap(); // 清除地图覆盖物
          let lng = e.lnglat.getLng();
          let lat = e.lnglat.getLat();
          this.$store.commit('longitudeChange', lng);
          this.$store.commit('latitudeChange', lat);
          // 点击添加标记
          let marker = new AMap.Marker({
            map: map,
            position: [lng, lat]
          });
          // 逆向地理编码解析
          geocoder.getAddress([lng, lat], (status, result) => {
            if (status === 'complete' && result.info === 'OK') {
              let enterpriseAddress = result.regeocode.formattedAddress;
              this.$store.commit('enterpriseAddress', enterpriseAddress);
            }
          });
          setTimeout(() => {
            this.$emit('mapClick');
          }, 0);
        });
      });
      map.setFitView();
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

