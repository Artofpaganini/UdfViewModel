package io.github.udfviewmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.udfviewmodel.presentation.with_map_holder.SomeDifficultScreen
import io.github.udfviewmodel.ui.UserActionLogger
import io.github.udfviewmodel.ui.theme.UdfViewModelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        println(isOfType<String>("Hello")) // true
        setContent {
            UdfViewModelTheme {
                SomeDifficultScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
inline fun <reified T> isOfType(value: Any) = value is T
inline fun <reified T> hasSameClass(value: Any) = value::class == T::class

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UdfViewModelTheme {
        Greeting("Android")
    }
}