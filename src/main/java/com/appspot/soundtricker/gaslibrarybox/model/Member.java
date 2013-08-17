package com.appspot.soundtricker.gaslibrarybox.model;

import java.io.Serializable;
import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@Model(schemaVersion = 1)
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;
    
    @Attribute(unindexed=true)
    private User user;
    
    @Attribute
    private String nickname;
    
    @Attribute(unindexed=true)
    private String userIconUrl;
    
    @Attribute(unindexed=true)
    private String url;
    
	@Attribute(listener=CreationDate.class)
    private Date registeredAt;

	@Attribute(listener=ModificationDate.class)
    private Date modifitedAt;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Member other = (Member) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

	/**
     * Returns the key.
     *
     * @return the key
     */
    public Key getKey() {
        return key;
    }

	public Date getModifitedAt() {
		return modifitedAt;
	}

	public String getNickname() {
		return nickname;
	}

	public Date getRegisteredAt() {
		return registeredAt;
	}

	public String getUrl() {
		return url;
	}

	public User getUser() {
		return user;
	}

	public String getUserIconUrl() {
		return userIconUrl;
	}

	/**
     * Returns the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

	/**
     * Sets the key.
     *
     * @param key
     *            the key
     */
    public void setKey(Key key) {
        this.key = key;
    }

	public void setModifitedAt(Date modifitedAt) {
		this.modifitedAt = modifitedAt;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

    public void setRegisteredAt(Date registeredAt) {
		this.registeredAt = registeredAt;
	}

    public void setUrl(String url) {
		this.url = url;
	}

    public void setUser(User user) {
		this.user = user;
	}

    public void setUserIconUrl(String userIconUrl) {
		this.userIconUrl = userIconUrl;
	}

    /**
     * Sets the version.
     *
     * @param version
     *            the version
     */
    public void setVersion(Long version) {
        this.version = version;
    }
}
