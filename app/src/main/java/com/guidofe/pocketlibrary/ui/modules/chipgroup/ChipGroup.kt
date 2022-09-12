package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow

/**
 * Create a chip group of type T.
 * getText() is a lambda that return the text to display for the item of type T.
 * onDeleted() is triggered every time the user click on the x.
 */
@Composable
fun <T> ChipGroup(list: List<T>, getText: (T) -> String, onDeleted: (T) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(
        mainAxisSpacing = 5.dp,
        crossAxisSpacing = 5.dp,
        modifier = modifier
    ) {
       list.forEach { element: T ->
           DeletableChip(text = getText(element), onDeleted = {
               onDeleted(element)
           })
       }
    }
}

@Preview(device = Devices.NEXUS_5, showSystemUi = true)
@Composable
fun ChipGroupPreview() {
    ChipGroup<String>(listOf("Frank Herbert", "Italo Calvino", "Edgar Allan Poe"), {item: String -> item}, {})
}