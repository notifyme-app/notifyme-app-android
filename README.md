# NotifyMe for Android

[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://github.com/notifyme-app/notifyme-app-android/blob/master/LICENSE)
![Android Build](https://github.com/notifyme-app/notifyme-app-android/workflows/Android%20Build/badge.svg)

<br />
<div align="left">
  <img width="180" height="180" src="app/src/main/app_icon-playstore.png" />
  <br />
</div>

[Download Android App](https://install.appcenter.ms/orgs/n2step/apps/n2step-app-android/distribution_groups/internal)

## Introduction

The app is a decentralised check-in system for meetings and events. Users can check in to a venue by scanning a QR Code. The check in is stored locally and encrypted. In case one of the visitors tests positive subsequent to a gathering, all other participants can be easily informed via the app. The app uses the [CrowdNotifier SDK](https://github.com/CrowdNotifier/crowdnotifier-sdk-android) based on the [CrowdNotifier White Paper](https://github.com/CrowdNotifier/documents) by Wouter Lueks (EPFL) et al. The app design, UX and implementation was done by [Ubique](https://ubique.ch/). More information can be found [here](https://notify-me.ch).

## Repositories
* Android SDK: [crowdnotifier-sdk-android](https://github.com/CrowdNotifier/crowdnotifier-sdk-android)
* iOS SDK: [crowdnotifier-sdk-ios](https://github.com/CrowdNotifier/crowdnotifier-sdk-ios)
* Android Demo App: [notifyme-app-android](https://github.com/notifyme-app/notifyme-app-android)
* iOS Demo App: [notifyme-app-ios](https://github.com/notifyme-app/notifyme-app-ios)
* Backend SDK: [notifyme-sdk-backend](https://github.com/notifyme-app/notifyme-sdk-backend)
* QR Code Generator Backend: [notifyme-qrgenerator-web](https://github.com/notifyme-app/notifyme-qrgenerator-web)
* QR Landing Page Backend: [notifyme-qrlandingpage-web](https://github.com/notifyme-app/notifyme-qrlandingpage-web)
* QR Trace Upload Backend: [notifyme-upload-web](https://github.com/notifyme-app/notifyme-upload-web)

## Work in Progress
The NotifyMe App for Android contains alpha-quality code only and is not yet complete. It has not yet been reviewed or audited for security and compatibility. We are both continuing the development and have started a security review. This project is truly open-source and we welcome any feedback on the code regarding both the implementation and security aspects.
This repository contains the open prototype Application, so please focus your feedback for this repository on implementation issues.

## Further Documentation
The full set of documents for CrowdNotifier is at https://github.com/CrowdNotifier/documents. Please refer to the technical documents and whitepapers for a description of the implementation.

## Installation and Building

The application can be downloaded and installed from  [here](https://install.appcenter.ms/orgs/n2step/apps/n2step-app-android/distribution_groups/internal).

The project can be opened with Android Studio 4.1 or later or you can build the project with Gradle using
```sh
$ ./gradlew assembleProdDebug
```
The APK is generated under app/build/outputs/apk/prod/debug/package-prod-debug.apk

## License
This project is licensed under the terms of the MPL 2 license. See the [LICENSE](LICENSE) file.
