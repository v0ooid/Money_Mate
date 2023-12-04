package my.edu.tarc.moneymate.Transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.MonetaryAccountRepository
import my.edu.tarc.moneymate.Income.Income
import java.util.function.LongFunction

class TransactionViewModel : ViewModel() {
     private val _result = MutableLiveData<String>()
     private val _title = MutableLiveData<String>()
     private val _selectedAccount = MutableLiveData<String>()
     private val _transactionDescription = MutableLiveData<String>()
     private val _transactionType = MutableLiveData<String>()
     private val _categoryId = MutableLiveData<String>()
     private val _categoryImage = MutableLiveData<Int>()
     private val _toAccount = MutableLiveData<String>()

     val result:LiveData<String> get() = _result
     val title:LiveData<String> get() = _title
     val selectedAccount:LiveData<String> get() = _selectedAccount
     val transactionDescription:LiveData<String> get() = _transactionDescription
     val transactionType:LiveData<String> get() = _transactionType
     val categoryId:LiveData<String> get() = _categoryId
     val categoryImage:LiveData<Int> get() = _categoryImage
     val toAccount: LiveData<String> get() = _toAccount

     init {
         _transactionType.value = "income"

     }

     fun updateData(newData: String)
     {
          _result.value = newData
     }
     fun updateTitleData(newData: String)
     {
          _title.value = newData
     }
     fun updateSelectedAccountData(newData: String)
     {
          _selectedAccount.value = newData
     }
     fun updateTransactionDescription(newData: String)
     {
          _transactionDescription.value = newData
     }
     fun updateTransactionType(newData: String)
     {
          _transactionType.value = newData
     }
     fun updateCategoryId(newData: String)
     {
          _categoryId.value = newData
     }
     fun updateCategoryImage(newData: Int)
     {
          _categoryImage.value = newData
     }

     fun updateToAccountData(newData: String)
     {
          _toAccount.value = newData
     }

}