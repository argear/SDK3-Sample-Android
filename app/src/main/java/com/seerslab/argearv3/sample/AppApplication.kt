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
package com.seerslab.argearv3.sample

import android.app.Application
import com.seerslab.argear.human.ARGHumanAR
import com.seerslab.argear.human.ARGHumanARConfig
import java.io.File

class AppApplication : Application() {
    lateinit var humanAR: ARGHumanAR
    lateinit var contentDirPath: String

    override fun onCreate() {
        super.onCreate()

        val argearDirPath = getExternalFilesDir(null)!!.absolutePath + "/argear/"
        val dir = File(argearDirPath)

        if (!dir.exists()) {
            dir.mkdirs()
        }

        contentDirPath = "$argearDirPath/content/"

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
    }

    override fun onTerminate() {
        humanAR.destroy()
        super.onTerminate()
    }
}
