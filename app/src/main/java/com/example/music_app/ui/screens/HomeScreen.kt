package com.example.music_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.music_app.data.music.entity.AlbumEntity
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.ui.components.MusicList
import com.example.music_app.ui.components.SearchBar
import com.example.music_app.ui.components.BottomNavBar
import com.example.music_app.ui.components.PlayingTrack
import com.example.music_app.viewmodel.HomeViewModel
import com.example.music_app.viewmodel.MusicViewModel
import com.example.music_app.viewmodel.PlayingTrackViewModel
import com.example.music_app.viewmodel.factory.MusicViewModelFactory
import com.example.music_app.viewmodel.UserViewModel
import com.example.music_app.viewmodel.factory.HomeViewModelFactory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest.Builder
import com.example.music_app.R
import com.example.music_app.data.music.entity.ArtistEntity

@Composable
fun HomeScreen(navController: NavController,
               repository: MusicRepository,
               userViewModel: UserViewModel,
               playingTrackViewModel: PlayingTrackViewModel = viewModel()
) {
    val musicViewModel: MusicViewModel = viewModel(
        factory = MusicViewModelFactory(repository)
    )

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )

    var query by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableIntStateOf(0) }

    val searchResult by musicViewModel.searchResult.collectAsState()
    val userState = userViewModel.user.collectAsState()
    val user = userState.value

    val context = LocalContext.current
    val loading by musicViewModel.loading.collectAsState()

    val showController by playingTrackViewModel.showController.collectAsState()

    var recentlyPlayedAlbums by remember { mutableStateOf<List<AlbumEntity>>(emptyList()) }
    var loadingRecentAlbums by remember { mutableStateOf(true) }

    var recentlyPlayedArtists by remember { mutableStateOf<List<ArtistEntity>>(emptyList())}
    var loadingRecentArtists by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        homeViewModel.saveSampleData()
        musicViewModel.loadAllData{ message ->
            if (message.isNotEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry, user?.uid) {
        if (navBackStackEntry?.destination?.route == "home" && user?.uid != null) {
            user.uid.let { userId ->
                loadingRecentAlbums = true
                val albums = repository.getRecentlyPlayedAlbums(userId)
                recentlyPlayedAlbums = albums
                loadingRecentAlbums = false

                loadingRecentArtists = true
                val artists = repository.getRecentlyPlayedArtists(userId)
                recentlyPlayedArtists = artists
                loadingRecentArtists = false
            }
        } else if (navBackStackEntry?.destination?.route != "home") {
            recentlyPlayedAlbums = emptyList()
            recentlyPlayedArtists = emptyList()
        }
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                if (showController) {
                    PlayingTrack(
                        viewModel = playingTrackViewModel,
                        repository = repository,
                        userId = user?.uid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                BottomNavBar(
                    navController = navController,
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        selectedIndex = index
                    },
                    userId = user?.uid
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                query = query,
                onQueryChange = { newQuery ->
                    query = newQuery
                    musicViewModel.search(newQuery)
                },
                onClose = {
                    query = ""
                }
            )

            if (query.isNotEmpty()) {
                MusicList(
                    loading = loading,
                    searchResult = searchResult,
                    navController = navController
                )
            } else if (loadingRecentAlbums) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (recentlyPlayedAlbums.isNotEmpty()) {
                RecentlyPlayedAlbums(
                    albums = recentlyPlayedAlbums,
                    onAlbumClick = { albumId ->
                        navController.navigate("albumDetails/$albumId")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (loadingRecentArtists) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (recentlyPlayedArtists.isNotEmpty()) {
                RecentlyPlayedArtists(
                    artists = recentlyPlayedArtists,
                    onArtistClick = { artistId ->
                        navController.navigate("artistDetails/$artistId")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RecentlyPlayedAlbums(
    albums: List<AlbumEntity>,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (albums.isNotEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = "Recently Played Albums",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(albums) { album ->
                    Card(
                        modifier = Modifier
                            .width(150.dp)
                            .clickable { onAlbumClick(album.id) }
                            .clip(RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors( MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    Builder(LocalContext.current).data(
                                    data = album.artworkUrl ?: ""
                                ).apply(block = { ->
                                    placeholder(R.drawable.ic_record_player_gray)
                                    error(R.drawable.ic_record_player_gray)
                                }).build()),
                                contentDescription = "Album artwork",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = album.title,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentlyPlayedArtists(
    artists: List<ArtistEntity>,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (artists.isNotEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = "Recently Played Artists",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(artists) { artist ->
                    Card(
                        modifier = Modifier
                            .width(150.dp)
                            .clickable { onArtistClick(artist.id) }
                            .clip(RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    Builder(LocalContext.current).data(
                                        data = artist.imageUrl ?: ""
                                    ).apply(block = { ->
                                        placeholder(R.drawable.ic_user_avatar_gray)
                                        error(R.drawable.ic_user_avatar_gray)
                                    }).build()),
                                contentDescription = "Artist image",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(60.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = artist.name,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}