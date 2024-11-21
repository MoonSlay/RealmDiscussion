package ph.edu.auf.realmdiscussion.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ph.edu.auf.realmdiscussion.R

val barrioFontFamily = FontFamily(
    Font(R.font.barrio_regular, FontWeight.Black)
)

val monsterratFontFamily = FontFamily(
    Font(R.font.montserrat_alternates_bold, FontWeight.Bold),
    Font(R.font.montserrat_alternates_medium, FontWeight.Medium),
    Font(R.font.montserrat_alternates_black, FontWeight.Black),
    Font(R.font.montserrat_alternates_light, FontWeight.Light)
)

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = barrioFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = barrioFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = monsterratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

