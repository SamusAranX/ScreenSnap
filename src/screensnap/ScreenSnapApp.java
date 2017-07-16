package screensnap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PNGEncodedImage;
import net.rim.device.api.ui.ConvenienceKeyUtilities;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.system.LED;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class ScreenSnapApp extends UiApplication {
   
    public static void main(String[] args) {
        ScreenSnapApp theApp = new ScreenSnapApp();       
        theApp.enterEventDispatcher();
    }

    public boolean mediaCardPresent = false;
	public PersistentStoreHelper persistentStore;
    Dialog notification = null;
    public ScreenSnapApp() {
    	// Is there a media card inserted?
    	Enumeration e = FileSystemRegistry.listRoots();
    	while (e.hasMoreElements() && !mediaCardPresent)
    	   this.mediaCardPresent = ((String)e.nextElement()).equalsIgnoreCase("sdcard/");
    	
    	System.out.println("ScreenSnap: Media Card is present: " + this.mediaCardPresent);

		this.persistentStore = PersistentStoreHelper.getInstance();
		
        this.pushScreen(new ConfigScreen());
        
        // Nowhere does BlackBerry's own documentation state that getConvenienceKeyAppName() can return null
        // The next five lines of code took me hours to debug
        // What certainly didn't help is the complete lack of stack traces I got from the OS
        String convenienceKeyBoundApp = ConvenienceKeyUtilities.getConvenienceKeyAppName(Keypad.KEY_CONVENIENCE_1);
        if (convenienceKeyBoundApp != null) {
        	this.showDialog("Please set the Convenience Key action to \"Do Nothing\" to avoid unwanted app launches.");
        	System.out.println("ScreenSnap (Convenience Key): " + convenienceKeyBoundApp);
     	}
        
        this.addKeyListener(new ConvenienceKeyListener()); // This is what makes the convenience key take a screenshot
    }   
    
    public void activate() {
    	// Called when app is put into the foreground again
    	System.out.println("ScreenSnap: activate()");
    }
    
    public void deactivate() {
    	// Called when app is put into the background
    	System.out.println("ScreenSnap: deactivate()");
    }
    
    public boolean getSetting(String key) {
    	Object settingObj = this.persistentStore.get(key);
    	if(settingObj != null) {
    		return ((Integer)settingObj).intValue() == 1;
    	}
    	return false;
    }
    
    public void putSettingAndCommit(String key, Object value) {
		this.persistentStore.put(key, value);
		this.persistentStore.commit();
    }
    
    public void showDialog(String message) {
    	// If there is a dialog being shown already, close it
		if(this.notification != null)
    		this.notification.close();
    	
		// ...then display another one.
    	this.notification = new Dialog(Dialog.D_OK, message, 0, null, 0);
    	synchronized (UiApplication.getEventLock()) {
    		UiEngine ui = Ui.getUiEngine();
    		ui.pushGlobalScreen(this.notification, 1, UiEngine.GLOBAL_QUEUE);
    	}
    }
    
    public boolean enumContainsString(Enumeration e, String s) {
    	while (e.hasMoreElements()) {
    	    String testStr = (String)e.nextElement();
    	    boolean strComp = testStr.equalsIgnoreCase(s);
    	    
    	    if (strComp)
    	    	return true;
    	}
    	return false;
    }
    
    public boolean takeScreenshot() throws IOException {
    	System.out.println("ScreenSnap: Taking Screenshot");
    	// Get the dimensions of the screen.
    	int width = Display.getWidth();
    	int height = Display.getHeight();

    	// Create a bitmap to store the screen capture in.
    	Bitmap bm = new Bitmap(width, height);
    	Display.screenshot(bm);

    	// Convert the Bitmap object to a PNG.
    	PNGEncodedImage png = PNGEncodedImage.encode(bm);
    	
    	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	String dateString = formatter.format(new Date());
    	
    	// https://community.blackberry.com/docs/DOC-10075
    	String devicePath = System.getProperty("fileconn.dir.photos") + "Screenshots/";
    	String mediaCardPath = System.getProperty("fileconn.dir.memorycard.photos") + "Screenshots/";
    	
    	// Check whether the "save on media card" option is enabled
    	boolean storeOnCard = this.getSetting(ConfigScreen.SETTINGS_KEY_MEDIA_CARD);
   
    	// If it is and a media card is present, use mediaCardPath; use devicePath otherwise.
		String finalDir = storeOnCard && this.mediaCardPresent ? mediaCardPath : devicePath; 
		System.out.println("ScreenSnap: finalDir is " + finalDir);
		
		// Create the Screenshots folder if it doesn't exist already
		// This might get weird if there's a folder with the same name, but in different casing
		// Blackberry OS 7 file systems do not seem to be case sensitive at all
    	// ADDENDUM 15.07.2017: This can (and probably will) fail if the device is connected to a computer
    	// The file system is inaccessible as long as the device is connected
    	FileConnection fldr = (FileConnection)Connector.open(finalDir);
    	if(!fldr.canWrite()) {
    		this.showDialog("Error taking a screenshot: File system is not accessible");
    		return false; // The file system is not accessible, abort
    	}
    	
    	if(!fldr.exists()) {
    		fldr.mkdir();
    	}

    	// Construct a file name, print it out, and create a FileConnection object
    	int nameCounter = 0;
		String strCounter = "";
		String finalPath = "";
    	
    	Enumeration existingFiles = fldr.list(dateString + "*.png", false);    	
		String testingFileName = dateString + strCounter + ".png";
    	do {
    		// Check if our file name of choice already exists
    		boolean fileAlreadyExists = this.enumContainsString(existingFiles, testingFileName);
    		if (!fileAlreadyExists) {
    			// If it doesn't, construct a new final screenshot file path and break out of the loop
    			finalPath = finalDir + testingFileName;
    			break;
    		}
    		
    		System.out.println("ScreenSnap: " + testingFileName + " exists already");
    		
    		// If so, increment the name counter and try again
    		nameCounter++;
			strCounter = nameCounter == 0 ? "" : "-" + Integer.toString(nameCounter);
			testingFileName = dateString + strCounter + ".png";
    	} while (true);
    	
		System.out.println("ScreenSnap: Settled on " + finalPath);

		FileConnection fc = (FileConnection)Connector.open(finalPath);
    	// Create the file.
    	fc.create();

    	// Write out the data to the file.
    	DataOutputStream out = fc.openDataOutputStream();
    	out.write(png.getData());

    	// Close all Connections.
    	out.close();
    	fc.close();
		fldr.close();
    	
    	// Check whether the "show dialogs" option is enabled
		boolean displayDialogs = this.getSetting(ConfigScreen.SETTINGS_KEY_SHOW_DIALOGS);
    	if(displayDialogs) {
    		// If so, show a dialog with the file path
    		this.showDialog("Saved screenshot to " + finalPath);
    	}
    	
		boolean flashLED = this.getSetting(ConfigScreen.SETTINGS_KEY_FLASH_LED);
		
		// Check if the device has a status LED and the "flash LED" option is enabled
    	if(LED.isSupported(LED.LED_TYPE_STATUS) && flashLED) {
    		final int blinkIntervalInMS = 100;
    		final int blinkColor = 0x00FF00FF; // 0x00RRGGBB
    		
    		// Check if the LED supports colors and act accordingly
    		if (LED.isPolychromatic(LED.LED_TYPE_STATUS)) {
    			LED.setColorConfiguration(blinkIntervalInMS, blinkIntervalInMS, blinkColor);
    		} else {
    			LED.setConfiguration(LED.LED_TYPE_STATUS, blinkIntervalInMS, blinkIntervalInMS, LED.BRIGHTNESS_100);
    		}
    		
    		// This thread will turn the LED off after two blinks
    		// I could do .setColorPattern instead, but that doesn't support devices without color LEDs
    		new Thread(new Runnable() {
    	        public void run() {
    	            try {
    	            	Thread.sleep(blinkIntervalInMS * 3); 
    	            	LED.setState(LED.LED_TYPE_STATUS, LED.STATE_OFF);
    	            }
    	            catch (InterruptedException ie) {}
    	        }
    	    }).start();
    	}
    	
    	return true;
    } 
}
