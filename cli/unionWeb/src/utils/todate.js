// Mon Aug 21 2017 17:11:27 GMT+0800 (中国标准时间) 转换为 'yyyy-mm-dd hh:mm:ss'
export default {
  todate(num) {
    num = num + "";
    let date = "";
    let month = new Array();
    month["Jan"] = '01';
    month["Feb"] = '02';
    month["Mar"] = '03';
    month["Apr"] = '04';
    month["May"] = '05';
    month["Jun"] = '06';
    month["Jul"] = '07';
    month["Aug"] = '08';
    month["Sep"] = '09';
    month["Oct"] = '10';
    month["Nov"] = '11';
    month["Dec"] = '12';
    let week = new Array();
    week["Mon"] = "一";
    week["Tue"] = "二";
    week["Wed"] = "三";
    week["Thu"] = "四";
    week["Fri"] = "五";
    week["Sat"] = "六";
    week["Sun"] = "日";
    let str = num.split(" ");
    date = str[3] + "-";
    date = date + month[str[1]] + "-" + str[2] + " " + str[4];
    return date;
  }
}
