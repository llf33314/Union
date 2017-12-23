export function lengthCheck(inputValue, inputLength) {
  let valueLength = 0;
  let maxLenth = 0;
  let chinese = '[\u4e00-\u9fa5]'; // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
  let str = inputValue || '';
  for (let i = 0; i < str.length; i++) {
    // 获取一个字符
    let temp = str.substring(i, i + 1); // 判断是否为中文字符
    if (temp.match(chinese)) {
      // 中文字符长度为1
      valueLength += 1;
    } else {
      // 其他字符长度为0.5
      valueLength += 0.5;
    }
    if (Math.ceil(valueLength) == inputLength) {
      maxLenth = i;
    }
  }
  valueLength = Math.ceil(valueLength); //进位取整
  return [valueLength, maxLenth];
}
