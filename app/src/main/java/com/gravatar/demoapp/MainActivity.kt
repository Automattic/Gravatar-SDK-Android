package com.gravatar.demoapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.demoapp.theme.GravatarDemoAppTheme
import com.gravatar.demoapp.theme.giveMeColor
import com.gravatar.publicapi.apis.ProfileApi
import com.gravatar.publicapi.infrastructure.ApiClient
import com.gravatar.publicapi.models.Profile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GravatarDemoAppTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    var username by remember { mutableStateOf("hamorillo") }
                    var realName by remember { mutableStateOf("") }
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        GravatarUserNameInput(
                            username = username,
                            onValueChange = { username = it },
                        )
                        Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                ApiClient().createService(ProfileApi::class.java)
                                    .showProfileByUsername(
                                        username
                                    ).enqueue(object : Callback<Profile> {
                                    override fun onResponse(
                                        call: Call<Profile>,
                                        response: Response<Profile>
                                    ) {
                                        realName = if (response.isSuccessful) {
                                            response.body()?.entry?.first()?.name?.formatted ?: ""
                                        } else {
                                            "ERROR! ❌"
                                        }
                                    }

                                    override fun onFailure(call: Call<Profile>, t: Throwable) {
                                        realName = "ERROR! ❌"
                                        Log.e("MainActivity", "onFailure: ${t.message}")
                                    }
                                })
                            }) {
                            Text(text = "Load Gravatar")
                        }
                        Text(text = realName)
                    }
                }
            }
        }
    }
}

@Composable
fun GravatarUserNameInput(
    username: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = username,
        onValueChange = onValueChange,
        label = { Text("Label") }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GravatarDemoAppTheme {
        GravatarUserNameInput("hamorillo", {})
    }
}
