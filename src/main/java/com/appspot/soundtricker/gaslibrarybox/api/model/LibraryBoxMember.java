package com.appspot.soundtricker.gaslibrarybox.api.model;

import java.util.Date;

public class LibraryBoxMember {
	
	private String memberKey;

	private String nickname;
    
    private String userIconUrl;
    
    private String url;
    
    private Date registeredAt;

    private Date modifitedAt;

	public final String getNickname() {
		return nickname;
	}

	public final void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public final String getUserIconUrl() {
		return userIconUrl;
	}

	public final void setUserIconUrl(String userIconUrl) {
		this.userIconUrl = userIconUrl;
	}

	public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	public final Date getRegisteredAt() {
		return registeredAt;
	}

	public final void setRegisteredAt(Date registeredAt) {
		this.registeredAt = registeredAt;
	}

	public final Date getModifitedAt() {
		return modifitedAt;
	}

	public final void setModifitedAt(Date modifitedAt) {
		this.modifitedAt = modifitedAt;
	}

	public String getMemberKey() {
		return memberKey;
	}

	public void setMemberKey(String key) {
		this.memberKey = key;
	}

}
