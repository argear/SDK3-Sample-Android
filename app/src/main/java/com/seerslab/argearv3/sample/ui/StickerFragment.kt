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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seerslab.argearv3.sample.R
import com.seerslab.argearv3.sample.databinding.FragmentStickerBinding
import com.seerslab.argearv3.sample.model.CategoryModel
import com.seerslab.argearv3.sample.model.ItemModel
import com.seerslab.argearv3.sample.ui.adapter.StickerCategoryListAdapter
import com.seerslab.argearv3.sample.ui.adapter.StickerListAdapter
import com.seerslab.argearv3.sample.viewmodel.ContentsViewModel

class StickerFragment : Fragment(),
    View.OnClickListener,
    StickerCategoryListAdapter.Listener, StickerListAdapter.Listener {

    private lateinit var stickerCategoryListAdapter: StickerCategoryListAdapter
    private lateinit var stickerListAdapter: StickerListAdapter
    private lateinit var contentsViewModel: ContentsViewModel

    private lateinit var rootView: FragmentStickerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = DataBindingUtil.inflate(inflater, R.layout.fragment_sticker, container, false)

        rootView.closeStickerButton.setOnClickListener(this)
        rootView.clearStickerButton.setOnClickListener(this)

        // init category_sticker list
        val recyclerViewStickerCategory: RecyclerView = rootView.stickerCategoryRecyclerview
        recyclerViewStickerCategory.setHasFixedSize(true)

        val categoryLayoutManager = LinearLayoutManager(context)
        categoryLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        recyclerViewStickerCategory.layoutManager = categoryLayoutManager
        stickerCategoryListAdapter = StickerCategoryListAdapter(this)
        recyclerViewStickerCategory.adapter = stickerCategoryListAdapter

        // init item_sticker list
        val recyclerViewSticker: RecyclerView = rootView.stickerRecyclerview
        recyclerViewSticker.setHasFixedSize(true)

        val itemsLayoutManager = LinearLayoutManager(context)
        itemsLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewSticker.layoutManager = itemsLayoutManager

        stickerListAdapter = StickerListAdapter(context, this)
        recyclerViewSticker.adapter = stickerListAdapter

        return rootView.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            contentsViewModel = ViewModelProvider(it).get(ContentsViewModel::class.java)
            contentsViewModel.contents.observe(
                viewLifecycleOwner,
                { contentsResponse ->
                    contentsResponse?.categories?.let {
                        stickerCategoryListAdapter.setData(contentsResponse.categories)
                    }
                })
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.close_sticker_button -> activity?.onBackPressed()
            R.id.clear_sticker_button -> {
                (activity as CameraActivity).clearStickers()
            }
        }
    }

    override fun onCategorySelected(category: CategoryModel?) {
        category?.let {
            stickerListAdapter.setData(category.items)
        }
    }

    override fun onStickerSelected(position: Int, item: ItemModel?) {
        item?.let {
            (activity as CameraActivity).setSticker(item)
        }
    }

    companion object {
        private val TAG = StickerFragment::class.java.simpleName
    }
}
