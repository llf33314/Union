<template>
  <div id="container">

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
      let map = new AMap.Map('container', {});
      map.plugin(['AMap.ToolBar', 'AMap.CitySearch', 'AMap.Geocoder'], () => {
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
