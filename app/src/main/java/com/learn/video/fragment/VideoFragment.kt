package com.fmt.mvi.learn.video.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fmt.mvi.learn.R
import com.fmt.mvi.learn.commom.ui.BaseFragment
import com.fmt.mvi.learn.commom.utils.ShareUtils
import com.fmt.mvi.learn.databinding.FragmentVideoBinding
import com.fmt.mvi.learn.video.action.VideoListViewAction
import com.fmt.mvi.learn.video.adapter.BaseRecAdapter
import com.fmt.mvi.learn.video.adapter.BaseRecViewHolder
import com.fmt.mvi.learn.video.model.VideoModel
import com.fmt.mvi.learn.video.state.VideoListViewState
import com.fmt.mvi.learn.video.viewmodel.VideoViewModel
import com.fmt.mvi.learn.video.weight.MyVideoPlayer
import kotlinx.coroutines.flow.distinctUntilChanged

class VideoFragment : BaseFragment() {

    private lateinit var mBinding: FragmentVideoBinding
    private val mAdapter by lazy { ListVideoAdapter() }
    private val mViewModel by viewModels<VideoViewModel>()
    private var mCurrentPage = 0
    private var  currentPosition = 0
    private lateinit var snapHelper:PagerSnapHelper
    private lateinit var layoutManager:LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentVideoBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        registerUIStateCallback()
        addListener()

    }
    private fun addListener() {
        mBinding.rvPage2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                            val view: View? = snapHelper.findSnapView(layoutManager)
                        //当前固定后的item position
                        val position: Int = recyclerView.getChildAdapterPosition(view!!)
                        if (currentPosition != position) {
                            //如果当前position 和 上一次固定后的position 相同, 说明是同一个, 只不过滑动了一点点, 然后又释放了
                            MyVideoPlayer.releaseAllVideos()
                            val viewHolder: RecyclerView.ViewHolder =
                                recyclerView.getChildViewHolder(view)
                            if (viewHolder is VideoViewHolder) {
                                viewHolder.mp_video.startVideo()
                            }
                        }
                        currentPosition = position
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {}
                    RecyclerView.SCROLL_STATE_SETTLING -> {}
                }
            }
        })
    }
    private fun initView() {
        val urlList = ArrayList<String>()
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201811/26/09/5bfb4c55633c9.mp4")
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201805/100651/201805181532123423.mp4")
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803151735198462.mp4")
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150923220770.mp4")
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150922255785.mp4")
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150920130302.mp4")
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803141625005241.mp4")
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803141624378522.mp4")
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803131546119319.mp4")
        snapHelper =PagerSnapHelper()
        snapHelper.attachToRecyclerView(mBinding.rvPage2)
         layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.rvPage2.setLayoutManager(layoutManager)
        mBinding.rvPage2.adapter=mAdapter
    }



    override fun loadPageData() {
        mViewModel.dispatch(VideoListViewAction.Refresh)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
    internal class ListVideoAdapter :
        BaseRecAdapter<VideoModel, VideoViewHolder>() {

        override fun onHolder(holder: VideoViewHolder?, bean: VideoModel, position: Int) {
            val layoutParams: ViewGroup.LayoutParams = holder!!.itemView.getLayoutParams()
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            holder.mp_video.setUp(bean.playurl, "第" + position + "个视频", MyVideoPlayer.STATE_NORMAL)
            if (position == 0) {
                holder.mp_video.startVideo()
            }
            Glide.with(context).load(bean.picurl).into(holder.mp_video.thumbImageView)
            holder.tv_title.text = bean.title
            holder.iv_share.setOnClickListener {
                ShareUtils.share(
                    context,
                    bean.title,
                    bean.playurl
                )
            }

        }

        override fun onCreateHolder(): VideoViewHolder {
            return VideoViewHolder(getViewByRes(R.layout.item_video2))
        }
    }
    class VideoViewHolder(var rootView: View) : BaseRecViewHolder(
        rootView
    ) {
        var mp_video: MyVideoPlayer
        var tv_title: TextView
        var iv_share:View

        init {
            mp_video = rootView.findViewById(R.id.mp_video)
            tv_title = rootView.findViewById(R.id.tv_title)
            iv_share= rootView.findViewById(R.id.iv_share)
        }
    }

    private fun registerUIStateCallback() {
        lifecycleScope.launchWhenResumed {
            mViewModel.state.flowWithLifecycle(lifecycle).distinctUntilChanged().collect { state ->
                when (state) {
                    is VideoListViewState.RefreshSuccessState -> {
                        mAdapter.setNewData(state.videoList)
                    }
                    else -> {}
                }
            }
        }
    }
}