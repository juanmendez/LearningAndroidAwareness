# LearningGoogleAwareness
This demo is broken in different fragment scenarios to show how to interact with [Google Awareness](https://developers.google.com/awareness/).

*   HeadphoneSnapshotFragment (shows how to get a [snapshot](https://developers.google.com/awareness/android-api/snapshot-api-overview) if headphones are plugged or not)
*   LocationSnapshotFragment (Similar to HeadphoneSnapshotFragment, but provides last location tracked by the device)
*   HeadphoneFenceFragment (shows how to create a [awarenessFence](https://developers.google.com/awareness/android-api/awarenessFence-create) to instruct Android Awareness to ping the app for on events)
*  	ComboFenceFragment (Similar to HeadphoneFenceFragment but combines fences with AND, OR, NOT clauses)
      * User can include a radius distance to make a awarenessFence based on current location
      * User has also the option to include along with distance, if headphones should be plugged in. It's kind of silly.
*   BackComboFenceFragment 
    * Works like ComboFenceFragment, but runs in the background so the app doesn't have to be present.
    * On reboot is capable of restarting the previous running awarenessFence
    * Last status appears in the notification bar
    * By clicking the notification, the user is taken to the fragment where the user can make edits, or simply stop the awarenessFence.

[Sign up to get your key](https://developers.google.com/awareness/android-api/get-a-key) then create a new file under the `app` folder as `awareness.properties` then update with `AWARENESS_API=YOUR_SECRET_KEY`

It has been a ton of fun doing this demo using [Android Annotations](http://androidannotations.org/)
