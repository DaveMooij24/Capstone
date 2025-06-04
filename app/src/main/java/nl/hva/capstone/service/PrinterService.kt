package nl.hva.printer

import android.content.Context
import android.util.Log
import com.epson.epos2.Epos2Exception
import com.epson.epos2.printer.Printer
import com.epson.epos2.printer.PrinterStatusInfo
import com.epson.epos2.printer.ReceiveListener
import com.google.firebase.Timestamp
import kotlinx.coroutines.*
import nl.hva.capstone.data.model.Product
import nl.hva.capstone.data.model.Sale
import nl.hva.capstone.data.model.SaleInformation
import nl.hva.capstone.data.model.Service
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrinterService(private val context: Context) : ReceiveListener {

    private var mPrinter: Printer? = null

    fun printReceipt(
        printerIp: String,
        clientName: String,
        appointmentDateTime: Date,
        saleInformation: List<SaleInformation>,
        nextAppointment: Date?,
        onResult: (Boolean, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            if (!isValidIpAddress(printerIp)) {
                onResult(false, "Invalid IP address format.")
                return@launch
            }

            if (initializePrinter()) {
                if (connectToPrinter(printerIp)) {
                    val result = buildAndSendReceiptData(clientName, appointmentDateTime, saleInformation, nextAppointment)
                    disconnectPrinter()
                    onResult(result, if (result) "Print successful!" else "Failed to print.")
                } else {
                    finalizePrinter()
                    onResult(false, "Failed to connect to printer.")
                }
            } else {
                onResult(false, "Failed to initialize printer.")
            }
        }
    }


    private fun isValidIpAddress(ip: String): Boolean {
        val ipPattern = Regex("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
        return ipPattern.matches(ip)
    }

    private suspend fun initializePrinter(): Boolean = withContext(Dispatchers.Main) {
        try {
            mPrinter = Printer(Printer.TM_T20, Printer.MODEL_ANK, context)
            mPrinter?.setReceiveEventListener(this@PrinterService)
            true
        } catch (e: Exception) {
            Log.e("PrinterService", "Initialization error", e)
            false
        }
    }

    private suspend fun connectToPrinter(ip: String): Boolean = withContext(Dispatchers.IO) {
        try {
            mPrinter?.connect("TCP:$ip", Printer.PARAM_DEFAULT)
            true
        } catch (e: Exception) {
            Log.e("PrinterService", "Connection error", e)
            false
        }
    }

    private suspend fun buildAndSendReceiptData(
        clientName: String,
        appointmentDateTime: Date,
        saleInformation: List<SaleInformation>,
        nextAppointment: Date?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val dutchFormatter = SimpleDateFormat("E dd-MM-yyyy HH:mm", Locale("nl", "NL"))
            val formattedDate = dutchFormatter.format(appointmentDateTime)

            mPrinter?.beginTransaction()
            mPrinter?.addTextAlign(Printer.ALIGN_CENTER)
            mPrinter?.addText("Kapsalon Mooij\nBraspenningstraat 70\n1827 JV Alkmaar\n06 46598233\n\n")
            mPrinter?.addTextAlign(Printer.ALIGN_LEFT)
            mPrinter?.addText(" Datum      : $formattedDate uur\n")
            mPrinter?.addText(" Medewerker : Sandra\n")
            mPrinter?.addText(" Klant      : $clientName\n\n")


            fun formatLine(left: String, right: String, width: Int = 47): String {
                val spaceCount = width - left.length - right.length
                val spaces = " ".repeat(if (spaceCount > 0) spaceCount else 1)
                return " $left$spaces$right\n"
            }

            var totalHigh = 0.0
            var totalLow = 0.0

            saleInformation.forEach {
                mPrinter?.addText(formatLine(it.name, "%.2f".format(it.price)))
                if(it.tax == 9) totalLow += it.price
                else if (it.tax == 21) totalHigh += it.price
            }

            val taxLow = totalLow / 109 * 9
            val taxHigh = totalHigh / 121 * 21
            val taxTotal = totalLow + totalHigh

            val taxLowFormatted = String.format("%.2f", taxLow)
            val taxHighFormatted = String.format("%.2f", taxHigh)
            val taxTotalFormatted = String.format("%.2f", taxTotal)

            val total = totalHigh + totalLow
            val totalFormatted = String.format("%.2f", total)

            mPrinter?.addText("\n")
            if(taxLowFormatted != "0,00"){
                mPrinter?.addText("  BTW 9%     : $taxLowFormatted\n")
            }
            if(taxLowFormatted != "0,00"){
                mPrinter?.addText("  BTW 21%    : $taxHighFormatted\n")
            }
            mPrinter?.addText("  BTW Totaal : $taxTotalFormatted\n")
            mPrinter?.addText(formatLine("TOTAAL:", "€ $totalFormatted"))
            mPrinter?.addText("\n")
            mPrinter?.addText(formatLine("Per pin voldaan", totalFormatted))
            mPrinter?.addText("\n")

            if (nextAppointment != null) {
                val dutchFormatter = SimpleDateFormat("EEEE dd MMMM yyyy 'om' HH:mm 'uur'", Locale("nl", "NL"))
                val formattedDate = dutchFormatter.format(nextAppointment)
                mPrinter?.addText(" Uw volgende afspraak:\n $formattedDate\n\n")


            } else {
                mPrinter?.addText(" Uw volgende afspraak:\n Geen toekomstige afspraak\n\n")
            }
            mPrinter?.addTextAlign(Printer.ALIGN_CENTER)
            mPrinter?.addText("Bedankt en tot ziens\n")
            mPrinter?.addText("www.kapsalonmooij.nl\n")
            mPrinter?.addFeedLine(3)
            mPrinter?.addCut(Printer.CUT_FEED)
            mPrinter?.sendData(Printer.PARAM_DEFAULT)
            true
        } catch (e: Exception) {
            Log.e("PrinterService", "Build/send error", e)
            try {
                mPrinter?.clearCommandBuffer()
                mPrinter?.endTransaction()
            } catch (_: Exception) {}
            false
        }
    }


    private suspend fun disconnectPrinter() = withContext(Dispatchers.IO) {
        try {
            mPrinter?.disconnect()
        } catch (e: Exception) {
            Log.e("PrinterService", "Disconnect error", e)
        } finally {
            finalizePrinter()
        }
    }

    private suspend fun finalizePrinter() = withContext(Dispatchers.Main) {
        try {
            mPrinter?.setReceiveEventListener(null)
            mPrinter = null
        } catch (_: Exception) {}
    }

    override fun onPtrReceive(printerObj: Printer?, code: Int, status: PrinterStatusInfo?, printJobId: String?) {
        Log.d("PrinterService", "onPtrReceive: $code")
    }
}
