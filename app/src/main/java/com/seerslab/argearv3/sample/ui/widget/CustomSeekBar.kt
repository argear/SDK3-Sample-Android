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
package com.seerslab.argearv3.sample.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

class CustomSeekBar : AppCompatSeekBar {

    var minValue: Int = 0
        set(min) {
            field = min
            super.setMax(maxValue - minValue)
        }

    var maxValue: Int = 0
        set(max) {
            field = max
            super.setMax(maxValue - minValue)
        }

    protected var listener: OnSeekBarChangeListener? = null

    constructor(context: Context) : super(context) {
        setUpInternalListener()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setUpInternalListener()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setUpInternalListener()
    }

    override fun setProgress(progress: Int) {
        super.setProgress(progress - minValue)
    }

    override fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        this.listener = listener
    }

    private fun setUpInternalListener() {
        super.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                listener?.onProgressChanged(seekBar, minValue + i, b)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                listener?.onStartTrackingTouch(seekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                listener?.onStopTrackingTouch(seekBar)
            }
        })
    }
}
