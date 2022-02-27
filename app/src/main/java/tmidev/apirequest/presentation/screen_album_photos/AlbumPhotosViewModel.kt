package tmidev.apirequest.presentation.screen_album_photos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tmidev.apirequest.R
import tmidev.apirequest.domain.type.ResultType
import tmidev.apirequest.domain.usecase.GetAlbumPhotosUseCase
import javax.inject.Inject

@HiltViewModel
class AlbumPhotosViewModel @Inject constructor(
    private val getAlbumPhotosUseCase: GetAlbumPhotosUseCase,
    savedState: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<AlbumPhotosUiState>(AlbumPhotosUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val albumId = savedState.get<Int>("albumId")

    init {
        getAlbumPhotos()
    }

    private fun getAlbumPhotos() = viewModelScope.launch {
        if (albumId == null) _uiState.value = AlbumPhotosUiState.Error(
            message = R.string.somethingWentWrong
        ) else getAlbumPhotosUseCase(albumId = albumId).collectLatest { resultType ->
            _uiState.value = when (resultType) {
                is ResultType.Loading -> AlbumPhotosUiState.Loading
                is ResultType.Success -> AlbumPhotosUiState.Success(
                    photos = resultType.data
                )
                is ResultType.Error -> AlbumPhotosUiState.Error(
                    message = R.string.somethingWentWrong
                )
            }
        }
    }

    fun reloadAlbumPhotos() = viewModelScope.launch {
        _uiState.value = AlbumPhotosUiState.Loading
        delay(timeMillis = 1000L)
        getAlbumPhotos()
    }
}