package noirdetective.game.ui.screens.office

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noirdetective.game.R

@Composable
fun OfficeScreen(
    viewModel: OfficeViewModel,
    onJournalClick: () -> Unit,
    onOfficeClick: () -> Unit,
    onCaseClick: () -> Unit,
    onWallBoardClick: () -> Unit,
    onChapter2Click: () -> Unit,
    onChapter4Click: () -> Unit,
    onChapter5Click: () -> Unit,
    onChapter6Click: () -> Unit,
    onChapter7Click: () -> Unit,
    onDebugClick: () -> Unit
) {
    val hasStartedCase = viewModel.hasStartedCase
    val canProgressToCh2 = viewModel.canProgressToChapter2()
    val canProgressToCh4 = viewModel.canProgressToChapter4()
    val canProgressToCh5 = viewModel.canProgressToChapter5()
    val canProgressToCh6 = viewModel.canProgressToChapter6()
    val canProgressToCh7 = viewModel.canProgressToChapter7()

    LaunchedEffect(Unit) {
        viewModel.checkProgress()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.office),
            contentDescription = "Office Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                        startY = 0f
                    )
                )
        )

        // Title
        Text(
            text = "DETECTIVE'S OFFICE",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            ),
            color = Color.Red,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
                .clickable { onDebugClick() } // Debug jump for testing
        )

        // Buttons Container
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Left Column: Wall Board then Journal
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Wall Board - Keeps its specific ratio and square corners
                    ImageButton(
                        resId = R.drawable.wall_board,
                        label = "WALL BOARD",
                        onClick = {
                            viewModel.hasVisitedWallBoard = true
                            onWallBoardClick()
                        },
                        width = 120.dp,
                        height = 160.dp,
                        cornerRadius = 0.dp
                    )
                    // Journal - Now consistent size
                    ImageButton(
                        resId = R.drawable.go_to_journal,
                        label = "JOURNAL",
                        onClick = {
                            viewModel.hasVisitedJournal = true
                            onJournalClick()
                        },
                        width = 120.dp,
                        height = 120.dp
                    )
                }

                // Right side: Files - Now consistent with Journal
                Column(
                    modifier = Modifier.padding(bottom = 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImageButton(
                        resId = R.drawable.go_to_office,
                        label = "FILES",
                        onClick = {
                            viewModel.hasVisitedFiles = true
                            onOfficeClick()
                        },
                        width = 120.dp,
                        height = 120.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Action Button
            val (buttonText, buttonAction, buttonColor) = when {
                canProgressToCh7 -> Triple("CONTINUE TO CHAPTER 7", onChapter7Click, Color(0xFF4CAF50).copy(alpha = 0.8f))
                canProgressToCh6 -> Triple("CONTINUE TO CHAPTER 6", onChapter6Click, Color(0xFF4CAF50).copy(alpha = 0.8f))
                canProgressToCh5 -> Triple("CONTINUE TO CHAPTER 5", onChapter5Click, Color(0xFF4CAF50).copy(alpha = 0.8f))
                canProgressToCh4 -> Triple("CONTINUE TO CHAPTER 4", onChapter4Click, Color(0xFF4CAF50).copy(alpha = 0.8f))
                canProgressToCh2 -> Triple("CONTINUE TO CHAPTER 2", onChapter2Click, Color(0xFF4CAF50).copy(alpha = 0.8f))
                hasStartedCase -> Triple("CONTINUE CASE", onCaseClick, Color.Red.copy(alpha = 0.7f))
                else -> Triple("START CASE", onCaseClick, Color.Red.copy(alpha = 0.7f))
            }

            Button(
                onClick = buttonAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color(0xFFD4AF37))
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (canProgressToCh2 || canProgressToCh4 || canProgressToCh5 || canProgressToCh6 || canProgressToCh7) 18.sp else 22.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ImageButton(
    resId: Int,
    label: String,
    onClick: () -> Unit,
    width: Dp = 160.dp,
    height: Dp = 160.dp,
    cornerRadius: Dp = 25.dp
) {
    val shape = RoundedCornerShape(cornerRadius)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width = width, height = height)
                .shadow(
                    elevation = 12.dp,
                    shape = shape,
                    ambientColor = Color(0xFFFFD700),
                    spotColor = Color(0xFFFFD700)
                )
                .clip(shape)
                .border(
                    BorderStroke(
                        2.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFD4AF37),
                                Color(0xFFFFD700),
                                Color(0xFFB8860B)
                            )
                        )
                    ),
                    shape
                )
                .clickable { onClick() }
        ) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                // Use Crop to prevent squashing/distortion
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            ),
            color = Color.LightGray
        )
    }
}
