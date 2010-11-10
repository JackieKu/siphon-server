package org.ku.siphon;

import java.nio.charset.Charset;
import java.security.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.KeyFactory;

public class User {
	private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
	private static final Charset CHARSET = Charset.forName("UTF-8");

	private Entity entity;

	public User(String user, String password) throws NoSuchAlgorithmException {
		entity = new Entity(KeyFactory.createKey(User.class.getSimpleName(), user));
		setPassword(password);
	}

	public User(String user) throws EntityNotFoundException {
		entity = datastoreService.get(KeyFactory.createKey(User.class.getSimpleName(), user));
	}

	public boolean checkPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		return MessageDigest.isEqual(md.digest(password.getBytes(CHARSET)), ((Blob) entity.getProperty("password")).getBytes());
	}

	public void setPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		entity.setUnindexedProperty("password", new Blob(md.digest(password.getBytes(CHARSET))));
	}

	public String getAddons() {
		Text text = (Text) entity.getProperty("addons");
		if (text == null)
			return "";
		return text.getValue();
	}

	public void setAddons(String addons) {
		entity.setUnindexedProperty("addons", new Text(addons));
	}

	public void store() {
		datastoreService.put(entity);
	}
}
