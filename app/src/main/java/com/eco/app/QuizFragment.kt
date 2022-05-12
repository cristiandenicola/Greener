package com.eco.app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.eco.app.databinding.FragmentQuizBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates
import kotlin.random.Random

//TODO convertire su firebase le domande del quiz da stringhe ad id per poi prendere la domanda di id corrispondente nel file strings.xml (per ottimizzare la traduzione)
class QuizFragment : Fragment() {
    private lateinit var binding: FragmentQuizBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var buttons: Array<Button?>
    private lateinit var txt_question: TextView
    private lateinit var reply: String
    private lateinit var correctreply: String
    private lateinit var getQuizDataListener: ValueEventListener
    private lateinit var quizReference: DatabaseReference
    private var quizQuestionsNumber: Int = 10
    var quizList = arrayListOf<Question>()
    val resultFragment = ResultQuizFragment()


    companion object {
        var correct_replies by Delegates.notNull<Int>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuizBinding.inflate(inflater, container, false)

        binding.quizShimmer.startShimmer()

        database =
            Firebase.database(RegisterPage.PATHTODB)

        //getto i button in un array per comodità
        buttons = getButtons()
        txt_question = binding.tvQuestion
        correct_replies=0
        setQuiz(txt_question, buttons)


        return binding.root

    }


    fun setQuiz(txt_question: TextView, buttonsArray: Array<Button?>) {
        val randomQuestion = Random.nextInt(10)
        val randomlistButtons = (0..3).shuffled().take(4)
        quizReference = database.getReference("Quiz")

        //TODO usare il get per prendere una sola volta i valori dal db (è più ottimizzato)
        /*quizReference.get().addOnSuccessListener { result->
            result.children
        }*/

        getQuizDataListener = quizReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val rispList = arrayListOf<String>()
                    val questionInDb = i.child("domanda").getValue(String::class.java)
                    val risp0 = i.child("risp0").getValue(String::class.java)
                    rispList.add(risp0!!)
                    val risp1 = i.child("risp1").getValue(String::class.java)
                    rispList.add(risp1!!)
                    val risp2 = i.child("risp2").getValue(String::class.java)
                    rispList.add(risp2!!)
                    val risp3 = i.child("risp3").getValue(String::class.java)
                    rispList.add(risp3!!)
                    val questionObject = Question(questionInDb, rispList)
                    quizList.add(questionObject)
                    Log.d("QUESTIONS", questionObject.toString())
                }
                val question = quizList[randomQuestion]
                Log.d("QUESTIONS", question.toString())
                txt_question.text = question.question//random question
                correctreply = question.listofrisp[0]
                for (i in 0..3) {
                    buttonsArray[randomlistButtons.get(i)]?.setText(question.listofrisp[i])
                    buttonsArray[i]?.setOnClickListener {
                        reply = buttonsArray[i]?.text.toString()
                        checkreply(reply, correctreply, i)
                    }
                }
                if(binding.quizShimmer.isShimmerStarted) {
                    binding.quizShimmer.stopShimmer()
                    binding.quizShimmer.visibility = View.INVISIBLE
                    binding.quizConstraintLayout.visibility = View.VISIBLE

                    /*val metrics = requireContext().resources.displayMetrics
                    //binding.questionContainer.layoutParams.height=0
                    binding.questionContainer.animate()
                        .scaleYBy(metrics.density*180)
                        .setDuration(500)
                        .setUpdateListener {
                            val layoutParams=binding.linearLayout.layoutParams as RelativeLayout.LayoutParams
                            layoutParams.addRule(RelativeLayout.BELOW,binding.questionContainer.id)
                            layoutParams.topMargin=(metrics.density*350).toInt()
                            binding.linearLayout.layoutParams=layoutParams
                        }*/
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun checkreply(reply: String, correctReply: String, position: Int) {
        if (reply == correctReply) {
            //Toast.makeText(requireActivity(), "RISPOSTA ESATTA", Toast.LENGTH_SHORT).show()
            correct_replies++
            quizQuestionsNumber--
            if (quizQuestionsNumber == 0) {
                replaceFragment(resultFragment)
            }
            setQuiz(txt_question, buttons)
        } else {
            //Toast.makeText(requireActivity(), "RISPOSTA ERRATA", Toast.LENGTH_SHORT).show()
            quizQuestionsNumber--
            if (quizQuestionsNumber == 0) {
                replaceFragment(resultFragment)
            }
            setQuiz(txt_question, buttons)
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.home_fragment_container, fragment)
        transaction?.commit()
    }

    fun getButtons(): Array<Button?> {
        val array: Array<Button?> = Array(4) { null }
        val btnR1 = binding.btnR1
        val btnR2 = binding.btnR2
        val btnr3 = binding.btnR3
        val btnr4 = binding.btnR4
        array[0] = btnR1
        array[1] = btnR2
        array[2] = btnr3
        array[3] = btnr4
        return array
    }

    override fun onDestroy() {
        super.onDestroy()
        quizReference.removeEventListener(getQuizDataListener)
    }
}