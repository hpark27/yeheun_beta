package com.beta.yeheun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beta.yeheun.databinding.ActivityPostingeditBinding
import com.beta.yeheun.databinding.GoodsEditBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class PostingEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostingeditBinding
    // firebase firestore
    private var fireStore: FirebaseFirestore? = null
    // snapshot
    private var snapshot: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostingeditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firestore
        fireStore = FirebaseFirestore.getInstance()

        // recyclerview adapter
        binding.goodsRecyclerview.adapter = ViewAdapter()
        binding.goodsRecyclerview.layoutManager = LinearLayoutManager(this)

        // back
        binding.back.setOnClickListener {
            finish()
        }

        // delete firestore data
        binding.offering.setOnClickListener {
            val fileName = getString(R.string.offering_id)
            FirebaseFirestore.getInstance().collection("Offering").document(fileName).delete()
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, getString(R.string.fcm_success), Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
                    }
        }
    }

    override fun onStop() {
        super.onStop()
        snapshot?.remove()
    }

    inner class ViewAdapter : RecyclerView.Adapter<PostingEditActivity.ViewAdapter.ViewHolder>(){
        // array lists for content informations
        private val contents: ArrayList<Goods> = ArrayList()

        init{
            fireStore?.collection("Goods")?.get()
                ?.addOnSuccessListener {result ->
                    contents.clear()
                    for(document in result){
                        val item = Goods(document["name"] as String?, document["price"] as String?,
                            document["remain"] as String?)

                        contents.add(item)
                    }
                    notifyDataSetChanged()
                }
                ?.addOnFailureListener {
                    Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
                }
        }

        inner class ViewHolder(val binding: GoodsEditBinding): RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(GoodsEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // title
            val fileName = contents[position].name.toString()

            holder.binding.name.text = fileName

            // delete file
            holder.binding.delete.setOnClickListener {
                fireStore?.collection("Goods")?.document(fileName)?.delete()?.addOnSuccessListener {
                    Toast.makeText(applicationContext,getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                    finish()
                }?.addOnFailureListener {
                    Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun getItemCount(): Int {
            return contents.size
        }
    }
}