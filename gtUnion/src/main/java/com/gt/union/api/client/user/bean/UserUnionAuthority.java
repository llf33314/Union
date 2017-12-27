package com.gt.union.api.client.user.bean;

/**
 * @author hongjiye
 * @time 2017-12-25 17:44
 **/
public class UserUnionAuthority {

	private Boolean authority;

	private Boolean pay;

	private String unionVersionName;

	public Boolean getAuthority() {
		return authority;
	}

	public void setAuthority(Boolean authority) {
		this.authority = authority;
	}

	public Boolean getPay() {
		return pay;
	}

	public void setPay(Boolean pay) {
		this.pay = pay;
	}

	public String getUnionVersionName() {
		return unionVersionName;
	}

	public void setUnionVersionName(String unionVersionName) {
		this.unionVersionName = unionVersionName;
	}
}
