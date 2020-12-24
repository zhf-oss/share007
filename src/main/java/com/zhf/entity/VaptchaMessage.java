package com.zhf.entity;

import java.io.Serializable;

/**
 * Vaptcha服务器端验证返回消息封装
 * @author Administrator
 *
 */
public class VaptchaMessage implements Serializable {

	private Integer success; //验证结果，1为通过，0为失败
	
	private Integer score; //可信度，区间[0, 100]
	
	private String msg; //信息描述

	public Integer getSuccess() {
		return success;
	}

	public void setSuccess(Integer success) {
		this.success = success;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
