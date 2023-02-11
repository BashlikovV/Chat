package by.bashlikovv.chat.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    data: DrawerUiState,
    drawerViewModel: DrawerViewModel = viewModel()
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colors.primary
    ) {
        Image(
            painter = painterResource(data.userImage),
            contentDescription = "user image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(75.dp)
        )
        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            onClick = {
                drawerViewModel.changeTheme()
            },
            shape = RoundedCornerShape(50.dp),
        ) {
            Image(
                painter = painterResource(if (data.darkTheme) R.drawable.wb_cloudy else R.drawable.wb_sunny),
                contentDescription = "theme",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(25.dp)
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = data.userName
        )
        Spacer(modifier = Modifier.height(15.dp))
    }
}