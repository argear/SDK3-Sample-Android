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
package com.seerslab.argearv3.sample.camera

import android.graphics.SurfaceTexture
import android.media.Image

abstract class ReferenceCamera {

    protected var listener: CameraListener? = null

    abstract val cameraFacingFront: Int
    abstract val cameraFacingBack: Int

    abstract fun setCameraTexture(textureId: Int?, surfaceTexture: SurfaceTexture?)
    abstract fun setFacing(CameraFacing: Int)
    abstract fun isCameraFacingFront(): Boolean
    abstract val previewSize: IntArray?
    abstract fun startCamera()
    abstract fun stopCamera()
    abstract fun destroy()
    abstract fun changeCameraFacing(): Boolean

    interface CameraListener {
        fun setConfig(
            previewWidth: Int,
            previewHeight: Int,
            verticalFov: Float,
            horizontalFov: Float,
            orientation: Int,
            isFrontFacing: Boolean,
            fps: Float
        )
        // region - for camera api 1
        fun feedRawData(data: ByteArray?)
        // endregion
        // region - for camera api 2
        fun feedRawData(data: Image?)
        // endregion
    }
}
