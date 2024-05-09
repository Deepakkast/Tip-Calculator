package app.ind.tipcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import app.ind.tipcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etBill.editText?.addTextChangedListener {
            calculateTipAndTotal()
        }

        // Calculate tip and total when tip percentage changes
        binding.tipSlider.addOnChangeListener { _, value, _ ->
            calculateTipAndTotal()
        }

        // Calculate tip and total when split by changes
        binding.tipSliderSplitBy.addOnChangeListener { _, value, _ ->
            calculateTipAndTotal()
        }

        // Calculate tip and total when rounding option changes
        binding.radioGrp.setOnCheckedChangeListener { group, checkedId ->
            calculateTipAndTotal()
        }

    }

    private fun calculateTipAndTotal() {
        val billAmount = binding.etBill.editText?.text.toString().toDoubleOrNull() ?: 0.0
        val tipPercentage = binding.tipSlider.value
        val splitBy = binding.tipSliderSplitBy.value
        val roundingOption = when (binding.radioGrp.checkedRadioButtonId) {
            R.id.radioUp -> RoundingOption.UP
            R.id.radioDown -> RoundingOption.DOWN
            else -> RoundingOption.NONE
        }

        val tipAmount = calculateTipAmount(billAmount, tipPercentage)
        val totalBill = calculateTotalBill(billAmount, tipAmount, roundingOption)

        updateUI(billAmount, tipAmount, totalBill, splitBy,tipPercentage)

    }


    private fun Double.ceil(): Double {
        return kotlin.math.ceil(this * 100) / 100
    }

    private fun Double.floor(): Double {
        return kotlin.math.floor(this * 100) / 100
    }

    private fun updateUI(
        billAmount: Double,
        tipAmount: Double,
        totalBill: Double,
        splitBy: Float,
        tipPercentage: Float
    ) {
        // Update UI with calculated values
        val perPersonAmount = totalBill / splitBy
        binding.tvbaseAmt.text = String.format("%.2f", billAmount)
        binding.tvtipAmt.text = String.format("%.2f", tipAmount)
        binding.tvtotalAmt.text = String.format("%.2f", totalBill)
        binding.tvPerPersonAmt.text = String.format("%.2f", perPersonAmount)
        binding.tvSplitBy.text = "Split By Members ${String.format("%.0f",splitBy)}"
        binding.tvChoosetip.text = "Choose Tip Percentage ${String.format("%.0f",tipPercentage)}"
    }



    private fun calculateTotalBill(billAmount: Double, tipAmount: Double, roundingOption: RoundingOption): Double {
        var total = billAmount + tipAmount
        when (roundingOption) {
            RoundingOption.UP -> total = total.ceil()
            RoundingOption.DOWN -> total = total.floor()
            RoundingOption.NONE -> {
            }
        }
        return total
    }

    private fun calculateTipAmount(billAmount: Double, tipPercentage: Float): Double {
        return billAmount * (tipPercentage / 100)
    }
}