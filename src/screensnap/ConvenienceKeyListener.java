package screensnap;

import java.io.IOException;

import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;

public final class ConvenienceKeyListener implements KeyListener {

    public boolean keyChar(char key, int status, int time) {
        return false;
    }

    public boolean keyDown(int keycode, int time) {
        if (Keypad.KEY_CONVENIENCE_1 == Keypad.key(keycode)) {
        	System.out.println("ScreenSnap: Convenience key pressed");
        	
        	try {
				if (((ScreenSnapApp)UiApplication.getUiApplication()).takeScreenshot()) {
					System.out.println("ScreenSnap: Screenshot taken.");
				} else {
					System.out.println("ScreenSnap: Couldn't take screenshot.");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("ScreenSnap: " + e.getMessage());
				e.printStackTrace();
				System.out.println("ScreenSnap: Error, couldn't take screenshot.");
			}
        	
            return true;
        }

        return false;
    }

    public boolean keyRepeat(int keycode, int time) {
        return false;
    }

    public boolean keyStatus(int keycode, int time) {
        return false;
    }

    public boolean keyUp(int keycode, int time) {
        return false;
    }
}