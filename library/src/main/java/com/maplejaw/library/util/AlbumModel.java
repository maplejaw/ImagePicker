package com.maplejaw.library.util;

public class AlbumModel {//相册类

	private String name;//相册名
	private int count;//相片数
	private String recent;//最近一张照片的地址


	public AlbumModel() {
		super();
	}
	
	public AlbumModel(String name) {
		this.name = name;
	}

	public AlbumModel(String name, int count, String recent) {
		super();
		this.name = name;
		this.count = count;
		this.recent = recent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getRecent() {
		return recent;
	}

	public void setRecent(String recent) {
		this.recent = recent;
	}


	public void increaseCount() {
		count++;
	}

}
