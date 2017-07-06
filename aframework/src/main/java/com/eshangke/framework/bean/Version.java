package com.eshangke.framework.bean;

import java.io.Serializable;

/**
 * 版本信息
 */
public class Version implements Serializable {
	private static final long serialVersionUID = 1L;
	private int versionCode;
	private String versionName;
	private String apkUrl;
	private String md5;
	private boolean success;
	private String apkSize;
	private boolean isUpgrade;
	private boolean forcedUpdate;
	private String updateInfo;
	private String message;

	public Integer getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getApkSize() {
		return apkSize;
	}

	public void setApkSize(String apkSize) {
		this.apkSize = apkSize;
	}

	public boolean isUpgrade() {
		return isUpgrade;
	}

	public void setUpgrade(boolean isUpgrade) {
		this.isUpgrade = isUpgrade;
	}

	public boolean isForcedUpdate() {
		return forcedUpdate;
	}

	public void setForcedUpdate(boolean forcedUpdate) {
		this.forcedUpdate = forcedUpdate;
	}

	public String getUpdateInfo() {
		return updateInfo;
	}

	public void setUpdateInfo(String updateInfo) {
		this.updateInfo = updateInfo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
