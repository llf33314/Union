<template>
  <div>
    <div id="container"></div>
    <div id="myPageTop">
      <table>
        <tr>
          <td>
            <label>请输入关键字：</label>
          </td>
        </tr>
        <tr>
          <td>
            <input id="tipinput" />
          </td>
        </tr>
      </table>
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
      });
      let autoOptions = {
        input: 'tipinput'
      };
      let auto = new AMap.Autocomplete(autoOptions);
      let placeSearch = new AMap.PlaceSearch({
        map: map
      }); //构造地点查询类
      AMap.event.addListener(auto, 'select', select); //注册监听，当选中某条记录时会触发
      function select(e) {
        placeSearch.setCity(e.poi.adcode);
        placeSearch.search(e.poi.name); //关键字查询查询
      }
      // 点击事件
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
        let geocoder = new AMap.Geocoder({
          radius: 1000,
          extensions: 'all'
        });
        geocoder.getAddress([lng, lat], (status, result) => {
          if (status === 'complete' && result.info === 'OK') {
            let enterpriseAddress = result.regeocode.formattedAddress;
            this.$store.commit('enterpriseAddress', enterpriseAddress);
            this.$emit('mapClick');
          }
        });
      });
    }
  }
};
</script>
