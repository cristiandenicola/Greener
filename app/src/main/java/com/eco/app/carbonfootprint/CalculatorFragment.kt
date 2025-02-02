package com.eco.app.carbonfootprint

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eco.app.databinding.FragmentCalculatorBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar


class CalculatorFragment : Fragment() {

    var JourneyModeScore = 0 //variabile che tiene conto del punteggio dell'utente nella pagina "Journey Mode";
    //il punteggio va in base a che mezzo usa l'utente per muoversi, su una scala da 1 a 10. (0 non c'è perchè anche camminando un minimo si inquina)
    //1 = walk
    //3 = moto
    //5 = auto (trasportando più passeggeri di una moto inquina di più)
    //5 = taxi ("")
    //8 = bus (vale lo stesso concetto, più passeggeri si porta più si inquina)
    //10 = treno (valore massimo al mezzo che trasporta più persone (escluso aereo perchè ha una sezione propria))

    private lateinit var binding: FragmentCalculatorBinding

    //VAR "Journey Mode"
    private lateinit var cardAuto : MaterialCardView
    private lateinit var cardBus : MaterialCardView
    private lateinit var cardTreno : MaterialCardView
    private lateinit var cardTaxi : MaterialCardView
    private lateinit var cardMoto : MaterialCardView
    private lateinit var cardWalk : MaterialCardView
    private lateinit var btnNextJM : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        //BINDING "Journey Mode"
        cardAuto = binding.cvAuto
        cardBus = binding.cvBus
        cardTreno = binding.cvTrain
        cardTaxi = binding.cvTaxi
        cardMoto = binding.cvMoto
        cardWalk = binding.cvWalk
        btnNextJM = binding.btnNextJM

        //Listener per settare checked le varie card presenti nella GridView
        cardAuto.setOnClickListener {
            if (cardAuto.isChecked == false) {
                cardAuto.isChecked = true
                JourneyModeScore += 5 //aggiorno valore dello score
            } else {
                cardAuto.isChecked = false
                JourneyModeScore = 0 //nel caso tolga la spunta rimuovo il valore aggiunto
            }
        }

        cardBus.setOnClickListener {
            if (cardBus.isChecked == false) {
                cardBus.isChecked = true
                JourneyModeScore += 8
            } else {
                cardBus.isChecked = false
                JourneyModeScore = 0
            }
        }

        cardTreno.setOnClickListener {
            if (cardTreno.isChecked == false) {
                cardTreno.isChecked = true
                JourneyModeScore += 10
            } else {
                cardTreno.isChecked = false
                JourneyModeScore = 0
            }
        }

        cardTaxi.setOnClickListener {
            if (cardTaxi.isChecked == false) {
                cardTaxi.isChecked = true
                JourneyModeScore += 5
            } else {
                cardTaxi.isChecked = false
                JourneyModeScore = 0
            }
        }

        cardMoto.setOnClickListener {
            if (cardMoto.isChecked == false) {
                cardMoto.isChecked = true
                JourneyModeScore += 3
            } else {
                cardMoto.isChecked = false
                JourneyModeScore = 0
            }
        }

        cardWalk.setOnClickListener {
            if (cardWalk.isChecked == false) {
                cardWalk.isChecked = true
                JourneyModeScore += 1
            } else {
                cardWalk.isChecked = false
                JourneyModeScore = 0
            }
        }

        btnNextJM.setOnClickListener{
            var sum = calcolaCO2()
            findNavController().navigate(CalculatorFragmentDirections.fromPage0ToPage1(sum))
        }

        return binding.root
    }

    fun  calcolaCO2(): Float {
        var sumTotale : Float = 0.0F

        sumTotale += JourneyModeScore

        return sumTotale
    }
}