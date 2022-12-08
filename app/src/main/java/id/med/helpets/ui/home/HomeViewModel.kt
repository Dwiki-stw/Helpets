package id.med.helpets.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.med.helpets.dataclass.Post
import id.med.helpets.repository.StoryRepository

class HomeViewModel : ViewModel() {

    private val repository: StoryRepository
    private val _allPets = MutableLiveData<List<Post>>()
    val allPets: LiveData<List<Post>> = _allPets

    init {
        repository = StoryRepository().getInstance()
        repository.loadPets(_allPets)
    }
}