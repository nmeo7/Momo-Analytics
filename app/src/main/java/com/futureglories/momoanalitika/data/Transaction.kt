package com.futureglories.momoanalitika

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "tx_id") val tx_id: String?,
    @ColumnInfo(name = "account") val account: String?,
    @ColumnInfo(name = "amount") val amount: Int?,
    @ColumnInfo(name = "subject_first_name") val subject_first_name: String?,
    @ColumnInfo(name = "subject_last_name") val subject_last_name: String?,
    @ColumnInfo(name = "subject_number") val subject_number: String?,
    @ColumnInfo(name = "time") val time: Long?,
    @ColumnInfo(name = "balance") val balance: Int?,
    @ColumnInfo(name = "fee") val fee: Int?,
    @ColumnInfo(name = "fee_percentage") val fee_percentage: Double?,
    @ColumnInfo(name = "message") val message: String?,
    @ColumnInfo(name = "token") val token: String?
)