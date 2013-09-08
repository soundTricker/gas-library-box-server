package com.appspot.soundtricker.gaslibrarybox.api.model;

import java.util.Date;

public class Library {
	private String libraryKey;
	private String label;
	private String desc;
	private String longDesc;
	private Date registeredAt;
	private Date modifiedAt;
	private String authorName;
	private String authorUrl;
	private String authorKey;
	
	/**
	 * get library key
	 * @return library key
	 */
	public final String getLibraryKey() {
		return libraryKey;
	}
	public final void setLibraryKey(String key) {
		this.libraryKey = key;
	}
	public final String getLabel() {
		return label;
	}
	public final void setLabel(String label) {
		this.label = label;
	}
	public final String getDesc() {
		return desc;
	}
	public final void setDesc(String desc) {
		this.desc = desc;
	}
	public final Date getRegisteredAt() {
		return registeredAt;
	}
	public final void setRegisteredAt(Date registeredAt) {
		this.registeredAt = registeredAt;
	}
	public final Date getModifiedAt() {
		return modifiedAt;
	}
	public final void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	public final String getAuthorName() {
		return authorName;
	}
	public final void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public final String getAuthorUrl() {
		return authorUrl;
	}
	public final void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	public final String getSourceUrl() {
		return sourceUrl;
	}
	public final void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getLongDesc() {
		return longDesc;
	}
	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}
	public String getAuthorIconUrl() {
		return authorIconUrl;
	}
	public void setAuthorIconUrl(String authorIconUrl) {
		this.authorIconUrl = authorIconUrl;
	}
	public String getAuthorKey() {
		return authorKey;
	}
	public void setAuthorKey(String authorKey) {
		this.authorKey = authorKey;
	}
	private String sourceUrl;
	private String authorIconUrl;

}
