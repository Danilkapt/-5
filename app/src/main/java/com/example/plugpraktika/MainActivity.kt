package com.example.plugpraktika

import android.app.Activity
import android.app.admin.FactoryResetProtectionPolicy
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.plugpraktika.databinding.ActivityMainBinding
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                textView.text = post.author
                textView2.text = post.published
                connn.text = post.content
                textView4.text = post.kolRep.toString()
                var k = 12
                if (post.likedByMe)
                {
                    imageView5?.setImageResource(R.drawable.unlike)
                }
                if (post.kolLikes)
                {
                    textView3?.text = "112"
                }

                imageView5.setOnClickListener() {
                    post.likedByMe = !post.likedByMe
                    post.kolLikes = !post.kolLikes
                    imageView5.setImageResource(
                        if (post.likedByMe) R.drawable.unlike else R.drawable.heart
                    )
                    textView3.text = if (post.kolLikes) "111" else "112"

                }

                imageView2.setOnClickListener() {
                    k = k+100
                    textView4.text = k.toString()
                    if (k >= 1000)
                    {
                        textView4.text = (k/1000).toString()+"."+((k%1000)/100).toString()+"K"
                    }
                    if (k>= 10000)
                    {
                        textView4.text = (k/1000).toString() + "K"
                    }
                    if (k >= 1000000)
                    {
                        textView4.text = (k/1000000).toString() + "." + ((k%1000000)/100000).toString() + "M"
                    }
                }
            }
            }
        binding.imageView5.setOnClickListener() {
            viewModel.like()
        }
        }
    }

interface PostRepository {
    fun get(): LiveData<Post>
    fun like()
}

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var kolLikes: Boolean = false,
    var kolRep: Long
)
class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        id = 1,
        author = "Плетнев Даниил",
        content = "Москва полна очарования в любое время года. Однако некоторые ее уголки становятся особенно неотразимыми в огнях декоративной подсветки. Представляем список самых живописных «ночных» мест столицы, который будет полезен не только фотографам-любителям.",
        published = "2 часа назад",
        likedByMe = false,
        kolLikes = false,
        kolRep = 12
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data
    override fun like() {
        post = post.copy(likedByMe = !post.likedByMe)
        data.value = post
    }
}

class ViewModelLazy<VM : ViewModel> (
    private val viewModelClass: KClass<VM>,
    private val storeProducer: () -> ViewModelStore,
    private val factoryProducer: () -> ViewModelProvider.Factory
) : Lazy<VM> {
    private var cached: VM? = null

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val store = storeProducer()
                ViewModelProvider(store, factory).get(viewModelClass.java).also {
                    cached = it
                }
            } else {
                viewModel
            }

        }

    override fun isInitialized() = cached != null
}

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.get()
    fun like() = repository.like()
}


