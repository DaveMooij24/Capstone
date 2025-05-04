package nl.hva.capstone.ui.components.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import nl.hva.capstone.R

@Composable
fun KapsalonBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 0.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.kapsalon_mooij_banner_1),
            contentDescription = "Banner 1 (Logo)",
            modifier = Modifier
                .width(130.dp)
                .fillMaxHeight()
        )

        Image(
            painter = painterResource(id = R.drawable.kapsalon_mooij_banner_2),
            contentDescription = "Banner 2",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        )
    }
}