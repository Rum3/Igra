package noirdetective.game.ui.screens.wallboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noirdetective.game.R
import noirdetective.game.data.local.entity.EvidenceItem
import noirdetective.game.ui.screens.notebook.NotebookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallBoardScreen(
    viewModel: NotebookViewModel,
    onBackClick: () -> Unit
) {
    val allEvidence by viewModel.collectedEvidence.collectAsState()
    val pinnedEvidence = allEvidence.filter { it.isPinned }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image - Using the cork board texture
        Image(
            painter = painterResource(id = R.drawable.wall_board),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Fill the screen with the cork texture
        )

        // Dark Overlay for better text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.6f)
                        ),
                        startY = 0f
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "INVESTIGATION WALL",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = Color(0xFFD4AF37)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.6f)
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (pinnedEvidence.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                "No evidence pinned to the wall yet.\nGo to FILES to pin important clues.",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(pinnedEvidence) { item ->
                            PinnedEvidenceCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinnedEvidenceCard(item: EvidenceItem) {
    // Looks like a Polaroid or a pinned note
    Surface(
        color = Color(0xFFF5F5DC), // Beige/Paper color
        shape = RoundedCornerShape(2.dp),
        shadowElevation = 8.dp,
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // "Pin" icon (Visual representaton of a red thumbtack)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Red, RoundedCornerShape(5.dp))
                    .shadow(2.dp, RoundedCornerShape(5.dp))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = item.shortDescription,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                ),
                color = Color.DarkGray,
                maxLines = 4,
                textAlign = TextAlign.Center
            )
        }
    }
}
