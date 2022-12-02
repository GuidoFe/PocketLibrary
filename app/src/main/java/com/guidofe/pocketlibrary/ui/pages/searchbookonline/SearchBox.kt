package com.guidofe.pocketlibrary.ui.pages.searchbookonline

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.LanguageAutocomplete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(
    title: String,
    author: String,
    lang: String,
    setTitle: (String) -> Unit,
    setAuthor: (String) -> Unit,
    setLang: (String) -> Unit,
    onStartSearch: () -> Unit,
    onEmptyFieldsError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = {
                setTitle(it)
            },
            label = { Text(stringResource(R.string.title)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = author,
                onValueChange = { setAuthor(it) },
                label = { Text(stringResource(R.string.author)) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            LanguageAutocomplete(
                text = lang,
                onTextChange = { setLang(it) },
                label = { Text(stringResource(R.string.language)) },
                onOptionSelected = { setLang(it) },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))
        ElevatedButton(
            onClick = {
                if (title.isBlank() && author.isBlank())
                    onEmptyFieldsError()
                else
                    onStartSearch()
                // lazyPagingItems.refresh()
            },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.search_24px),
                    contentDescription = stringResource(R.string.search)
                )
                Text(stringResource(R.string.search).uppercase())
            }
        }
    }
}