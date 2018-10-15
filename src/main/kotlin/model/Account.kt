package model

class Account(val id: String, val type: AccountType)

enum class AccountType {
    PAYPAL, VISA, MASTERCARD
}