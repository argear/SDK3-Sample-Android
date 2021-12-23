ARGear sample application for Android
======================
(c) Copyright 2021 Seerslab. All rights reserved.

##### This repository contains an Android sample application that uses ARGear SDK. It might be helpful when you write your own applications based on the ARGear SDK. Compared to the previous SDK, the new SDK provides easier APIs including cloud connections and network operations. Please refer to the API documentation in the `doc` directory. If you find any bugs, problems, or if you have any suggestions, please register them as issues of this repository.

> Note: This release doesn't contain full features of SDK and the features will be added step by step.

* 24th December, 2021: 3D contents augmentation and rendering, contents download, SDK validation.


### 1. Build & Run
You can build the sample application using Android Studio or console command ( `gradle` )

#### 1.1 Prerequisites
1. Android Studio : [Download Android Studio and SDK tools  |  Android Developers](https://developer.android.com/studio)
2. Required platform API level is 23 or higher
3. Architecture : arm64-v8a

#### 1.2 Android Studio
1. Open **ARGearSDKSampleAndroid** project
2. Click the `Run` button in the toolbar
3. Sample app will be built and installed on your device

#### 1.3 Command Line
1. Install Gradle for your platform from the following link: [Install Gradle](https://gradle.org/install/)
2. You can simply call the gradle task as below on the **ARGearSDKSampleAndroid** folder  
   `$ gradle clean` and `$ gradle build`
3. Output files including apk can be found in the following location  
   `./app/build/outputs/apk/`
   <br />
   <br />

### 2. Programming Guide
#### 2.1 Creating SDK Components
Prior to writing your own application code, you should create your project to communicate with your mobile application from the [ARGear Console](https://console.argear.io). After you log in to the console, you can get information on how to create the project from the support section.
If you create the project successfully, you will see the KEY information to use SDK validation as below.
![Project Key Information](https://user-images.githubusercontent.com/94022774/146729616-e54359e1-59b2-4e5d-8144-585eca718f63.png)<br />
The Application ID should be same with your mobile appication package name.
> above key information is the same as this sample project.

Using this key information, you can create an SDK interface instance, `ARGHumanAR`, as follows:

``` kotlin
    val config = ARGHumanARConfig(
        AppConfig.API_URL,
        AppConfig.API_KEY,
        AppConfig.SECRET_KEY,
        AppConfig.AUTH_KEY,
        argearDirPath,
        contentDirPath
    )

    humanAR = ARGHumanAR.create()
    humanAR.setConfiguration(config)
```

> You can see more detailed code information from the sample application source code.

#### 2.2 Getting camera preview data and passing to HumanAR SDK for image processing
You can get the camera preview data from the listener `ReferenceCamera.CameraListener` which is registered while creating camera

``` kotlin
    private fun initCamera() {
        camera =
            if (AppConfig.USE_CAMERA_API == 1) {
                ReferenceCamera1(this, cameraListener)
            } else {
                ReferenceCamera2(
                    this,
                    cameraListener,
                    windowManager.defaultDisplay.rotation
                )
            }
    }
    
    
    private var cameraListener: ReferenceCamera.CameraListener =
    object : ReferenceCamera.CameraListener {
    ...

        override fun feedRawData(data: ByteArray?) {
            argCameraFrameData = ARGCameraFrameData(argCameraConfig, data)
        }

        override fun feedRawData(data: Image?) {
            argCameraFrameData = ARGCameraFrameData(argCameraConfig, data)
        }
    }
```

Whenever the camera screen layout is drawn, preview data is set to HumanAR

``` kotlin
    private var glViewListener: GLView.GLViewListener = object : GLView.GLViewListener {
        override fun onDrawFrame(gl: GL10?, width: Int?, height: Int?) {
        ...
            if (argCameraFrameData != null) {
                val mARGFrame: ARGFrame =
                    humanAR.process(argCameraFrameData, ARGHumanARProcessOptions.NONE)
                screenRenderer.draw(mARGFrame.textureId, localWidth, localHeight)
            }
        }
    }
```

#### 2.3 Getting content categories
Content categories are defined in the SDK as below

``` kotlin
public enum ARGHumanARCategory {
    ARGHumanARCategoryNone(0),
    ARGHumanARCategoryFace(1),
    ARGHumanARCategoryBeautification(2),
    ARGHumanARCategoryBgSegmentation(4),
    ARGHumanARCategoryFaceSegmentation(8),
    ARGHumanARCategoryfootFitting(16),
    ARGHumanARCategoryGlassFitting(32),
    ARGHumanARCategoryAll(65535);
}
```

You can get information about currently available categories by using `humanAR.contentCategory`

``` kotlin
class ContentsRepository {
    fun getContents(humanAR: ARGHumanAR): MutableLiveData<ContentsResponse> {
        val contents: MutableLiveData<ContentsResponse> = MutableLiveData()
        val categories = ArrayList<CategoryModel>()
        for (category in humanAR.contentCategory) {
            val items = ArrayList<ItemModel>()
            ...
            categories.add(
                CategoryModel(
                    category, "3D Sticker",
                    "", false, 0, "", items
                )
            )
        }
        contents.value =
            ContentsResponse("", "", "", "", 0, categories)

        return contents
    }
}
```

#### 2.4 Getting Content list

To augment various contents on the human face in the screen, you need to get the content information from the ARGear cloud. The contents informatino includes UUID, thumbnail URI,  content URI, and so on. You can get the content list by using `getContentList` API.

``` kotlin
    public ARGContentInfo[] getContentList(String category, int offset, int count) 
    
    public class ARGContentInfo {
    	private String _uuid;
    	private String _thumbnailUri;
    	private String _contentUri;
    	private Map<String, String> _properties;
    }
```

This API provides the content list under certain `category`. The offset is value of the starting position from the begining and the `count` is reqeust number of contents.
> You can see the detaild information of the API and parameters from the API document.

You can make a sample application data model with the above content information received from the ARGear cloud

``` kotlin
    val categories = ArrayList<CategoryModel>()
    for (category in humanAR.contentCategory) {
        val items = ArrayList<ItemModel>()
        for (arcontentInfo in humanAR.getContentList(category, 0, 100)) {
            items.add(
                ItemModel(
                    arcontentInfo.uuid,
                    "", "",
                    arcontentInfo.thumbnailUri, arcontentInfo.contentUri,
                    0, 0, 0, 0, 0,
                    false, "", 0, ""
                )
            )
        }
        categories.add(
            CategoryModel(
                category, "3D Sticker",
                "", false, 0, "", items
            )
        )
    }
```


#### 2.5 Applying & Clearing Content
To augment (or cancel) specific content on the screen, you can use following APIs.

``` kotlin
    public void applyContent(String uuid) 
    public void cancelContent(String uuid) 
```

The content selected by the user is applied after clearing the previous content

``` kotlin
    if (currentSelectedStickerItem == null) {
        if (currentAppliedStickerItem != null) {
            humanAR.cancelContent(currentAppliedStickerItem?.uuid)
            currentAppliedStickerItem = null
        }
    } else {
        if (currentSelectedStickerItem?.uuid != currentAppliedStickerItem?.uuid) {
            if (currentAppliedStickerItem != null)
                humanAR.cancelContent(currentAppliedStickerItem?.uuid)
            humanAR.applyContent(currentSelectedStickerItem?.uuid)
            currentAppliedStickerItem = currentSelectedStickerItem
        }
    }
```