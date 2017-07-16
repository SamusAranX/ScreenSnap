# ScreenSnap
## A little, unobtrusive screenshot tool for BBOS 7

I recently acquired a BlackBerry 9900 Bold, but noticed that there was a distinct lack of still-functioning and free screenshot apps, so I set out to write my own.

## Usage

This is the main screen of ScreenSnap:

![Screenshot of ScreenSnap's main screen](https://apps.peterwunder.de/screenshots/screensnap1.png)
![Screenshot of ScreenSnap's optional dialog](https://apps.peterwunder.de/screenshots/screensnap2.png)

First, you’ll need to set your Convenience Key action to Do Nothing. ScreenSnap will listen to Convenience Key presses regardless, but this way you’ll avoid accidental app launches.
Then, install ScreenSnap and start it. From now on, ScreenSnap will always run in the background and autostart whenever you reboot your device.
Press the Convenience Key to take a screenshot.
It’ll either be saved to `/home/user/pictures/Screenshots` or `/SDCard/BlackBerry/pictures/Screenshots`, depending on your settings.

If you have dialog boxes enabled in ScreenSnap, it'll display a dialog with the path of the saved image.

It can also flash the status LED of the device (if present) when a screenshot is taken.

## Things you should know before trying to build this yourself

You'll need an installed BlackBerry development environment. If you're new to BlackBerry development or just don't have a working environment set up, download the pre-made one: http://developer.blackberry.com/bbos/java/download/#jde
You'll also need to [request signing keys from RIM](https://www.blackberry.com/SignedKeys/codesigning.html) first. Unsigned, ScreenSnap will run in the simulator, but to run your build on a real device, you’ll need to sign it. ScreenSnap won’t be able to do pretty much anything while unsigned.

## Downloads

Check the Releases tab for signed downloadable versions.

## Known Things (That Aren't Really Issues but Might Be Seen As Issues by Some People) You Don’t Need to Open Issues For

* Screenshots are saved as PNGs. There are no options to save screenshots in another format. If this is a problem for you, I recommend getting a larger memory card. ~~death to low-quality jpegs~~
* ScreenSnap may behave unpredictably if the device is connected to a PC in USB Drive mode. But considering that in that case, the file system is not accessible by the device, that shouldn't come as a surprise. The app can't save screenshots if there is nothing to save them to.
* Taking screenshots of the lock screen is currently not possible. This might never get fixed due to lack of documentation.