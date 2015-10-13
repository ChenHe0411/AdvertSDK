package com.ymzz.plat.alibs.util;

public class DownloadMission {
	
	private String name;
	private String icon;
	private String url;
	private String id;
	private boolean hid = false;
	public boolean getHid() {
		return hid;
	}
	public void setHid(boolean hid) {
		this.hid = hid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
