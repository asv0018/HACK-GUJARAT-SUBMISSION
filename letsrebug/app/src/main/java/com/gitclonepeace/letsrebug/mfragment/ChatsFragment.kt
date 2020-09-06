package com.gitclonepeace.letsrebug.mfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.teamvpn.devhub.AdapterClasses.UserAdapter
import com.teamvpn.devhub.ModelClass.Chatlist
import com.teamvpn.devhub.ModelClass.Users
import com.teamvpn.devhub.Notifications.Token
import com.teamvpn.devhub.R


class ChatsFragment : Fragment() {


    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var userChatlist: List<Chatlist>? = null
    lateinit var recycler_view_chatlist: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recycler_view_chatlist = view.findViewById(R.id.recycler_view_chatlist)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        userChatlist = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                (userChatlist as ArrayList).clear()
                for(dataSnapshot in p0.children)
                {
                    val chatlist = dataSnapshot.getValue(Chatlist::class.java)
                    (userChatlist as ArrayList).add(chatlist!!)
                }
                retrieveChatList()

            }

            override fun onCancelled(error: DatabaseError) {



            }
        })



        updateToken(FirebaseInstanceId.getInstance().token)


        return view
    }

    private fun updateToken(token: String?) {

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList()
    {
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatUsersDB")
        ref!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                (mUsers as ArrayList).clear()

                for(dataSnapshot in p0.children)
                {
                    val user = dataSnapshot.getValue(Users::class.java)

                    for(eachChatList in userChatlist!!)
                    {
                        if(user!!.getUID().equals(eachChatList.getId()))
                        {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                //userAdapter = UserAdapter(context!!, (mUsers as ArrayList<Users>), true)
                userAdapter = UserAdapter(context, (mUsers as ArrayList<Users>), true)
                recycler_view_chatlist.adapter = userAdapter


            }

            override fun onCancelled(error: DatabaseError) {


            }

        })

    }
}


