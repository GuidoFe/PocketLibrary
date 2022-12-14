package com.guidofe.pocketlibrary.ui.pages.viewbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.guidofe.pocketlibrary.R

@Composable
fun SummaryTab(
    description: String?,
    isScrollable: Boolean,
    modifier: Modifier = Modifier
) {
    val summaryScrollState = rememberScrollState()
    Box(modifier = modifier) {
        if (description.isNullOrBlank()) {
            Text(
                stringResource(R.string.no_description),
                modifier = Modifier
                    .align(Alignment.Center)
            )
        } else {
            SelectionContainer() {
                Text(
                    text = description,

                    modifier = if (isScrollable)
                        Modifier.verticalScroll(summaryScrollState)
                    else Modifier
                )
            }
        }
    }
}