import { getStrLength } from '@/utils/length-check.js';

// // 联系电话
// export function directorPhonePass(rule, value, callback) {
//   if (!value) {
//     callback(new Error('联系电话不能为空，请重新输入'));
//   } else if (!value.match(/(^1[3|4|5|6|7|8][0-9][0-9]{8}$)|(^0\d{2,3}-?\d{7,8}$)/)) {
//     callback(new Error('请输入正确的联系电话'));
//   } else {
//     callback();
//   }
// }

// 手机
export function cellPhonePass(rule, value, callback) {
  if (!value) {
    callback(new Error('手机号码不能为空，请重新输入'));
  } else if (!value.match(/^1[3|4|5|6|7|8][0-9][0-9]{8}$/)) {
    callback(new Error('请输入正确的手机号码'));
  } else {
    callback();
  }
}

// 邮箱
export function emailPass(rule, value, callback) {
  if (!value) {
    callback(new Error('邮箱不能为空，请重新输入'));
  } else if (!value.match(/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/)) {
    callback(new Error('请输入正确的邮箱'));
  } else {
    callback();
  }
}

// 积分折扣率
export function integralExchangeRatioPass(rule, value, callback) {
  if (value !== 0 && !value) {
    callback(new Error('积分折扣率内容不能为空，请重新输入'));
  } else if (isNaN(value)) {
    callback(new Error('积分折扣率必须为数字值，请重新输入'));
  } else if (value < 0) {
    callback(new Error('积分折扣率不能小于0%，请重新输入'));
  } else if (value > 30) {
    callback(new Error('积分折扣率不能大于30%，请重新输入'));
  } else {
    callback();
  }
}

// 价格 最低1元
export function pricePass(rule, value, callback) {
  if (value !== 0 && !value) {
    callback(new Error('价格不能为空，请重新输入'));
  } else if (isNaN(value)) {
    callback(new Error('价格必须为数字值，请重新输入'));
  } else if (value < 1) {
    callback(new Error('价格不能小于1元，请重新输入'));
  } else {
    callback();
  }
}

// 客户姓名 限制20个字符
export function clientNamePass(rule, value, callback) {
  if (!value) {
    callback(new Error('意向客户姓名不能为空，请重新输入'));
  } else if (getStrLength(value) > 10) {
    callback(new Error('意向客户姓名在10个字以内'));
  } else {
    callback();
  }
}

// 企业名称 限制30个字符
export function enterpriseNamePass(rule, value, callback) {
  if (!value) {
    callback(new Error('企业名称不能为空，请重新输入'));
  } else if (getStrLength(value) > 15) {
    callback(new Error('企业名称在15个字以内'));
  } else {
    callback();
  }
}

// 负责人 限制20个字符
export function directorNamePass(rule, value, callback) {
  if (!value) {
    callback(new Error('负责人不能为空，请重新输入'));
  } else if (getStrLength(value) > 10) {
    callback(new Error('负责人在10个字以内'));
  } else {
    callback();
  }
}

// 业务备注 限制100个字符
export function businessMsgPass(rule, value, callback) {
  if (!value) {
    callback(new Error('业务备注不能为空，请重新输入'));
  } else if (getStrLength(value) > 50) {
    callback(new Error('业务备注在50个字以内'));
  } else {
    callback();
  }
}

// 理由 限制100个字符
export function reasonPass(rule, value, callback) {
  if (!value) {
    callback(new Error('理由不能为空，请重新输入'));
  } else if (getStrLength(value) > 50) {
    callback(new Error('理由在50个字以内'));
  } else {
    callback();
  }
}

// 活动卡名称 限制20个字符
export function activityCardNamePass(rule, value, callback) {
  if (!value) {
    callback(new Error('活动卡名称不能为空，请重新输入'));
  } else if (getStrLength(value) > 10) {
    callback(new Error('活动卡名称在10个字以内'));
  } else {
    callback();
  }
}

// 活动卡说明 限制250个字符
export function activityCardIllustrationPass(rule, value, callback) {
  if (!value) {
    callback(new Error('活动卡说明不能为空，请重新输入'));
  } else if (getStrLength(value) > 125) {
    callback(new Error('活动卡说明在125个字以内'));
  } else {
    callback();
  }
}

// 联盟名称 限制20个字符
export function unionNamePass(rule, value, callback) {
  if (!value) {
    callback(new Error('联盟名称不能为空，请重新输入'));
  } else if (getStrLength(value) > 10) {
    callback(new Error('联盟名称在10个字以内'));
  } else {
    callback();
  }
}

// 联盟说明 限制60个字符
export function unionIllustrationPass(rule, value, callback) {
  if (!value) {
    callback(new Error('联盟说明不能为空，请重新输入'));
  } else if (getStrLength(value) > 30) {
    callback(new Error('联盟说明在30个字以内'));
  } else {
    callback();
  }
}
