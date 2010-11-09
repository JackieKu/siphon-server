package org.ku.appengine;

import javax.jdo.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

public final class Services {
	private Services() {
	}

	private static class DatastoreServiceSingleton {
		private static final DatastoreService instance = DatastoreServiceFactory.getDatastoreService();
	}

	public static DatastoreService getDatastoreService() {
		return DatastoreServiceSingleton.instance;
	}

	private static class MemcacheServiceSingleton {
		private static final MemcacheService instance = MemcacheServiceFactory.getMemcacheService();
	}

	public static MemcacheService getMemcacheService() {
		return MemcacheServiceSingleton.instance;
	}

	private static class PersistenceManagerFactorySingleton {
		private static final PersistenceManagerFactory instance =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");
	}

	public static PersistenceManagerFactory getPersistenceManagerFactory() {
		return PersistenceManagerFactorySingleton.instance;
	}
}
