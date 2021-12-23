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
package com.seerslab.argearv3.sample.model

import com.google.gson.annotations.SerializedName

data class ItemModel(
    @SerializedName("uuid")
    var uuid: String?,

    @SerializedName("title")
    var title: String?,

    @SerializedName("description")
    var description: String?,

    @SerializedName("thumbnail")
    var thumbnailUrl: String?,

    @SerializedName("zip_file")
    var zipFileUrl: String?,

    @SerializedName("num_stickers")
    var numStickers: Int,

    @SerializedName("num_effects")
    var numEffects: Int,

    @SerializedName("num_bgms")
    var numBgms: Int,

    @SerializedName("num_filters")
    var numFilters: Int,

    @SerializedName("num_masks")
    var numMasks: Int,

    @SerializedName("has_trigger")
    var hasTrigger: Boolean,

    @SerializedName("status")
    var status: String?,

    @SerializedName("updated_at")
    var updatedAt: Long,

    @SerializedName("type")
    var type: String?
)
