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
package com.seerslab.argearv3.sample.ui.adapter

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.seerslab.argearv3.sample.R
import com.seerslab.argearv3.sample.model.CategoryModel
import java.util.*

class StickerCategoryListAdapter(private val listListener: Listener?) :
    RecyclerView.Adapter<StickerCategoryListAdapter.ViewHolder>() {

    companion object {
        private val TAG = StickerCategoryListAdapter::class.java.simpleName
    }

    private val listCategories: MutableList<CategoryModel> = ArrayList()

    interface Listener {
        fun onCategorySelected(category: CategoryModel?)
    }

    fun setData(categories: List<CategoryModel>?) {
        categories?.let {
            listCategories.clear()
            for (model in categories) {
                if (!TextUtils.equals(model.title, "filters")) {
                    listCategories.add(model)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listCategories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.category_sticker, parent, false)
        return CategoryViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    abstract class ViewHolder(v: View) :
        RecyclerView.ViewHolder(v) {
        abstract fun bind(position: Int)
    }

    inner class CategoryViewHolder(v: View) :
        ViewHolder(v), View.OnClickListener {

        private var buttonCategory: Button = v.findViewById<View>(R.id.category_button) as Button
        private var categoryModel: CategoryModel? = null

        override fun bind(position: Int) {
            categoryModel = listCategories[position]

            /*
			 * Sticker Category 의 제목을 표시합니다.
			 */
            Log.d(TAG, "category_sticker $position $categoryModel")
            buttonCategory.text = categoryModel?.title
            buttonCategory.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listListener?.onCategorySelected(categoryModel)
        }
    }
}
