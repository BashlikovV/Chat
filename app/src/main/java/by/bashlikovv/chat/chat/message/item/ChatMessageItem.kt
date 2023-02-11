package by.bashlikovv.chat.chat.message.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet

@Composable
fun MessagesItem(data: MessageItemUiState) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
    ) {
        val constraintSet = getMessageItemConstraints()

        ConstraintLayout(
            constraintSet = constraintSet,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(data.image),
                contentDescription = data.message,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(30.dp)
                    .layoutId("image")
            )
            Text(
                text = data.message,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                modifier = Modifier.layoutId("message")
            )
            Text(
                text = data.time,
                fontWeight = FontWeight.Thin,
                fontSize = 8.sp,
                modifier = Modifier.layoutId("time")
            )
        }
    }
}

private fun getMessageItemConstraints(): ConstraintSet {
    return ConstraintSet {
        val image = createRefFor("image")
        val message = createRefFor("message")
        val time = createRefFor("time")

        constrain(image) {
            start.linkTo(anchor = parent.start, margin = 5.dp)
            top.linkTo(anchor = parent.top, margin = 5.dp)
            bottom.linkTo(anchor = parent.bottom, margin = 5.dp)
        }
        constrain(message) {
            start.linkTo(anchor = image.end, margin = 5.dp)
            top.linkTo(anchor = parent.top, margin = 5.dp)
            bottom.linkTo(anchor = parent.bottom, margin = 5.dp)
        }
        constrain(time) {
            end.linkTo(anchor = parent.end, margin = 5.dp)
            bottom.linkTo(anchor = parent.bottom, margin = 5.dp)
        }
    }
}