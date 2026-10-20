package com.example.calendar.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.AstronomicalCalculator
import com.example.calendar.core.Provinces
import com.example.calendar.model.CityLocation
import com.example.ui.theme.cardFrameColor

/**
 * City list grouped by province. Each province row has a › drawer that expands to its cities.
 * While searching, a flat list of matching cities is shown instead.
 */
@Composable
fun ProvinceCityPicker(
    selectedCityId: String,
    isFa: Boolean,
    query: String,
    height: Dp,
    onSelect: (CityLocation) -> Unit
) {
    var expanded by remember { mutableStateOf(setOfNotNull(Provinces.provinceOf(selectedCityId)?.nameEnglish)) }
    val q = query.trim().lowercase()

    LazyColumn(
        modifier = Modifier.fillMaxWidth().height(height),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (q.isNotEmpty()) {
            val found = AstronomicalCalculator.CITIES.filter {
                it.namePersian.contains(q) || it.nameEnglish.lowercase().contains(q)
            }
            items(found, key = { it.id }) { city ->
                CityRow(city, city.id == selectedCityId, isFa, onSelect)
            }
        } else {
            Provinces.ALL.forEach { province ->
                val isOpen = province.nameEnglish in expanded
                item(key = "p_" + province.nameEnglish) {
                    val cities = Provinces.cities(province)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                            .border(BorderStroke(1.dp, cardFrameColor), RoundedCornerShape(12.dp))
                            .clickable {
                                expanded = if (isOpen) expanded - province.nameEnglish else expanded + province.nameEnglish
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isFa) province.namePersian else province.nameEnglish,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isFa) "${cities.size} شهر" else "${cities.size} cities",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(26.dp)
                                .rotate(if (isOpen) 90f else 0f)
                        )
                    }
                }
                if (isOpen) {
                    items(Provinces.cities(province), key = { "c_" + it.id }) { city ->
                        Box2(Modifier.padding(start = 14.dp)) {
                            CityRow(city, city.id == selectedCityId, isFa, onSelect)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Box2(modifier: Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier.fillMaxWidth()) { content() }
}

@Composable
private fun CityRow(city: CityLocation, isChosen: Boolean, isFa: Boolean, onSelect: (CityLocation) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isChosen) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            )
            .clickable { onSelect(city) }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (isFa) city.namePersian else city.nameEnglish,
                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                color = if (isChosen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${city.latitude}°N, ${city.longitude}°E",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        RadioButton(selected = isChosen, onClick = { onSelect(city) })
    }
}
