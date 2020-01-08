package com.supyuan.modules.simulation.data;

import java.sql.Timestamp;
import java.util.Date;

public class SimDataVo {

	private int code;
	private double longitude;
	private double latitude;
	private int popuNum;
	private int count;
	private Date date;
	private Timestamp createDate;
	private int creator;
	
	public int getId() {
		return code;
	}
	public void setId(int id) {
		this.code = id;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getPopuNum() {
		return popuNum;
	}
	public void setPopuNum(int popuNum) {
		this.popuNum = popuNum;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public int getCreator() {
		return creator;
	}
	public void setCreator(int creator) {
		this.creator = creator;
	}
	
	@Override
	public String toString() {
		return "经度："+longitude + ";纬度：" + latitude + ";人数：" + popuNum + ";日期：" + date;
	}
}
