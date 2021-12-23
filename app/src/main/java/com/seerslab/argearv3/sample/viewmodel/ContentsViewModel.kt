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
package com.seerslab.argearv3.sample.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seerslab.argearv3.sample.api.ContentsResponse

import com.seerslab.argearv3.sample.AppApplication


class ContentsViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableLiveData: MutableLiveData<ContentsResponse>
    private val contentsRepository: ContentsRepository = ContentsRepository.instance

    val contents: LiveData<ContentsResponse>
        get() = mutableLiveData

    init {
//        mutableLiveData = contentsRepository.getContents(AppConfig.API_KEY)
        mutableLiveData = contentsRepository.getContents((application as AppApplication).humanAR)
    }
}
