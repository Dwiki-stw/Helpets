package id.med.helpets.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import id.med.helpets.dataclass.Post

class StoryRepository {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    @Volatile private var INSTANCE: StoryRepository ?= null

    fun getInstance(): StoryRepository {
        return INSTANCE ?: synchronized(this) {
            val instance = StoryRepository()
            INSTANCE = instance
            instance
        }
    }

    fun loadPets (petsList: MutableLiveData<List<Post>>) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                try {

                    val _petsList: List<Post> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Post::class.java)!!
                    }

                    petsList.postValue(_petsList)

                } catch (e: Exception) {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}