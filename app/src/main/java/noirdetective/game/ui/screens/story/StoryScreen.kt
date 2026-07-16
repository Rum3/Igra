package noirdetective.game.ui.screens.story

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noirdetective.game.R

@Composable
fun StoryScreen(
    storyId: String,
    viewModel: StoryViewModel,
    onJournalClick: () -> Unit,
    onOfficeClick: () -> Unit
) {
    val chapter = viewModel.currentChapter
    val isLoading = viewModel.isLoading
    val visitedChapters = viewModel.visitedChapters
    val actionPoints = viewModel.actionPoints

    // Use storyId as key to force reset state when navigating to a new storyId
    key(storyId) {
        var currentSegmentIndex by remember { mutableIntStateOf(0) }

        LaunchedEffect(Unit) {
            viewModel.loadChapter(storyId)
            currentSegmentIndex = 0
        }

        val context = LocalContext.current
        val imageResId = chapter?.backgroundImage?.let {
            context.resources.getIdentifier(it, "drawable", context.packageName)
        } ?: 0

        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background Layer
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black))
            }

            // 2. Dark Overlay Layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Black.copy(alpha = 0.7f),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // 3. Main UI Layer
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Red)
                }
            } else if (chapter == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading story", color = Color.White)
                }
            } else {
                // Split by "||" and be very thorough with cleaning whitespace
                val segments = remember(chapter.id, chapter.content) {
                    chapter.content.split("||")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                }
                
                val currentSegment = if (currentSegmentIndex < segments.size) segments[currentSegmentIndex] else segments.lastOrNull() ?: ""
                val hasMoreSegments = currentSegmentIndex < segments.size - 1

                Column(modifier = Modifier.fillMaxSize()) {
                    // Text Area (Scrollable)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(110.dp))

                            Crossfade(targetState = currentSegment, label = "textAnimation") { segmentText ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().minHeight(200.dp),
                                    color = Color.Black.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = chapter.title,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 2.sp
                                            ),
                                            color = Color.Red
                                        )
                                        
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 16.dp),
                                            thickness = 1.dp,
                                            color = Color.Red.copy(alpha = 0.5f)
                                        )
                                        
                                        Text(
                                            text = segmentText,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                lineHeight = 32.sp,
                                                fontSize = 19.sp
                                            ),
                                            color = Color.White,
                                            textAlign = TextAlign.Justify
                                        )

                                        if (chapter.id == "chapter_02_1" || chapter.id == "chapter_05_camden_hub") {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Surface(
                                                color = Color.Red.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(1.dp, Color.Red)
                                            ) {
                                                Text(
                                                    text = "ACTION POINTS REMAINING: $actionPoints",
                                                    modifier = Modifier.padding(8.dp),
                                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    // Interaction Area (Fixed at bottom)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                    startY = 0f
                                )
                            )
                            .padding(bottom = 50.dp, start = 24.dp, end = 24.dp, top = 10.dp)
                    ) {
                        if (hasMoreSegments) {
                            Button(
                                onClick = { currentSegmentIndex++ },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red.copy(alpha = 0.8f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("CONTINUE...", style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp))
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Logic for Chapter transitions
                                val initialEvidence = listOf("chapter_01_2a", "chapter_01_2b", "chapter_01_2c", "chapter_01_2g")
                                val viewedCount = visitedChapters.count { it in initialEvidence }
                                
                                val isEndOfChapter1 = chapter.id == "chapter_01_1" && viewedCount == 4
                                val isEndOfChapter3 = chapter.id == "chapter_03_final_files"
                                val isEndOfChapter4 = chapter.id == "chapter_04_restaurant_detail" || chapter.id == "chapter_04_warehouse_discovery"
                                val isEndOfChapter5 = chapter.id == "chapter_05_end" || chapter.id == "chapter_05_ignore_path" || chapter.id == "chapter_05_drugs_path"
                                val isEndOfChapter6 = chapter.id == "chapter_06_end"

                                if (isEndOfChapter1 || isEndOfChapter3 || isEndOfChapter4 || isEndOfChapter5 || isEndOfChapter6) {
                                    Button(
                                        onClick = onOfficeClick,
                                        modifier = Modifier.fillMaxWidth().height(60.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFD4AF37),
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(2.dp, Color.Red)
                                    ) {
                                        Text(
                                            "RETURN TO OFFICE DESK",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                chapter.choices.forEach { choice ->
                                    val isVisited = visitedChapters.contains(choice.nextChapterId)
                                    
                                    Button(
                                        onClick = { viewModel.makeChoice(choice) },
                                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isVisited) Color.Black.copy(alpha = 0.5f) else Color.DarkGray.copy(alpha = 0.9f),
                                            contentColor = if (isVisited) Color.Gray else Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(
                                            width = 1.dp, 
                                            color = if (isVisited) Color.Green.copy(alpha = 0.4f) else Color.Red.copy(alpha = 0.4f)
                                        ),
                                        contentPadding = PaddingValues(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            if (isVisited) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp).padding(end = 8.dp),
                                                    tint = Color.Green.copy(alpha = 0.6f)
                                                )
                                            }
                                            Text(
                                                text = choice.text,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isVisited) FontWeight.Normal else FontWeight.Medium
                                                ),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Quick Navigation Layer (Top Most)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickNavButton(resId = R.drawable.go_to_journal, onClick = onJournalClick)
                QuickNavButton(resId = R.drawable.go_to_office, onClick = onOfficeClick)
            }
        }
    }
}

@Composable
fun QuickNavButton(resId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.6f))
            .border(BorderStroke(1.5.dp, Color(0xFFD4AF37)), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier.size(35.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

private fun Modifier.minHeight(height: androidx.compose.ui.unit.Dp) = this.defaultMinSize(minHeight = height)
