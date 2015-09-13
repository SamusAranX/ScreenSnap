# ScreenSnap
## A little, unobtrusive screenshot tool for BlackBerry OS 7

I recently acquired a BlackBerry 9900 Bold, but noticed that there was a distinct lack of still-functioning and free screenshot apps, so I set out to write my own.

## Usage

This is the main screen of ScreenSnap:

![Screenshot of ScreenSnap's main screen](https://cloud.githubusercontent.com/assets/676069/9834904/0b110a8c-59d0-11e5-8be0-e5c70191112f.png)

First, you’ll need to set your Convenience Key action to Do Nothing. ScreenSnap will listen to Convenience Key presses regardless, but this way you’ll avoid accidental app launches.
Then, install ScreenSnap and start it. From now on, ScreenSnap will always run in the background and autostart whenever you reboot your device.
Press the Convenience Key to take a screenshot.
It’ll either be saved to
`/home/user/pictures/Screenshots` or 
`/SDCard/BlackBerry/pictures/Screenshots`, depending on your settings.

## Things you should know before trying to build this yourself

You need to [request signing keys from RIM](https://www.blackberry.com/SignedKeys/codesigning.html) first. Unsigned, ScreenSnap will run in the simulator, but to run your build on a real device, you’ll need to sign it. ScreenSnap won’t be able to  take screenshots or save its settings otherwise.

## Downloads

Check the Releases tab for signed downloadable versions.

## Known Things That You Don’t Need To Open Issues For

* ScreenSnap can’t be completely closed as of now. This shouldn’t be an issue as it does literally nothing when not saving screenshots and thus, shouldn’t interfere with normal operation.
* Screenshots are saved as PNGs only. If this is a problem for you, I recommend getting a larger memory card. 
