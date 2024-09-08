package com.armstrongindustries.jbradio.metadata

//variable holding the coil image.
object MyVariables{
    var stringAlbum = "https://radiojar-lib.appspot.com/get_media_image?size=orig&guid=f8eef4b4-c480-11eb-acff-fa163eb018f7"
}
/*
@Composable
fun metadataImage() {
    Column {
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(MyVariables.stringAlbum)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.sle_radio),
                contentDescription = stringResource(R.string.description),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.clip(CircleShape)
            )
        }
    }
}*/