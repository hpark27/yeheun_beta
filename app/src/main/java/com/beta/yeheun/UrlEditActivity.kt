package com.beta.yeheun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beta.yeheun.databinding.ActivityUrleditBinding
import com.beta.yeheun.databinding.NoticeEditBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UrlEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUrleditBinding
    // firebase firestore
    private var fireStore: FirebaseFirestore? = null
    // snapshot
    private var snapshot: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUrleditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firestore
        fireStore = FirebaseFirestore.getInstance()

        // recyclerview adapter
        binding.noticeRecyclerview.adapter = ViewAdapter()
        binding.noticeRecyclerview.layoutManager = LinearLayoutManager(this)

        // initialize the reference to firebase database bulletin
        val bulletinRef = FirebaseDatabase.getInstance().getReference("Admin").child("Bulletin")
        // initialize the reference to firebase database bulletin
        val mainApplicationRef = FirebaseDatabase.getInstance().getReference("Admin").child("Application")

        // back
        binding.back.setOnClickListener {
            finish()
        }

        // delete bulletin url
        binding.bulletin.setOnClickListener {
            bulletinRef.setValue("")
        }

        // delete main announcement
        binding.application.setOnClickListener {
            mainApplicationRef.setValue("")
        }
    }

    override fun onStop() {
        super.onStop()
        snapshot?.remove()
    }

    inner class ViewAdapter : RecyclerView.Adapter<UrlEditActivity.ViewAdapter.ViewHolder>(){
        // array lists for content informations
        private val contents: ArrayList<Notice> = ArrayList()

        init{
            fireStore?.collection("Notice")?.get()
                ?.addOnSuccessListener {result ->
                    contents.clear()
                    for(document in result){
                        val item = Notice(document["title"] as String?,
                            document["content"] as String?, document["date"] as String?)

                        contents.add(item)
                    }
                    notifyDataSetChanged()
                }
                ?.addOnFailureListener {
                    Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
                }
        }

        inner class ViewHolder(val binding: NoticeEditBinding): RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(NoticeEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // title
            val title = contents[position].title.toString()
            // date
            val date = contents[position].date.toString()
            // get fileName
            val fileName = "$title $date"
            // title
            holder.binding.title.text = title
            // content
            holder.binding.content.text = contents[position].content

            // delete file
            holder.binding.delete.setOnClickListener {
                fireStore?.collection("Notice")?.document(fileName)?.delete()?.addOnSuccessListener {
                    Toast.makeText(applicationContext,getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                    finish()
                }?.addOnFailureListener {
                    Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
                }
            }

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