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
public class GasLibrary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;
    
    @Attribute(unindexed=true)
    private String sourceUrl;
    
    @Attribute
    private String label;

	@Attribute(unindexed=true)
    private String desc;
	
	@Attribute(unindexed=true)
	private String longDesc;

	@Attribute(listener=CreationDate.class)
    private Date registeredAt;

	@Attribute(listener=ModificationDate.class)
    private Date modifitedAt;

	@Attribute
    private String authorName;

	@Attribute(unindexed=true)
    private String authorUrl;

	@Attribute(unindexed=true)
    private User author;
	
	@Attribute(unindexed=true)
	private String authorIconUrl;

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
        GasLibrary other = (GasLibrary) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

	public User getAuthor() {
		return author;
	}

	public String getAuthorName() {
		return authorName;
	}

	public String getAuthorUrl() {
		return authorUrl;
	}

	public String getDesc() {
		return desc;
	}

	/**
     * Returns the key.
     *
     * @return the key
     */
    public Key getKey() {
        return key;
    }

	public String getLabel() {
		return label;
	}

	public Date getModifitedAt() {
		return modifitedAt;
	}

	public Date getRegisteredAt() {
		return registeredAt;
	}

	public String getSourceUrl() {
		return sourceUrl;
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
    
    public void setAuthor(User author) {
		this.author = author;
	}
    
    public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
    
    public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
    
    public void setDesc(String desc) {
		this.desc = desc;
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

    public void setLabel(String label) {
		this.label = label;
	}

    public void setModifitedAt(Date modifitedAt) {
		this.modifitedAt = modifitedAt;
	}

    public void setRegisteredAt(Date registeredAt) {
		this.registeredAt = registeredAt;
	}

    public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
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
}
