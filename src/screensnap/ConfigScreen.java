package screensnap;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.StandardTitleBar;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.Font;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class ConfigScreen extends MainScreen {
	
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

		/*
		* Stuff for the first CheckboxField
		*/
		Object storeOnCardObj = ((ScreenSnapApp)UiApplication.getUiApplication()).persistentStore.get("storeOnCard");
		boolean storeOnCard = false;
    	if(storeOnCardObj != null) // Inline ternary operators would not work here, for some reason
    		storeOnCard = ((Integer)storeOnCardObj).intValue() == 1;

		CheckboxField cbf1 = new CheckboxField("Store screenshots on " + mediaCardName, storeOnCard); // Initialize a check box with the previously loaded value
		cbf1.setEnabled(((ScreenSnapApp)UiApplication.getUiApplication()).mediaCardPresent); // there's not much point to this if there is no media card inserted
		cbf1.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
        		CheckboxField cbf = (CheckboxField)field;

        		// Save the new value
        		((ScreenSnapApp)UiApplication.getUiApplication()).persistentStore.put("storeOnCard", new Integer(cbf.getChecked() ? 1 : 0));
        		((ScreenSnapApp)UiApplication.getUiApplication()).persistentStore.commit();
            }
        });
		
		/*
		* Stuff for the second CheckboxField
		*/
		Object displayDialogsObj = ((ScreenSnapApp)UiApplication.getUiApplication()).persistentStore.get("displayDialogs");
		boolean displayDialogs = false;
    	if(displayDialogsObj != null) // Inline ternary operators would not work here, for some reason
    		displayDialogs = ((Integer)displayDialogsObj).intValue() == 1;
		
		CheckboxField cbf2 = new CheckboxField("Display dialogs when taking screenshots", displayDialogs); // Initialize a check box with the previously loaded value
		cbf2.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
        		CheckboxField cbf = (CheckboxField)field;

        		// Save the new value
        		((ScreenSnapApp)UiApplication.getUiApplication()).persistentStore.put("displayDialogs", new Integer(cbf.getChecked() ? 1 : 0));
        		((ScreenSnapApp)UiApplication.getUiApplication()).persistentStore.commit();
            }
        });
		
		/*
		* Stuff for the exit button
		*/
		ButtonField bbf = new ButtonField("Exit ScreenSnap", Field.FIELD_HCENTER); // Center the button horizontally
		bbf.setMargin(10, 0, 0, 0); // Give the button a bit of top margin
		bbf.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
        		System.exit(0); // Actually exit ScreenSnap
            }
        });

		// Add everything to the screen
		vfm.add(cbf1);
		vfm.add(cbf2);
		vfm.add(bbf);
		this.add(vfm);
		
		// gotta do this after everything's been added to this screen because otherwise it'll segfault
		cbf1.setFocus();
	}

	public void onDisplay() {
		System.out.println("ScreenSnap: onDisplay");
	}

	public boolean onClose() {
		System.out.println("ScreenSnap: onClose");
		UiApplication.getUiApplication().requestBackground(); // Don't actually close the app, just move it to the background
		
		return true;
	}
}
