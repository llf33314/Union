// 只能输入正数
export function numberCheck(value) {
  //先把非数字的都替换掉，除了数字和.
  value = value.replace(/[^\d\.]/g, '');
  //必须保证第一个为数字而不是.
  value = value.replace(/^\./g, '');
  //保证只有出现一个.而没有多个.
  value = value.replace(/\.{2,}/g, '.');
  //保证.只出现一次，而不能出现两次以上
  value = value
    .replace('.', '$#$')
    .replace(/\./g, '')
    .replace('$#$', '.');
  return value;
}

// 时间戳转化为 yyyy-mm-dd hh:mm:ss
export function timeFilter(value) {
  value = new Date(value);
  let num = value + '';
  let date = '';
  let month = new Array();
  month['Jan'] = '01';
  month['Feb'] = '02';
  month['Mar'] = '03';
  month['Apr'] = '04';
  month['May'] = '05';
  month['Jun'] = '06';
  month['Jul'] = '07';
  month['Aug'] = '08';
  month['Sep'] = '09';
  month['Oct'] = '10';
  month['Nov'] = '11';
  month['Dec'] = '12';
  let week = new Array();
  week['Mon'] = '一';
  week['Tue'] = '二';
  week['Wed'] = '三';
  week['Thu'] = '四';
  week['Fri'] = '五';
  week['Sat'] = '六';
  week['Sun'] = '日';
  let str = num.split(' ');
  date = str[3] + '-';
  date = date + month[str[1]] + '-' + str[2] + ' ' + str[4];
  return date;
}

// 商机状态，数字转换为文字
export function bussinessStatusChange(value) {
  switch (value) {
    case 1:
      value = '未处理';
      break;
    case 2:
      value = '已完成';
      break;
    case 3:
      value = '已拒绝';
    default:
      value = value;
  }
}

// 佣金结算，交易类型
export function commissionTypeFilter(value) {
  switch (value) {
    case 1:
      value = '线上';
      break;
    case 2:
      value = '线下';
      break;
    default:
      value = value;
  }
}

// 佣金结算，结算状态
export function commissionIsCloseFilter(value) {
  switch (value) {
    case 0:
      value = '未结算';
      break;
    case 1:
      value = '已结算';
      break;
    default:
      value = value;
  }
}

// 消费核销，支付状态
export function expenseStatusFilter(value) {
  switch (value) {
    case 1:
      value = '未支付';
      break;
    case 2:
      value = '已支付';
      break;
    case 3:
      value = '已退款';
      break;
    default:
      value = value;
  }
}
