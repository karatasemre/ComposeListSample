package com.ek.customlist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ek.customlist.ui.theme.ComposeListExampleTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListExampleTheme {
                val viewModel = viewModel<MainViewModel>()
                val isLoading by viewModel.isLoading.collectAsState()
                val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

                SwipeRefresh(state = swipeRefreshState, onRefresh = viewModel::loadStuff, indicator = {
                        state, refresh ->
                    SwipeRefreshIndicator(state = state, refreshTriggerDistance = refresh, backgroundColor = Color(0xFF63358B),
                        contentColor = Color.White)
                }) {

                    LazyColumn(modifier = Modifier.fillMaxSize()){
                        items(100){
                            SwipeToRevealItem {
                                ShimmerListItem (
                                    isLoading = isLoading,
                                    contentAfterLoading = {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)) {
                                            Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(40.dp))
                                            Spacer(modifier = Modifier.width(15.dp))
                                            Column {
                                                Text("User ${it+1}")
                                                Text("Phone Number")
                                            }
                                        }

                                    }, modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(20.dp)
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToRevealItem(content: @Composable () -> Unit) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val size = with(LocalDensity.current) { 100.dp.toPx() }
    val anchors = mapOf(0f to 0, -size to 1)  // 0 is the original position, -size is the revealed position

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.9f) },
                orientation = Orientation.Horizontal
            )
    ) {
        // Reveal area behind the item
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(200.dp)
                .align(Alignment.CenterEnd)
                .background(Color.White)
        ) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = null, modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = {
                        Toast
                            .makeText(context, "User added to favorites", Toast.LENGTH_SHORT)
                            .show()
                    }),tint = Color.Green)
                Spacer(modifier = Modifier.width(24.dp))
                Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = {
                        Toast
                            .makeText(context, "Message sent to user", Toast.LENGTH_SHORT)
                            .show()
                    }),tint = Color.DarkGray)
                Spacer(modifier = Modifier.width(24.dp))
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null, modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = {
                        Toast
                            .makeText(context, "User deleted", Toast.LENGTH_SHORT)
                            .show()
                    }),tint = Color.Red)
            }
        }

        // Main content which moves with swipe
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = swipeableState.offset.value.dp)
                .background(Color.LightGray)
        ) {
            content()
        }
    }
}
