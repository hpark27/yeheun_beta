package com.beta.yeheun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beta.yeheun.databinding.ActivityNoticeviewBinding
import com.beta.yeheun.databinding.NoticeViewBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NoticeViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeviewBinding
    // firebase firestore
    private var fireStore: FirebaseFirestore? = null
    // snapshot
    private var snapshot: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firestore
        fireStore = FirebaseFirestore.getInstance()

        // connect recyclerview
        binding.noticeRecyclerview.adapter = ViewAdapter()
        binding.noticeRecyclerview.layoutManager = LinearLayoutManager(this)

        // back
        binding.back.setOnClickListener {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        snapshot?.remove()
    }

    inner class ViewAdapter : RecyclerView.Adapter<ViewAdapter.ViewHolder>(){
        // array lists for content informations
        private val contents: ArrayList<Notice> = ArrayList()

        init{
            fireStore?.collection("Notice")?.get()
                    ?.addOnSuccessListener {result ->
                        contents.clear()
                        for(document in result){
                            val item = Notice(document["title"] as String?,
                                    document["content"] as String?, document["date"] as String?)

                            contents.add(0,item)
                        }
                        notifyDataSetChanged()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(applicationContext, getString(R.string.main_googleform_fail), Toast.LENGTH_SHORT).show()
                    }
        }

        inner class ViewHolder(val binding: NoticeViewBinding): RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(NoticeViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // title
            holder.binding.title.text = contents[position].title
            // content
            holder.binding.content.text = contents[position].content
            // date
            holder.binding.date.text = contents[position].date

            // view detail
            holder.binding.more.setOnClickListener {
                if(holder.binding.contentLayout.visibility == View.VISIBLE){
                    holder.binding.contentLayout.visibility = View.GONE
                    holder.binding.more.setImageResource(R.drawable.up_arrow)
                }else{
                    holder.binding.contentLayout.visibility = View.VISIBLE
                    holder.binding.more.setImageResource(R.drawable.down_arrow)
                }
            }
        }

        override fun getItemCount(): Int {
            return contents.size
        }
    }
}