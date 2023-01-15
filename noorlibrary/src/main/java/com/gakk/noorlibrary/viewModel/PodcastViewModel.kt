package com.gakk.noorlibrary.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.data.rest.youtube.YoutubeRepository
import com.gakk.noorlibrary.model.podcast.AddCommentResponse
import com.gakk.noorlibrary.model.podcast.CommentListResponse
import com.gakk.noorlibrary.model.podcast.LiveVideosResponse
import com.gakk.noorlibrary.model.youtube.YoutubeVideoDetails
import kotlinx.coroutines.launch

class PodcastViewModel(private val repository: RestRepository) : ViewModel() {

    private var youtubeRepository: YoutubeRepository = YoutubeRepository(viewModelScope)
    var liveUrlResponse: MutableLiveData<Resource<LiveVideosResponse>> =
        MutableLiveData()

    var livevideosResponse: MutableLiveData<Resource<LiveVideosResponse>> =
        MutableLiveData()

    var commentListData: MutableLiveData<Resource<CommentListResponse>> =
        MutableLiveData()

    var postComment: MutableLiveData<Resource<AddCommentResponse>> = MutableLiveData()
    var addLikeComment: MutableLiveData<Resource<AddCommentResponse>> = MutableLiveData()


    fun loadLiveUrl(categoryId: String, subCategoryId: String) {
        viewModelScope.launch {
            liveUrlResponse.postValue(Resource.loading(data = null))

            try {
                liveUrlResponse.postValue(
                    Resource.success(
                        repository.getLive(categoryId, subCategoryId)
                    )
                )
            } catch (e: Exception) {

                liveUrlResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun loadLiveVideos(categoryId: String) {
        viewModelScope.launch {
            livevideosResponse.postValue(Resource.loading(data = null))

            try {
                livevideosResponse.postValue(
                    Resource.success(
                        repository.getLiveVideos(categoryId)
                    )
                )
            } catch (e: Exception) {
                livevideosResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun fetchYoutubeVideo(videoId: String?, isPodcast: Boolean): LiveData<YoutubeVideoDetails>? {
        val youtubeUrlLiveData = MutableLiveData<YoutubeVideoDetails>()
        youtubeRepository.fetchVideo(
            videoId!!,
            isPodcast
        ) { youtubeVideoDetails: YoutubeVideoDetails ->
            youtubeUrlLiveData.value = youtubeVideoDetails
            null
        }
        return youtubeUrlLiveData
    }

    fun loadCommentList(
        categoryId: String,
        topicsId: String,
        pageNumber: Int
    ) {


        viewModelScope.launch {
            commentListData.postValue(Resource.loading(data = null))

            try {
                commentListData.postValue(
                    Resource.success(
                        repository.getCommentList(categoryId, topicsId, pageNumber)
                    )
                )
            } catch (e: Exception) {

                commentListData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun addComment(
        categoryId: String, textContentId: String, message: String
    ) {
        postComment.postValue(Resource.loading(data = null))
        viewModelScope.launch {
            try {
                postComment.postValue(
                    Resource.success(
                        data = repository.postComment(
                            categoryId, textContentId, message
                        )
                    )
                )
            } catch (e: Exception) {
                postComment.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun likeComment(commentId: String) {
        viewModelScope.launch {
            addLikeComment.postValue(Resource.loading(data = null))

            try {
                addLikeComment.postValue(Resource.success(data = repository.likeComment(commentId)))
            } catch (e: Exception) {
                addLikeComment.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }


}
