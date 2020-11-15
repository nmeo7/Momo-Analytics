package com.futureglories.momoanalitika.logic

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.futureglories.momoanalitika.data.Transaction
import com.futureglories.momoanalitika.data.TransactionTypes
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MomoParser (var context: Context?) {

    val INBOX = "content://sms/inbox"
    val TAG = "SMS_TAG"

    private fun parseWithdrawn(message: String): Transaction? {
        if (!message.startsWith("*153*txid:")) return null
        var ret = message.replace("*153*txid:", "TX_ID\t")
        // ret = ret.replace("*s*", "\nAMOUNT\t")
        ret = ret.replace("has via agent: ", "\nSUBJECT\t")
        ret = ret.replace(", withdrawn ", "\nAMOUNT\t")
        ret = ret.replace(" rwf from your mobile money account:", "\nACCOUNT\t")
        ret = ret.replace(" at 20", "\nTIME\t20")
        ret = ret.replace(
            " and you can now collect your money in cash. your new balance: ",
            "\nBALANCE\t"
        )
        ret = ret.replace(" rwf. fee paid: ", "\nFEE\t")
        ret = ret.replace(" rwf. message from agent: ", "\nMSG\t")

        ret = ret.replace(". *EN#", "")
        ret = "TYPE\t${TransactionTypes.WITHDRAW}\n$ret";

        // ret = ret.replace("#", "\r\n");
        return cleaned(ret)
    }

    private fun parseTransferred(message: String): Transaction? {
        if (!message.startsWith("*165*s*") && !message.startsWith("you have transferred ")) return null

        if (message.startsWith("you have transferred ")) {
            var ret = message.replace("you have transferred ", "AMOUNT\t")
            ret = ret.replace(" rwf to ", "\nSUBJECT\t")
            ret = ret.replace(") from your mobile money account ", ")\nACCOUNT\t")
            ret = ret.replace(" at 20", "\nTIME\t20")
            ret = ret.replace(". your new balance: ", "\nBALANCE\t")
            ret = ret.replace(". message from sender: ", "\nMSG\t")
            ret = ret.replace(". message to receiver: ", "\nMSG\t")
            ret = ret.replace(". financial transaction id: ", "\nTX_ID\t")
            ret = ret.replace(".", "")
            ret = "TYPE\t${TransactionTypes.TRANSFER}\n$ret";
            return cleaned(ret)
        }

        var ret = message.replace("*165*s*", "AMOUNT\t")
        ret = ret.replace(" rwf transferred to ", "\nSUBJECT\t")
        ret = ret.replace(") from ", ")\nACCOUNT\t")
        ret = ret.replace(" at 20", "\nTIME\t20")
        ret = ret.replace(" . fee was: ", "\nFEE\t")
        ret = ret.replace(" rwf. new balance: ", "\nBALANCE\t")
        ret = ret.replace(" . new balance: ", "\nBALANCE\t")
        ret = ret.replace(" rwf.", "@@@")
        if (ret.split("@@@".toRegex()).toTypedArray().size != 2) return null
        ret = ret.split("@@@".toRegex()).toTypedArray()[0]
        ret = "TYPE\t${TransactionTypes.TRANSFER}\n$ret";
        // ret = ret.replace("#", "\r\n");
        return cleaned(ret)
    }

    private fun parseReceived(message: String): Transaction? {
        if (!message.startsWith("*165*r*you have received ") &&
            !message.startsWith("*135*r*you have received ") &&
            !message.startsWith("*134*r*you have received ")
        ) return null

        var ret = message.replace("*165*r*", "TYPE\t" + TransactionTypes.RECEIVE)
        ret = ret.replace("*134*r*", "TYPE\t" + TransactionTypes.OTHER)
        ret = ret.replace("*135*r*", "TYPE\t" + TransactionTypes.DEPOSIT) // this is kubitsa
        ret = ret.replace("you have received ", "\nAMOUNT\t")
        ret = ret.replace(" rwf from ", "\nSUBJECT\t")
        ret = ret.replace(" on your mobile money account at ", "\nTIME\t")
        ret = ret.replace(". your new balance:", "\nBALANCE\t")
        ret = ret.replace(". message from sender:", "\nMSG\t")
        ret = ret.replace("your new balance:", "\nBALANCE\t")
        ret = ret.replace("rwf.", "@@@")
        if (ret.split("@@@".toRegex()).toTypedArray().size != 2) return null
        ret = ret.split("@@@".toRegex()).toTypedArray()[0]
        // ret = ret.replace("#", "\r\n");
        return cleaned(ret)
    }

    private fun cleaned(ret: String): Transaction {
        var ret = ret
        ret = ret.replace(". *en#", "")

        val details = ret.split("\n".toRegex()).toTypedArray()
        val detailsMap: MutableMap<String, String?> = HashMap()
        for (s in details) {
            val entry = s.split("\t".toRegex()).toTypedArray()
            if (entry.size != 2) continue
            var value = entry[1].trim { it <= ' ' }
            if (entry[0] == "TX_ID" || entry[0] == "AMOUNT" || entry[0] == "BALANCE" || entry[0] == "FEE"
            ) value = value.split(" ".toRegex()).toTypedArray()[0]
            if (entry[0] == "TIME") {
                if (value.split(" ".toRegex()).toTypedArray().size > 2)
                    value = value.split(" ".toRegex()).toTypedArray()[0] + " " +
                            value.split(" ".toRegex()).toTypedArray()[1]
            }
            detailsMap[entry[0]] = value
        }

        if (!detailsMap.containsKey("TYPE")) detailsMap["TYPE"] = TransactionTypes.UNKNOWN.toString()

        if (detailsMap.containsKey("SUBJECT"))
        {
            val recipient = detailsMap["SUBJECT"]!!.split(" \\(25".toRegex()).toTypedArray()
            if (recipient.size == 2) {
                detailsMap["SUBJECT_NAME"] = recipient[0].trim { it <= ' ' }
                var names = detailsMap["SUBJECT_NAME"]?.split(" ")!!.toTypedArray()
                detailsMap["SUBJECT_LAST_NAME"] = ""
                for (i in 1 until names.size)
                    detailsMap["SUBJECT_LAST_NAME"] += names[i] + " "
                detailsMap["SUBJECT_LAST_NAME"] = detailsMap["SUBJECT_LAST_NAME"]?.trim { it <= ' ' }
                detailsMap["SUBJECT_FIRST_NAME"] = names[0]

                detailsMap["SUBJECT_NUMBER"] = "25" + recipient[1].replace(")", "")
                ret = """
                ${detailsMap["SUBJECT_NAME"]!!.toUpperCase()}
                
                """.trimIndent()
                ret += """
                PHONE NUMBER  : ${detailsMap["SUBJECT_NUMBER"]}
                
                """.trimIndent()
            } else {
                detailsMap["SERVICE"] = detailsMap["SUBJECT"]
                ret = """
                ${detailsMap["SERVICE"]!!.toUpperCase()}
                
                """.trimIndent()
            }
            detailsMap.remove("SUBJECT")
        }

        if (detailsMap.containsKey("TX_ID")) ret = """
     #${detailsMap["TX_ID"]}
     $ret
     """.trimIndent()
        ret += """
            ${detailsMap["TIME"].toString()}
            
            """.trimIndent()
        ret += "Amount : " + detailsMap["AMOUNT"] + " Rwf"
        if (detailsMap.containsKey("FEE")) ret += " + " + detailsMap["FEE"] + " Fee"
        ret += "\n"
        ret += """Balance : ${detailsMap["BALANCE"]} Rwf
"""
        if (detailsMap.containsKey("MSG")) ret += "Message   : " + detailsMap["MSG"]

        val df = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
        detailsMap["TIME"] = df.parse(detailsMap["TIME"]!!)?.time.toString()

        if (detailsMap["BALANCE"] == "" || !detailsMap.containsKey("BALANCE")) detailsMap["BALANCE"] = "0"
        if (detailsMap["AMOUNT"] == "" || !detailsMap.containsKey("AMOUNT")) detailsMap["AMOUNT"] = "0"
        if (detailsMap["FEE"] == "" || !detailsMap.containsKey("FEE")) detailsMap["FEE"] = "0"
        if (detailsMap["AMOUNT"] != "0")
            detailsMap["FEE_PERCENTAGE"] = (100 * detailsMap["FEE"]!!.toDouble() / detailsMap["AMOUNT"]!!.toDouble()).toString()
        else
            detailsMap["FEE_PERCENTAGE"] = "0"

        Log.i(TAG, detailsMap.toString())


        detailsMap["UID"] = md5(detailsMap.toString())

        return Transaction(
            detailsMap["UID"]!!,
            enumValueOf<TransactionTypes>(detailsMap["TYPE"]!!).ordinal + 1,
            detailsMap["TX_ID"],
            detailsMap["ACCOUNT"],
            detailsMap["AMOUNT"]?.toInt(),
            detailsMap["SUBJECT_FIRST_NAME"],
            detailsMap["SUBJECT_LAST_NAME"],
            detailsMap["SUBJECT_NUMBER"],
            detailsMap["TIME"]?.toLong(),
            detailsMap["BALANCE"]?.toInt(),
            detailsMap["FEE"]?.toInt(),
            detailsMap["FEE_PERCENTAGE"]?.toDouble(),
            detailsMap["MSG"],
            detailsMap["TOKEN"]
        )
    }

    private fun parsePayment(message: String): Transaction? {
        if (!message.startsWith("*162*txid:") &&
            !message.startsWith("*161*txid:") &&
            !message.startsWith("*164*s*")
        ) return null
        var ret = message.replace("*162*txid:", "TX_ID\t")
        ret = ret.replace("*161*txid:", "TX_ID\t")
        ret = ret.replace("*164*s*y'ello,a transaction of", "AMOUNT\t")
        ret = ret.replace("*s*your payment of ", "\nAMOUNT\t")
        ret = ret.replace("rwf by ", "\nSUBJECT\t")
        ret = ret.replace(" on your momo account was successfully completed at ", "\nTIME\t")
        ret = ret.replace(". message from debit receiver: ", "\nMSG\t")
        ret = ret.replace(" rwf to ", "\nSUBJECT\t")
        ret = ret.replace(" has been completed at ", "\nTIME\t")
        ret = ret.replace(". your new balance:", "\nBALANCE\t")
        ret = ret.replace("rwf. fee was ", "\nFEE\t")
        ret = ret.replace(". message: ", "\nMSG\t")
        ret = ret.replace(". financial transaction id:", "\nTX_ID\t")
        ret = ret.replace("with token ", "\nTOKEN\t")
        ret = ret.replace("rwf", "")
        ret = ret.replace(". *en#", "")

        ret = "TYPE\t${TransactionTypes.PAYMENT}\n$ret";

        return cleaned(ret)
    }

    fun md5(s: String): String? {
        val digest: MessageDigest
        try {
            digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray(Charset.forName("US-ASCII")), 0, s.length)
            val magnitude: ByteArray = digest.digest()
            val bi = BigInteger(1, magnitude)
            return java.lang.String.format("%0" + (magnitude.size shl 1) + "x", bi)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun retrieveNowPlaying(): LinkedList<Transaction?> {
        var ret  = LinkedList<Transaction?>()
        val cursor: Cursor =
            context?.getContentResolver()?.query(Uri.parse(INBOX), null, null, null, null) !!
        Log.i(TAG, "### onCreate.")
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                var msgData = ""
                val addressCol: Int = cursor.getColumnIndex("address")
                val address: String = cursor.getString(addressCol)
                if (address == "M-Money") {
                    val date: Int = cursor.getColumnIndex("date")
                    val date_sent: Int = cursor.getColumnIndex("date_sent")
                    val body: Int = cursor.getColumnIndex("body")
                    val service_center: Int = cursor.getColumnIndex("service_center")
                    var bodyDetails: String = cursor.getString(body)
                    var type = -1
                    bodyDetails = bodyDetails.toLowerCase().replace("=", "")
                    if (bodyDetails.contains("payment")) type = 1
                    if (bodyDetails.contains("transferred")) type = 2
                    if (bodyDetails.contains("received")) type = 3
                    if (bodyDetails.contains("withdrawn")) type = 4
                    if (bodyDetails.contains("a transaction of")) type = 1
                    if (bodyDetails.contains("payment") && bodyDetails.contains("transferred") ||
                        bodyDetails.contains("payment") && bodyDetails.contains("received") ||
                        bodyDetails.contains("payment") && bodyDetails.contains("withdrawn") ||
                        bodyDetails.contains("transferred") && bodyDetails.contains("received") ||
                        bodyDetails.contains("transferred") && bodyDetails.contains("withdrawn") ||
                        bodyDetails.contains("received") && bodyDetails.contains("withdrawn")
                    ) {
                        type = -1
                    }
                    var Action = "Unknown"
                    if (type == 1) Action = "KWISHYURA"
                    if (type == 2) Action = "KOHEREZA"
                    if (type == 3) Action = "KWAKIRA"
                    if (type == 4) Action = "KUBIKUZA"
                    if (type == 1) ret.add(parsePayment(bodyDetails))
                    if (type == 2) ret.add(parseTransferred(bodyDetails))
                    if (type == 3) ret.add(parseReceived(bodyDetails))
                    if (type == 4) ret.add(parseWithdrawn(bodyDetails))

                    // for (idx in 0 until cursor.getColumnCount()) {
                        // msgData += " /// " + cursor.getColumnName(idx)
                            // .toString() + ":" + cursor.getString(idx)
                    // }
                    // Log.i(TAG, "onCreate: $msgData")
                    // Log.i(TAG, "parsed: $bodyDetails")
                }
                // use msgData
            } while (cursor.moveToNext())
        } else {
            return ret
        }
        return ret
    }
}