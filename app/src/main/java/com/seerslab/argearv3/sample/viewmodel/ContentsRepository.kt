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

import androidx.lifecycle.MutableLiveData
import com.seerslab.argear.human.ARGHumanAR
import com.seerslab.argearv3.sample.api.ContentsResponse
import com.seerslab.argearv3.sample.model.CategoryModel
import com.seerslab.argearv3.sample.model.ItemModel

class ContentsRepository {

    companion object {
        @JvmStatic
        val instance: ContentsRepository by lazy { ContentsRepository() }
    }

    fun getContents(humanAR: ARGHumanAR): MutableLiveData<ContentsResponse> {
        val contents: MutableLiveData<ContentsResponse> = MutableLiveData()
        val categories = ArrayList<CategoryModel>()
        for (category in humanAR.contentCategory) {
            val items = ArrayList<ItemModel>()
            for (contentInfo in humanAR.getContentList(category.first, 0, 100)) {
                items.add(
                    ItemModel(
                        contentInfo.uuid,
                        "", "",
                        contentInfo.thumbnailUri, contentInfo.contentUri,
                        0, 0, 0, 0, 0,
                        false, "", 0, ""
                    )
                )
            }
            categories.add(
                CategoryModel(
                    category.first, category.second,
                    "", false, 0, "", items
                )
            )
        }
        contents.value =
            ContentsResponse("", "", "", "", 0, categories)

        return contents
    }
}
