# LearningAndroidAwareness
This demo is broken in different fragment scenarios to show how to interact with Android Awareness.

*  	HeadphoneSnapshotFragment (shows how to get the instante state of headphones plugged in or out from the device)
*   LocationSnapshotFragment (Similar to HeadphoneSnapshotFragment, but provides last location tracked by the device)
*   HeadphoneFenceFragment (shows how to create a fence to instruct Android Awareness to ping the app for headphone events)
*  	ComboFenceFragment (Similar to HeadphoneFenceFragment but combines fences with AND, OR, NOT clauses)
*   BackComboFenceFragment 
    * Works like ComboFenceFragment, but runs in the brackground
    * On reboot is capable of restarting the previous running awareness
    * Status appears in the notificatio bar
    * By clicking the notification, the user is taken to the fragment where the user can make edits, or simply stop the awareness.
