<template>
  <div id="container">

  </div>
</template>

<script>
import axios from 'axios'
import $ from 'jquery'
export default {
  name: 't-map',
  data() {
    return {
      lat: 39.916527,
      lng: 116.397128,
    }
  },
  methods: {
    // codeAddress() {
    //   geocoder.getLocation(this.$store.state.address);
    // }
  },
  mounted() {
    var _this = this;
    var geocoder, citylocation, map, marker = null;
    var codeAddress = function(value) {
      geocoder.getLocation(value);
    }
    eventBus.$on('addressChange', () => {
      codeAddress(_this.$store.state.address);
    })
    var init = function() {
      var center = new qq.maps.LatLng(_this.lat, _this.lng);
      map = new qq.maps.Map(document.getElementById('container'), {
        center: center,
        zoom: 13
      });
      //获取城市列表接口设置中心点
      citylocation = new qq.maps.CityService({
        complete: function(result) {
          map.setCenter(result.detail.latLng);
        }
      });
      //调用searchLocalCity();方法    根据用户IP查询城市信息。
      citylocation.searchLocalCity();
      //调用地址解析类
      geocoder = new qq.maps.Geocoder({
        complete: function(result) {
          map.setCenter(result.detail.location);
          var marker = new qq.maps.Marker({
            map: map,
            position: result.detail.location
          });

        }
      });
      codeAddress(_this.$store.state.address);
      //添加监听事件
      qq.maps.event.addListener(map, 'click', function(event) {
        var marker = new qq.maps.Marker({
          position: event.latLng,
          map: map
        });
        qq.maps.event.addListener(map, 'click', function(event) {
          marker.setMap(null);
        });
      });
      qq.maps.event.addListener(map, 'click', function(event) {
        // axios({
        //   method: 'get',
        //   baseURL: '',
        //   url: '/apitest/v1/?location=' + event.latLng.lat + "," + event.latLng.lng + '&key=C4LBZ-DCNWF-JJMJD-N7WUE-VHSIJ-3KFPW&get_poi=0', // 调试
        //   headers: { 'X-Requested-With': 'XMLHttpRequest' },
        // }) // 调试
        let data = {};
        data.output = "jsonp";
        data.location = event.latLng.lat + "," + event.latLng.lng;
        data.key = "2VBBZ-A3C3O-E7XW7-S6RWA-Q676Z-O6FGU";
        $.ajax({
          type: "get",
          dataType: 'jsonp',
          data: data,
          jsonp: "callback",
          url: "https://apis.map.qq.com/ws/geocoder/v1",
          jsonpCallback: "QQmap", // 测试
        })
          .then(res => {
            if (res.status == 0) {
              _this.$store.commit('latitudeChange', event.latLng.lat);
              _this.$store.commit('longitudeChange', event.latLng.lng);
              _this.$store.commit('enterpriseAddress', res.result.address);
            }
            _this.$emit('mapClick');
          })
          .catch(err => {
            _this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      });
    }
    init()
  }
}
</script>

