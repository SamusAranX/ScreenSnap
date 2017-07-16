package screensnap;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.DrawTextParam;
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
	
	CheckboxField cbf1, cbf2, cbf3;
	ButtonField bbf;
	RichTextField footerField, versionInfo;
	
	public static final String SETTINGS_KEY_MEDIA_CARD = "storeOnCard";
	public static final String SETTINGS_KEY_SHOW_DIALOGS = "displayDialogs";
	public static final String SETTINGS_KEY_FLASH_LED = "flashLED";
	
	public ConfigScreen() {
		// Get the app name  
		String appName = ApplicationDescriptor.currentApplicationDescriptor().getModuleName();
		String appVersion = ApplicationDescriptor.currentApplicationDescriptor().getVersion();

		// Set up a standard title bar
		StandardTitleBar sTitleBar = new StandardTitleBar().addTitle(appName).addClock().addNotifications().addSignalIndicator();
		sTitleBar.setPropertyValue(StandardTitleBar.PROPERTY_BATTERY_VISIBILITY, StandardTitleBar.BATTERY_VISIBLE_LOW_OR_CHARGING);
		this.setTitleBar(sTitleBar);

		// Get the system font and set up a little footer label
		Font systemFont = Font.getDefault().derive(Font.PLAIN, 7, Ui.UNITS_pt);
		this.footerField = new RichTextField("ScreenSnap, a thingy by Peter Wunder" + "\n" + "https://peterwunder.de", RichTextField.TEXT_JUSTIFY_HCENTER|Field.NON_FOCUSABLE);
		this.footerField.setPadding(6, 0, 6, 0);
		this.footerField.setFont(systemFont, true);
		this.setStatus(footerField); // setStatus() refers to the status area of a MainScreen, which essentially is a field that's docked at the bottom

		VerticalFieldManager vfm = new VerticalFieldManager();

		String mediaCardName = System.getProperty("fileconn.dir.memorycard.name"); // Get the media card's name, if there is one
		// I'm not actually sure what happens if there is no memory card, as I'm too lazy to take my phone apart to remove it

		/*
		* Stuff for the first CheckboxField
		* This one will set whether screenshots get saved on the device or the media card
		*/
		boolean storeOnCard = ((ScreenSnapApp)UiApplication.getUiApplication()).getSetting(ConfigScreen.SETTINGS_KEY_MEDIA_CARD);
    	String cbf1Text = ((ScreenSnapApp)UiApplication.getUiApplication()).mediaCardPresent ? "Store screenshots on " + mediaCardName : "No media card present";
    	this.cbf1 = new CheckboxField(cbf1Text, storeOnCard); // Initialize a check box with the previously loaded value
		this.cbf1.setEditable(((ScreenSnapApp)UiApplication.getUiApplication()).mediaCardPresent); // there's not much point to this if there is no media card inserted
		this.cbf1.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
        		CheckboxField cbf = (CheckboxField)field;

        		// Save the new value
        		((ScreenSnapApp)UiApplication.getUiApplication()).putSettingAndCommit(ConfigScreen.SETTINGS_KEY_MEDIA_CARD, new Integer(cbf.getChecked() ? 1 : 0));
            }
        });
		
		/*
		* Stuff for the second CheckboxField
		* This one sets if dialogs get shown or not
		*/
		boolean displayDialogs = ((ScreenSnapApp)UiApplication.getUiApplication()).getSetting(ConfigScreen.SETTINGS_KEY_SHOW_DIALOGS);
		this.cbf2 = new CheckboxField("Display dialogs after taking screenshots", displayDialogs); // Initialize a check box with the previously loaded value
		this.cbf2.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
        		CheckboxField cbf = (CheckboxField)field;

        		// Save the new value
        		((ScreenSnapApp)UiApplication.getUiApplication()).putSettingAndCommit(ConfigScreen.SETTINGS_KEY_SHOW_DIALOGS, new Integer(cbf.getChecked() ? 1 : 0));
            }
        });
		
		/*
		* Stuff for the third CheckboxField
		* This one sets if the LED flashes after a screenshot was taken
		*/
		boolean flashLED = ((ScreenSnapApp)UiApplication.getUiApplication()).getSetting(ConfigScreen.SETTINGS_KEY_FLASH_LED);
		this.cbf3 = new CheckboxField("Flash the LED after taking screenshots", flashLED); // Initialize a check box with the previously loaded value
		this.cbf3.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
        		CheckboxField cbf = (CheckboxField)field;

        		// Save the new value
        		((ScreenSnapApp)UiApplication.getUiApplication()).putSettingAndCommit(ConfigScreen.SETTINGS_KEY_FLASH_LED, new Integer(cbf.getChecked() ? 1 : 0));
            }
        });
		
		/*
		* Stuff for the exit button
		*/
		this.bbf = new ButtonField("Exit ScreenSnap", Field.FIELD_HCENTER) { // Center the button horizontally
			// Make sure the text inside the button is always readable
			// Without this, the text might get cut off when changing fonts while the app is running in the background
			public int getPreferredWidth() {
				DrawTextParam dtp = new DrawTextParam();
				dtp.iTruncateWithEllipsis = DrawTextParam.NO_TRUNCATE_WITH_ELLIPSIS;
				return this.getFont().measureText(getLabel(), 0, getLabel().length(), dtp, null);
			}
		};
		this.bbf.setMargin(8, 0, 0, 0); // Give the button a bit of top margin
		this.bbf.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
        		System.exit(0); // Actually exit ScreenSnap
            }
        });
		
		/*
		* Version information label
		*/
		this.versionInfo = new RichTextField("Version " + appVersion, RichTextField.TEXT_JUSTIFY_HCENTER|Field.NON_FOCUSABLE);
		this.versionInfo.setPadding(8, 0, 0, 0);

		// Add everything to the screen
		vfm.add(this.cbf1);
		vfm.add(this.cbf2);
		vfm.add(this.cbf3);
		vfm.add(this.bbf);
		vfm.add(this.versionInfo);
		this.add(vfm);
		
		// gotta do this after everything's been added to this screen because otherwise it'll segfault
		if(this.cbf1.isEditable())
			this.cbf1.setFocus();
		else
			this.cbf2.setFocus();
	}
	
	// This gets called every time the focus state changes
	public void onFocusNotify(boolean focus) {
		System.out.println("ScreenSnap: onFocusNotify (" + String.valueOf(focus) +  ")");
		
		if (focus) {
			// If this gets the focus, reset the fonts of the footer and the version info label
			// These two elements are the only ones that don't automatically change their fonts on a system font change
			Font systemFont = Font.getDefault().derive(Font.PLAIN, 7, Ui.UNITS_pt);
			this.footerField.setFont(systemFont, true);
			this.versionInfo.setFont(systemFont, true);
		}
	}

	public boolean onClose() {
		System.out.println("ScreenSnap: onClose");
		UiApplication.getUiApplication().requestBackground(); // Don't actually close the app, just move it to the background
		
		return true;
	}
}
