/*******************************************************************************
 * Copyright(C) 2021 SEERSLAB Inc. All Rights Reserved.
 *
 * PROPRIETARY/CONFIDENTIAL
 *
 * This software is the confidential and proprietary information of
 * SEERSLAB Inc. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SEERSLAB Inc.
 *
 * "SEERSLAB Inc." MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. "SEERSLAB Inc." SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 ******************************************************************************/
package com.seerslab.argearv3.sample.ui

import android.content.Context
import android.graphics.Point
import android.media.Image
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.seerslab.argear.human.ARGHumanAR
import com.seerslab.argear.human.ARGHumanARCategory
import com.seerslab.argear.human.ARGHumanARProcessOptions
import com.seerslab.argear.types.ARGCameraFrameData
import com.seerslab.argear.types.ARGFrame
import com.seerslab.argear.types.config.ARGCameraConfig
import com.seerslab.argear.types.config.ARGImageFormat
import com.seerslab.argearv3.sample.AppApplication
import com.seerslab.argearv3.sample.R
import com.seerslab.argearv3.sample.camera.ReferenceCamera
import com.seerslab.argearv3.sample.camera.ReferenceCamera2
import com.seerslab.argearv3.sample.common.PermissionHelper
import com.seerslab.argearv3.sample.databinding.ActivityCameraBinding
import com.seerslab.argearv3.sample.model.ItemModel
import com.seerslab.argearv3.sample.rendering.CameraTexture
import com.seerslab.argearv3.sample.rendering.ScreenRenderer
import com.seerslab.argearv3.sample.viewmodel.ContentsViewModel
import java.io.File
import java.util.EnumSet
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraActivity"
    }

    private lateinit var humanAR: ARGHumanAR

    private lateinit var camera: ReferenceCamera
    private lateinit var glView: GLView
    private lateinit var screenRenderer: ScreenRenderer
    private lateinit var cameraTexture: CameraTexture

    private lateinit var argCameraConfig: ARGCameraConfig
    private var argCameraFrameData: ARGCameraFrameData? = null

    private var currentSelectedStickerItem: ItemModel? = null
    private var currentAppliedStickerItem: ItemModel? = null
    private var hasTrigger = false
    private var useARGSessionDestroy = false

    private var deviceWidth = 0
    private var deviceHeight = 0
    var gLViewWidth = 0
        private set
    var gLViewHeight = 0
        private set

    private lateinit var fragmentManager: FragmentManager
    private lateinit var stickerFragment: StickerFragment
    private lateinit var contentsViewModel: ContentsViewModel

    private lateinit var dataBinding: ActivityCameraBinding

    private var isInitializeSdk: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val realSize = Point()
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        display.getRealSize(realSize)

        deviceWidth = realSize.x
        deviceHeight = realSize.y
        gLViewWidth = realSize.x
        gLViewHeight = realSize.y

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera)

        fragmentManager = supportFragmentManager
        stickerFragment = StickerFragment()

        humanAR = (application as AppApplication).humanAR
    }

    override fun onResume() {
        super.onResume()

        if (!isInitializeSdk) {
            if (hasPermission()) {
                initSdk()
                isInitializeSdk = true
            }
        }

        if (::camera.isInitialized) {
            camera.startCamera()
            setGLViewSize(camera.previewSize)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::camera.isInitialized) {
            camera.stopCamera()
        }
    }

    override fun onDestroy() {
        if (::camera.isInitialized) {
            camera.destroy()
            useARGSessionDestroy = true
        }
        humanAR.finalize(EnumSet.of(ARGHumanARCategory.ARGHumanARCategoryAll))
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (stickerFragment.isAdded) {
            dataBinding.functionsLayout.visibility = View.VISIBLE
        }
        super.onBackPressed()
    }

    private fun hasPermission(): Boolean {
        if (!PermissionHelper.hasPermission(this)) {
            if (PermissionHelper.shouldShowRequestPermissionRationale(this)) {
                dataBinding.root.visibility = View.GONE
                Toast.makeText(this, "Please check your permissions!", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            PermissionHelper.requestPermission(this)
            return false
        }
        dataBinding.root.visibility = View.VISIBLE
        return true
    }

    private fun initSdk() {
        humanAR.initialize(EnumSet.of(ARGHumanARCategory.ARGHumanARCategoryAll))
        contentsViewModel = ViewModelProvider(this).get(ContentsViewModel::class.java)

        initGLView()
        initCamera()
    }

    private fun initGLView() {
        val cameraLayout: FrameLayout = dataBinding.cameraLayout
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        screenRenderer = ScreenRenderer()
        cameraTexture = CameraTexture()

        glView = GLView(this, glViewListener)
        glView.setZOrderMediaOverlay(true)
        cameraLayout.addView(glView, params)
    }

    private fun initCamera() {
        camera = ReferenceCamera2(
            this,
            cameraListener,
            windowManager.defaultDisplay.rotation
        )
    }

    fun onClickButtons(v: View) {
        when (v.id) {
            R.id.sticker_button -> showStickers()
        }
    }

    private fun setGLViewSize(cameraPreviewSize: IntArray?) {
        if (cameraPreviewSize == null) return

        val previewWidth = cameraPreviewSize[1]
        val previewHeight = cameraPreviewSize[0]

        gLViewHeight = deviceHeight
        gLViewWidth = (deviceHeight.toFloat() * previewWidth / previewHeight).toInt()

        if ((gLViewWidth != glView.viewWidth || gLViewHeight != glView.height)
        ) {
            glView.holder.setFixedSize(gLViewWidth, gLViewHeight)
        }
    }

    fun setMeasureSurfaceView(view: View) {
        if (view.parent is FrameLayout) {
            view.layoutParams = FrameLayout.LayoutParams(gLViewWidth, gLViewHeight)
        } else if (view.parent is RelativeLayout) {
            view.layoutParams = RelativeLayout.LayoutParams(gLViewWidth, gLViewHeight)
        }
        view.x = 0.0f
    }

    private fun showSlot(fragment: Fragment?) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.slot_container, fragment!!)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun showStickers() {
        showSlot(stickerFragment)

        dataBinding.functionsLayout.visibility = View.GONE
        dataBinding.shutterLayout.visibility = View.GONE
    }

    fun setSticker(item: ItemModel) {
        if (!File((application as AppApplication).contentDirPath + item.uuid).exists()) {
            humanAR.getContent(item.uuid, item.zipFileUrl)
        }
        currentSelectedStickerItem = item
    }

    fun clearStickers() {
        currentSelectedStickerItem = null
        hasTrigger = false
    }

    private var glViewListener: GLView.GLViewListener = object : GLView.GLViewListener {
        override fun onSurfaceCreated(
            gl: GL10?,
            config: EGLConfig?
        ) {
            screenRenderer.create()
            cameraTexture.createCameraTexture()
        }

        override fun onDrawFrame(gl: GL10?, width: Int?, height: Int?) {
            camera.setCameraTexture(
                cameraTexture.textureId,
                cameraTexture.surfaceTexture
            )

            val localWidth = width ?: 0
            val localHeight = height ?: 0

            if (argCameraFrameData != null) {
                val mARGFrame: ARGFrame =
                    humanAR.process(argCameraFrameData, ARGHumanARProcessOptions.NONE)
                screenRenderer.draw(mARGFrame.textureId, localWidth, localHeight)
            }

            val drawItem = currentSelectedStickerItem
            if (drawItem == null) {
                if (currentAppliedStickerItem != null) {
                    humanAR.cancelContent(currentAppliedStickerItem?.uuid)
                    currentAppliedStickerItem = null
                }
            } else {
                if (drawItem.uuid != currentAppliedStickerItem?.uuid) {
                    if (currentAppliedStickerItem != null)
                        humanAR.cancelContent(currentAppliedStickerItem?.uuid)
                    humanAR.applyContent(drawItem.uuid)
                    currentAppliedStickerItem = drawItem
                }
            }
        }
    }

    private var cameraListener: ReferenceCamera.CameraListener =
        object : ReferenceCamera.CameraListener {
            override fun setConfig(
                previewWidth: Int,
                previewHeight: Int,
                verticalFov: Float,
                horizontalFov: Float,
                orientation: Int,
                isFrontFacing: Boolean,
                fps: Float
            ) {
                argCameraConfig = ARGCameraConfig(
                    previewWidth,
                    previewHeight,
                    ARGImageFormat.ARGImageFormatYuv420_888,
                    isFrontFacing,
                    orientation,
                    horizontalFov,
                    verticalFov
                )
            }

            override fun feedRawData(data: ByteArray?) {
                argCameraFrameData = ARGCameraFrameData(argCameraConfig, data)
            }

            override fun feedRawData(data: Image?) {
                argCameraFrameData = ARGCameraFrameData(argCameraConfig, data)
            }
        }
}
