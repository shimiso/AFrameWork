package com.eshangke.framework.bean;

import java.io.Serializable;

public class Lesson implements Serializable {

	private static final long serialVersionUID = 1198857672325152750L;

	private String id;

	private String courseId;

	private String courseRealGuid;

	private String name;

	private int totalTime;

	private String userID;

	private int totalSize;

	private int sectionCount;

	private int hasHD;// 0 表示无高清 1表示有高清

	private int isPublish;// 0表示未发布 1表示发布

	private int lessonOrder;
	
	private int playingSectionIndex;
	
	private int offsetDurationInSection;

	private int isDownload;// 0 未开始下载 1 暂停 2正在下载 3下载完成 4 下载等待 5 下载失败

	private boolean selected;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getSectionCount() {
		return sectionCount;
	}

	public void setSectionCount(int sectionCount) {
		this.sectionCount = sectionCount;
	}

	public int getHasHD() {
		return hasHD;
	}

	public void setHasHD(int hasHD) {
		this.hasHD = hasHD;
	}

	public int getIsPublish() {
		return isPublish;
	}

	public void setIsPublish(int isPublish) {
		this.isPublish = isPublish;
	}

	public int getLessonOrder() {
		return lessonOrder;
	}

	public void setLessonOrder(int lessonOrder) {
		this.lessonOrder = lessonOrder;
	}

	public int getIsDownload() {
		return isDownload;
	}

	public void setIsDownload(int isDownload) {
		this.isDownload = isDownload;
	}

	public int getPlayingSectionIndex() {
		return playingSectionIndex;
	}

	public void setPlayingSectionIndex(int playingSectionIndex) {
		this.playingSectionIndex = playingSectionIndex;
	}

	public int getOffsetDurationInSection() {
		return offsetDurationInSection;
	}

	public void setOffsetDurationInSection(int offsetDurationInSection) {
		this.offsetDurationInSection = offsetDurationInSection;
	}

	public String getCourseRealGuid() {
		return courseRealGuid;
	}

	public void setCourseRealGuid(String courseRealGuid) {
		this.courseRealGuid = courseRealGuid;
	}
}
