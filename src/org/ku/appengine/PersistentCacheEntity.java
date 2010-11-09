package org.ku.appengine;

import com.google.appengine.api.datastore.*;

public class PersistentCacheEntity {
	static final String PROPERTY_VALUE = "v";
	static final String PROPERTY_ACCESS_TIME = "a";
	static final String PROPERTY_UPDATE_TIME = "m";

	private Entity entity;

	PersistentCacheEntity(Key key) {
		this.entity = new Entity(key);
	}

	PersistentCacheEntity(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setValue(Object value) {
		entity.setProperty(PROPERTY_VALUE, value);
	}

	public Object getValue() {
		return entity.getProperty(PROPERTY_VALUE);
	}

	public void setAccessTime(long msec) {
		entity.setProperty(PROPERTY_ACCESS_TIME, msec);
	}

	public void setAccessTime() {
		this.setAccessTime(System.currentTimeMillis());
	}

	public long getAccessTime() {
		return (Long) entity.getProperty(PROPERTY_ACCESS_TIME);
	}

	public void setUpdateTime(long msec) {
		entity.setProperty(PROPERTY_UPDATE_TIME, msec);
	}

	public void setUpdateTime() {
		this.setUpdateTime(System.currentTimeMillis());
	}

	public long getUpdateTime() {
		return (Long) entity.getProperty(PROPERTY_UPDATE_TIME);
	}
}
