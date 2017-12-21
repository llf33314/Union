export const unionIdChange = (state, id) => {
  state.unionId = id;
  sessionStorage.unionId = id;
}
export const unionMemberIdChange = (state, id) => {
  state.unionMemberId = id;
  sessionStorage.unionMemberId = id;
}
export const latitudeChange = (state, value) => {
  state.addressLatitude = value;
}
export const longitudeChange = (state, value) => {
  state.addressLongitude = value;
}
// 点击地址
export const enterpriseAddressChange = (state, value) => {
  state.enterpriseAddress = value;
}
// 是否盟主
export const isUnionOwnerChange = (state, value) => {
  state.isUnionOwner = value;
}

