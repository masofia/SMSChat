# Chat App

## Testing 

It is possible to send and receive SMS messages using the Emulator.

Receiving messages: Once the Emulator is running click on the 3 dots on the side menu and then "phone". 

Sending messages: Use 5554 as the phone number and SMS messages will get sent to itself.


## Encryption 

SMS has a max character limitation - https://en.wikipedia.org/wiki/SMS. This may cause issues sending the encrypted messages if they are too long and we may need to think about how to break them up.


## Open Items / Improvements:

- SMS messages sent and received showing up in other chat? Is it possible to mute it from the other app?
If not it should be fine since the messages displayed there will be encrypted anyway. 

"If the user has multiple SMS messaging apps installed on the Android phone, the App chooser will appear with a list of these apps, and the user can choose which one to use. (Android smartphones will have at least one, such as Messenger.)"
(https://google-developer-training.github.io/android-developer-phone-sms-course/Lesson%202/2_p_sending_sms_messages.html)

"You can manage SMS operations such as dividing a message into fragments, sending a multipart message, get carrier-dependent configuration values, and so on." -- will need to break up encrypted messages as they may be too long
(https://google-developer-training.github.io/android-developer-phone-sms-course/Lesson%202/2_p_sending_sms_messages.html)

- The App does not use any DB. When the app is closed, all message history is lost. A ConversationActivity contains message history, but when you exit the specific activity (and go back to main), it gets lost. For a message to be received, one must be on the ConversationActivity screen. 

- Use shared ViewModel? Use fragments?
https://stackoverflow.com/questions/44641121/share-viewmodel-between-fragments-that-are-in-different-activity
https://stackoverflow.com/questions/27466397/trying-create-a-chat-with-fragment
https://blog.mindorks.com/shared-viewmodel-in-android-shared-between-fragments


## Resources:

- https://google-developer-training.github.io/android-developer-phone-sms-course
- https://medium.com/@anitaa_1990/how-to-update-an-activity-from-background-service-or-a-broadcastreceiver-6dabdb5cef74 
- https://medium.com/@peterekeneeze/passing-data-between-activities-2d0ef122f19d