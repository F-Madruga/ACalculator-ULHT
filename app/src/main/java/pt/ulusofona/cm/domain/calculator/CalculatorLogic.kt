package pt.ulusofona.cm.domain.calculator

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import pt.ulusofona.cm.data.local.entities.Operation
import pt.ulusofona.cm.data.local.room.dao.OperationDao
import pt.ulusofona.cm.ui.listeners.OnDisplayChanged
import pt.ulusofona.cm.ui.listeners.OnHistoryChanged
import retrofit2.Retrofit

class CalculatorLogic(private val storage: OperationDao, private val retrofit: Retrofit) {

    private val TAG = CalculatorLogic::class.java.simpleName

    fun insertSymbol(display: String, symbol: String, listener: OnDisplayChanged) {
        Log.i(TAG, "InsertSymbol")
        listener.onDisplayChanged(if (display.isEmpty()) symbol else display + symbol)
    }

    fun clear(display: String, listener: OnDisplayChanged) {
        Log.i(TAG, "Clear")
        listener.onDisplayChanged("0")
    }

    fun deleteSymbol(display: String, listener: OnDisplayChanged) {
        Log.i(TAG, "DeleteSymbol")
        listener.onDisplayChanged(if(display.length == 1) "0" else display.dropLast(1))
    }

    fun performOperation(expression: String, listener: OnDisplayChanged) {
        Log.i(TAG, "PerformOperation")
        val expressionBuilder = ExpressionBuilder(expression).build()
        val result = expressionBuilder.evaluate()
        CoroutineScope(Dispatchers.IO).launch {
            storage.insert(Operation(expression, result))
            Log.i(TAG, "Done inserting")
            listener.onAddOperation()
        }
        listener.onDisplayChanged(result.toString())
    }
}