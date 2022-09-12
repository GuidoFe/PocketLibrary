package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.*
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.utils.getUri

@Composable
fun BookPreviewRow(book: Book, authors: List<Author>, isFavorite: Boolean = false, loanStatus: Loan? = null) {
    BoxWithConstraints() {
        androidx.compose.material.Surface(
            color = MaterialTheme.colors.surface,
            elevation = 4.dp,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(100.dp)
                    .width(maxWidth)
                    .padding(5.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(painter = painterResource(R.drawable.sample_cover),
                        "test")
                    var statusBadge: Int? = null
                    if (loanStatus != null)
                        when (loanStatus.type) {
                            LoanType.LENT -> statusBadge = R.drawable.ic_baseline_lent_24
                            LoanType.BORROWED -> statusBadge = R.drawable.ic_baseline_borrowed_24
                            else -> statusBadge == null
                        }
                    if (statusBadge != null)
                        BadgeIcon(painter = painterResource(statusBadge),
                            stringResource(R.string.lent),
                            modifier = Modifier
                                .offset(3.dp, 3.dp)
                            )
                }
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)) {
                    Text(book.title, fontSize = 20.sp)
                    if (book.subtitle != null)
                        Text(book.subtitle, fontSize = 15.sp)
                }
                if (isFavorite)
                    Icon(painter = painterResource(R.drawable.ic_baseline_star_24),
                        "star")
            }

        }
    }
}

@Composable
@Preview(showSystemUi = true, device = Devices.PIXEL_4)
fun BookPreviewRowPreview() {
    PocketLibraryTheme(darkTheme = true) {
        BookPreviewRow(
            Book(
                bookId = 1,
                title = "Nice Title",
                subtitle = "Descriptive Subtitle",
                description = "Summary",
                publisher = "Penguin",
                published = 2005,
                coverURI = LocalContext.current.resources
                    .getUri(R.drawable.sample_cover),
                identifier = "4354325",
                score = 3.5f
            ),
            listOf(Author(0, "Frank Herbert"),
                   Author(1, "J.R.R. Tolkien")),
            true
            //Loan(0, LoanType.LENT, "edw", Date(2312L))
        )
    }
}