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
import net.rim.device.api.ui.UiApplication;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class ScreenSnapApp extends UiApplication {
   
	public PersistentStoreHelper persistentStore;
    public static void main(String[] args) {
        ScreenSnapApp theApp = new ScreenSnapApp();       
        theApp.enterEventDispatcher();
    }

    public boolean mediaCardPresent = false;
    public ScreenSnapApp() {
    	// Is there a media card inserted?
    	Enumeration e = FileSystemRegistry.listRoots();
    	while (e.hasMoreElements() && !mediaCardPresent)
    	   mediaCardPresent = ((String)e.nextElement()).equalsIgnoreCase("sdcard/");
    	
    	System.out.println("ScreenSnap: Media Card is present: " + mediaCardPresent);

		persistentStore = PersistentStoreHelper.getInstance();
		
        pushScreen(new ConfigScreen());
        
        addKeyListener(new ConvenienceKeyListener()); // This is what makes the convenience key take a screenshot
    }   
    
    public void activate() {
    	System.out.println("ScreenSnap: activate()");
    }
    
    public void deactivate() {
    	System.out.println("ScreenSnap: deactivate()");
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
    	
    	// https://supportforums.blackberry.com/t5/Java-Development/Supported-System-getProperty-keys/ta-p/445219
    	String devicePath = System.getProperty("fileconn.dir.photos") + "Screenshots/";
    	String mediaCardPath = System.getProperty("fileconn.dir.memorycard.photos") + "Screenshots/";
    	
    	Object storeOnCardObj = persistentStore.get("storeOnCard");
    	boolean storeOnCard = false;
    	if(storeOnCardObj != null)
    		storeOnCard = ((Integer)storeOnCardObj).intValue() == 1;
   
		String finalPath = storeOnCard && mediaCardPresent ? mediaCardPath : devicePath;  // Are storeOnCard and mediaCardPresent == true? If so, use mediaCardPath. Use devicePath otherwise.
		
		// Create the Screenshots folder if it doesn't exist already
		// This might get weird if there's a folder with the same name, but in different casing
		// Blackberry OS 7 file systems do not seem to be case sensitive at all
    	FileConnection fldr = (FileConnection)Connector.open(finalPath);
    	if(!fldr.exists()) {
    		fldr.mkdir();
    		fldr.close();
    	}

    	// Increment a number in the file name and try again if there is a file already.
    	int nameCounter = 0;
		String strCounter = nameCounter == 0 ? "" : "-" + Integer.toString(nameCounter);
		String filePath = finalPath + dateString + strCounter + ".png";
		System.out.println("ScreenSnap: " + filePath);
    	FileConnection fc = (FileConnection)Connector.open(filePath);
		while(fc.exists()) {
			System.out.println(filePath + " already exists");
			fc.close();
			
			nameCounter++;
			strCounter = nameCounter == 0 ? "" : "-" + Integer.toString(nameCounter);
			filePath = finalPath + dateString + strCounter + ".png";
	    	fc = (FileConnection)Connector.open(filePath);
		}
		System.out.println("Settled on " + filePath);

    	// Create the file.
    	fc.create();

    	// Write out the data to the file.
    	DataOutputStream out = fc.openDataOutputStream();
    	out.write(png.getData());

    	// Close all Connections.
    	out.close();
    	fc.close();
    	
    	return true;
    } 
}
