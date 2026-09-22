package com.jenil.f1comp.ui.home.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.Transformation
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
import com.jenil.f1comp.util.TeamUtils

class TopCropTransformation(
    private val topFraction: Float = 0.22f
) : Transformation {

    override val cacheKey: String = "${TopCropTransformation::class.java.name}-$topFraction"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val cropHeight = (input.height * topFraction).toInt().coerceAtLeast(1)
        val cropSize = minOf(input.width, cropHeight) // square region
        val xOffset = (input.width - cropSize) / 2

        val output = createBitmap(cropSize, cropSize, input.config ?: Bitmap.Config.ARGB_8888)
        Canvas(output).drawBitmap(
            input,
            Rect(xOffset, 0, xOffset + cropSize, cropSize),
            Rect(0, 0, cropSize, cropSize),
            null
        )
        return output
    }
}
@Composable
fun DriverHeadshotCircle(
    headshotUrl: String?,
    driverName: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (!headshotUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(headshotUrl)
                    .crossfade(true)
                    .allowHardware(true)
                    .build(),
                contentDescription = driverName,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = driverName.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DriverProfileCircle(
    imageUrl: String?,
    driverName: String,
    size: Dp,
    modifier: Modifier = Modifier,
    numberDrawableRes: Int? = null,
    driverProfiles: List<DriverProfileEntity> = emptyList()
) {
    val context = LocalContext.current
    val numRes = numberDrawableRes ?: TeamUtils.getDriverDrawableRes(driverName, context, driverProfiles)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (numRes != null && numRes != 0) {
            Image(
                painter = painterResource(id = numRes),
                contentDescription = driverName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .transformations(TopCropTransformation(topFraction = 0.22f))
                    .crossfade(true)
                    .allowHardware(true)
                    .build(),
                contentDescription = driverName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val driverNum = TeamUtils.getDriverNumber(driverName, driverProfiles)
            val textToShow = if (driverNum.isNotBlank() && driverNum != "00") "#$driverNum" else driverName.take(1).uppercase()
            Text(
                text = textToShow,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}




@Composable
fun TeamLogoCircle(
    logoUrl: String?,
    teamName: String,
    size: Dp,
    containerColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    val logoDrawableRes = TeamUtils.getTeamLogoDrawableRes(teamName, context)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (logoDrawableRes != null && logoDrawableRes != 0) {
            Image(
                painter = painterResource(id = logoDrawableRes),
                contentDescription = teamName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(size * 0.16f)
            )
        } else if (!logoUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(logoUrl)
                    .crossfade(true)
                    .allowHardware(true)
                    .build(),
                contentDescription = teamName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(size * 0.16f)
            )
        } else {
            Text(
                text = teamName.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TeamLogoCircleTheme(
    logoUrl: String?,
    teamName: String,
    size: Dp,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    val logoDrawableRes = TeamUtils.getTeamLogoDrawableRes(teamName, context)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (logoDrawableRes != null && logoDrawableRes != 0) {
            Image(
                painter = painterResource(id = logoDrawableRes),
                contentDescription = teamName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(size * 0.16f)
            )
        } else if (!logoUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(logoUrl)
                    .crossfade(true)
                    .allowHardware(true)
                    .build(),
                contentDescription = teamName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(size * 0.16f)
            )
        } else {
            Text(
                text = teamName.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}