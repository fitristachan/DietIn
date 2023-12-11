package com.dietinapp.ui.activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.dietinapp.R
import com.dietinapp.database.datastore.UserPreference
import com.dietinapp.database.datastore.UserPreferenceViewModel
import com.dietinapp.database.datastore.UserPreferenceViewModelFactory
import com.dietinapp.database.datastore.dataStore
import com.dietinapp.ui.screen.onboarding.FirstScreen
import com.dietinapp.database.preferences.OnboardingManager
import com.dietinapp.ui.screen.onboarding.SecondScreen
import com.dietinapp.ui.theme.DietInTheme
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class OnBoardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()

        val onboardingManager = OnboardingManager(this)
        val onboardingCompleted = onboardingManager.getData("isCompleted", false)

        val pref = UserPreference.getInstance(application.dataStore)
        val userPreferenceViewModel =
            ViewModelProvider(
                this,
                UserPreferenceViewModelFactory(pref)
            )[UserPreferenceViewModel::class.java]

        userPreferenceViewModel.getSession().observe(this) { session: Boolean? ->
            if (session == true && auth.currentUser != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else if (onboardingCompleted) {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                setContent {
                    DietInTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            OnBoarding()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoarding(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        val pageCount = 2
        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        val onBoardingManager = remember { OnboardingManager(context) }
        val data = remember { mutableStateOf(onBoardingManager.getData("isCompleted", false)) }

        HorizontalPager(
            pageCount = pageCount,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) { page ->
            if (page == 0) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Row(
                        Modifier
                            .height(50.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                onBoardingManager.saveData("isCompleted", true)
                                data.value = true
                                context.findActivity()?.finish()
                                context.startActivity(
                                    Intent(
                                        context,
                                        AuthActivity::class.java
                                    )
                                )
                            },
                        ) {
                            Text(
                                text = stringResource(R.string.skip_button),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        FirstScreen()
                        Spacer(
                            Modifier
                                .height(56.dp)
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Spacer(
                        Modifier
                            .height(50.dp)
                            .fillMaxWidth()
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        SecondScreen()
                        Button(
                            onClick = {
                                onBoardingManager.saveData("isCompleted", true)
                                data.value = true
                                context.findActivity()?.finish()
                                context.startActivity(
                                    Intent(
                                        context,
                                        AuthActivity::class.java
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.second_onboarding_button),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.background,
                            )
                        }
                    }
                }
            }
        }
        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            HorizontalPagerIndicator(
                pageCount = 2,
                pagerState = pagerState,
                inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant,
                activeColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    val currentPage = pagerState.currentPage
                    val nextPage = if (currentPage < pageCount) currentPage + 1 else 0
                    coroutineScope.launch { pagerState.animateScrollToPage(nextPage) }
                }

            )
        }
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
                .collect { currentPage ->
                    pagerState.animateScrollToPage(currentPage)
                }
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview(showBackground = true)
@Composable
fun OnBoardingPreview() {
    DietInTheme {
        OnBoarding()
    }
}