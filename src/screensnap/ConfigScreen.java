package screensnap;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.StandardTitleBar;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.Font;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class ConfigScreen extends MainScreen implements FieldChangeListener
{
	public ConfigScreen() {
		// Get the app name  
		String appName = ApplicationDescriptor.currentApplicationDescriptor().getModuleName();

		// Set up a standard title bar
		StandardTitleBar sTitleBar = new StandardTitleBar().addTitle(appName).addClock().addNotifications().addSignalIndicator();
		sTitleBar.setPropertyValue(StandardTitleBar.PROPERTY_BATTERY_VISIBILITY, StandardTitleBar.BATTERY_VISIBLE_LOW_OR_CHARGING);
		setTitleBar(sTitleBar);

		// Get the system font and set up a little footer label
		Font systemFont = Font.getDefault().derive(Font.PLAIN, 6, Ui.UNITS_pt);
		RichTextField header = new RichTextField("ScreenSnap, a thingy by @SamusAranX", RichTextField.TEXT_JUSTIFY_HCENTER|Field.NON_FOCUSABLE);
		header.setPadding(6, 0, 6, 0);
		header.setFont(systemFont, true);
		this.setStatus(header); // setStatus() refers to the status area of a MainScreen, which essentially is a field that's docked at the bottom

		VerticalFieldManager vfm = new VerticalFieldManager();

		String mediaCardName = System.getProperty("fileconn.dir.memorycard.name"); // Get the media card's name, if there is one
		// I'm not actually sure what happens if there is no memory card, as I'm too lazy to take my phone apart to remove it

		Object storeOnCardObj = ((ScreenSnapApp)UiApplication.getUiApplication()).persistentStore.get("storeOnCard");
		boolean storeOnCard = false;
    	if(storeOnCardObj != null) // Inline ternary operators would not work here, for some reason
    		storeOnCard = ((Integer)storeOnCardObj).intValue() == 1;
    	
		boolean mediaCardPresent = ((ScreenSnapApp)UiApplication.getUiApplication()).mediaCardPresent; // Is there a media card inserted?

		CheckboxField cbf = new CheckboxField("Store screenshots on " + mediaCardName, storeOnCard); // Initialize a check box with the previously loaded value
		cbf.setChangeListener(this); // the listener method is at the bottom of the file
		cbf.setEnabled(mediaCardPresent); // there's not much point to this if there is no media card inserted

		// Add everything to the screen
		vfm.add(cbf);
		this.add(vfm);
		
		// gotta do this after everything's been added to this screen because otherwise it'll segfault
		cbf.setFocus();
	}

	public void onDisplay() {
		System.out.println("ScreenSnap: onDisplay");
	}

	public boolean onClose() {
		UiApplication.getUiApplication().requestBackground(); // Don't actually close the app, just move it to the background
		return true;
	}

	public void fieldChanged(Field field, int context) {
		System.out.println("ScreenSnap: field is null? " + field == null);
		CheckboxField cbf = (CheckboxField)field;

		// Save the new value
		((ScreenSnapApp)UiApplication.getUiApplication()).persistentStore.put("storeOnCard", new Integer(cbf.getChecked() ? 1 : 0));
		((ScreenSnapApp)UiApplication.getUiApplication()).persistentStore.commit();
	}
}
