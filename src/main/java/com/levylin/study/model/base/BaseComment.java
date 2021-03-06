package com.levylin.study.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseComment<M extends BaseComment<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setArticleId(java.lang.Integer articleId) {
		set("articleId", articleId);
	}

	public java.lang.Integer getArticleId() {
		return get("articleId");
	}

	public void setEmail(java.lang.String email) {
		set("email", email);
	}

	public java.lang.String getEmail() {
		return get("email");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return get("content");
	}

	public void setDateTime(java.util.Date dateTime) {
		set("dateTime", dateTime);
	}

	public java.util.Date getDateTime() {
		return get("dateTime");
	}

	public void setNick(java.lang.String nick) {
		set("nick", nick);
	}

	public java.lang.String getNick() {
		return get("nick");
	}

	public void setPId(java.lang.Integer pId) {
		set("pId", pId);
	}

	public java.lang.Integer getPId() {
		return get("pId");
	}

}
