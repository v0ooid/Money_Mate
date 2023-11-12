package my.edu.tarc.moneymate.Transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransactionViewModel : ViewModel() {
     private val _result = MutableLiveData<String>()
     private val _title = MutableLiveData<String>()

     val result:LiveData<String> get() = _result
     val title:LiveData<String> get() = _title

     fun updateData(newData: String)
     {
          _result.value = newData
     }
     fun updateTitleData(newData: String)
     {
          _title.value = newData
     }
}