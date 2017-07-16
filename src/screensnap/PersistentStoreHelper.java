package screensnap;

import java.util.Hashtable;

import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Persistable;

/** 
 * Use this class like so:
 * <code>
 *    PersistentStoreHelper store = PersistentStoreHelper.getInstance();
 *    Vector msgs = (Vector)store.get("chatMsgs");
 *    if (msgs == null) {
 *        msgs = new Vector();
 *        store.put("chatMsgs", msgs);
 *    }
 *    msgs.addElement("this is a new chat message");
 *    store.put("someOtherKey", someOtherValue);
 *    // commit will save changes both to the msgs list, and to `someOtherValue` added
 *    store.commit();
 * </code>
 */
public class PersistentStoreHelper implements Persistable {  // only Persistable so inner class can be!

	/** 
	 * persistentHashtable is now an instance of a class that
	 * only exists in your app 
	 */
	private MyAppsHashtable persistentHashtable;
	private PersistentObject persistentObject;

	/** PersistentStoreHelper is a singleton */
	private static PersistentStoreHelper instance = null;

	/** call this to get access to the methods of this class */
	public static PersistentStoreHelper getInstance() {
		if (instance == null) {
			instance = new PersistentStoreHelper();
		}
		return instance;
	}

	private long getLongKey(String key) {
		SHA1Digest sha1Digest = new SHA1Digest();
		sha1Digest.update(key.getBytes());
		byte[] hashValBytes = sha1Digest.getDigest();
		long hashValLong = 0;

		for(int i = 0; i < 8; ++i) {
			hashValLong |= ((long)(hashValBytes[i]) & 0x0FF) << (8*i);
		}

		return hashValLong;
	}

	/** constructor private to force users to call getInstance() */
	private PersistentStoreHelper() {
		final long KEY = getLongKey("com.peterwunder.ScreenSnap");
		persistentObject = PersistentStore.getPersistentObject(KEY);
		if (persistentObject.getContents() == null) {
			persistentHashtable = new MyAppsHashtable();
			persistentObject.setContents(persistentHashtable);
		} else {
			persistentHashtable = (MyAppsHashtable)persistentObject.getContents();
		}
	}
	
	/** use this method to retrieve a saved value from the store, by its key */
	public Object get(String key) {
		return persistentHashtable.get(key);
	}

	/** use this method to add a value to be saved in the store */
	public void put(String key, Object value) {
		persistentHashtable.put(key, value);
	}

	/** call this when you want to make sure all recent changes are saved to device storage */
	public void commit() {
		persistentObject.setContents(persistentHashtable);
		persistentObject.commit();
	}
	
	public void clear() {
		persistentHashtable.clear();
		persistentObject.setContents(persistentHashtable);
		persistentObject.commit();
	}

	private class MyAppsHashtable extends Hashtable implements Persistable {
		// don't need anything else ... the declaration does it all!
	}
}