# Calegram Messenger

SinaCalegramator is a client for telegram based on Telegram-Android
https://github.com/DrKLO/Telegram
Telegram is a messaging app with a focus on speed and security. It’s superfast, simple and free. This repo contains the official source code for Telegram App for Android.

Calegram help Telegram users to hide their Telegram Clients on their phone


## building apk
1. setup android devlop environment
2. clone git repository
3. add signing.properties in root folder like
```
storePassword=your_keystore_pwd
keyPassword=your_key_pwd
keyAlias=your_alias
storeFile=your_key_store_file_path
```
4. build cmd
```
./gradlew assembleAfatRelease
```
