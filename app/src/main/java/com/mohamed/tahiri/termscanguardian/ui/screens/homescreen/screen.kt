package com.mohamed.tahiri.termscanguardian.ui.screens.homescreen

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import com.google.gson.Gson
import com.mohamed.tahiri.termscanguardian.R
import com.mohamed.tahiri.termscanguardian.data.Resulta
import com.mohamed.tahiri.termscanguardian.database.Data
import com.mohamed.tahiri.termscanguardian.database.DataModelViewModel
import com.mohamed.tahiri.termscanguardian.screen
import com.mohamed.tahiri.termscanguardian.ui.getNameFromFile
import com.mohamed.tahiri.termscanguardian.ui.theme.TermScanGuardianTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: DataModelViewModel) {
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    val openSheet = remember { mutableStateOf(false) }
    val showFab = remember { mutableStateOf(false) }
    val listOfImage = remember { mutableListOf<Uri?>() }
    val text = remember { mutableStateOf("") }
    val textOptimize = remember { mutableStateOf("") }
    val enabled = remember { mutableStateOf(true) }
    val buttonText = remember { mutableStateOf("Scan") }
    val search = remember { mutableStateOf("") }
    val images = remember { mutableStateOf("") }
    val scan = remember { mutableStateOf("") }
    val gson = Gson()
    var openSheetData: Data by remember {
        mutableStateOf(Data(text = null, time = null, images = null))
    }
    val pickPDF = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        it.let {
            for (i in it?.let { it1 -> convertPdfToImageURIs(context, it1) }!!) {
                listOfImage.add(i)
            }
            showFab.value = true
        }
    }
    val imageCropLauncher =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let {
                    listOfImage.add(it)
                    showFab.value = true
                }
            } else {
                println("ImageCropping error: ${result.error}")
            }
        }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            val cropOptions = CropImageContractOptions(
                null,
                CropImageOptions(imageSourceIncludeGallery = false)
            )
            imageCropLauncher.launch(cropOptions)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConstraintLayout {
                val (card, subCard) = createRefs()
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .constrainAs(card) {
                        top.linkTo(parent.top)
                    }
                    .clip(shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
                    .background(color = MaterialTheme.colorScheme.primary)) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column {
                            Text(text = "Hello", color = MaterialTheme.colorScheme.background)
                            Text(
                                text = getNameFromFile(context),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.background
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth(.8f)
                        .height(100.dp)
                        .shadow(10.dp, shape = RoundedCornerShape(10.dp))
                        .constrainAs(subCard) {
                            top.linkTo(card.bottom)
                            bottom.linkTo(card.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }, colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Card(modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                val cropOptions = CropImageContractOptions(
                                    null,
                                    CropImageOptions(imageSourceIncludeGallery = false)
                                )
                                val permissionCheckResult = ContextCompat.checkSelfPermission(
                                    context, android.Manifest.permission.CAMERA
                                )
                                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                    imageCropLauncher.launch(cropOptions)
                                } else {
                                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }
                            }
                            .shadow(2.dp, shape = RoundedCornerShape(10.dp))) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceAround,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.camera),
                                    contentDescription = ""
                                )
                                Text(
                                    text = "Take Photo",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Card(modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                val cropOptions = CropImageContractOptions(
                                    null,
                                    CropImageOptions(imageSourceIncludeCamera = false)
                                )
                                imageCropLauncher.launch(cropOptions)
                            }
                            .shadow(2.dp, shape = RoundedCornerShape(10.dp))) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceAround,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.picture),
                                    contentDescription = ""
                                )
                                Text(
                                    text = "Pick Image",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Card(modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                pickPDF.launch("application/pdf")
                            }
                            .shadow(2.dp, shape = RoundedCornerShape(10.dp))) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceAround,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.document),
                                    contentDescription = ""
                                )
                                Text(text = "Pick PDF", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = search.value,
                    onValueChange = { search.value = it },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = ""
                        )
                    },
                    placeholder = {
                        Text(text = "Searching for...")
                    },
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                val dataList = if (search.value == "") viewModel.getData()
                    .collectAsState(initial = emptyList()) else viewModel.searchData(search.value)
                    .collectAsState(initial = emptyList())
                if (dataList.value.isEmpty()) {
                    Text(text = "Nothing")
                } else {
                    LazyColumn(content = {
                        items(dataList.value) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(125.dp)
                                    .shadow(2.dp, shape = RoundedCornerShape(10.dp))
                                    .clickable {
                                        openSheetData = Data(
                                            id = it.id,
                                            text = it.text,
                                            time = it.time,
                                            images = it.images
                                        )
                                        openSheet.value = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LazyRow(
                                        modifier = Modifier
                                            .size(115.dp)
                                            .padding(5.dp)
                                    ) {
                                        items(it.images!!.split("*_*")) {
                                            if (it != "") {
                                                Image(
                                                    modifier = Modifier.size(115.dp),
                                                    bitmap = MediaStore.Images.Media.getBitmap(
                                                        context.contentResolver,
                                                        it.toUri()
                                                    ).asImageBitmap(),
                                                    contentDescription = ""
                                                )
                                            }
                                        }
                                    }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val sdf = SimpleDateFormat(
                                            "yyyy-MM-dd HH:mm:ss",
                                            Locale.getDefault()
                                        )
                                        val date = Date(it.time!!.toLong())
                                        val formattedDateTime = sdf.format(date)
                                        Text(
                                            text = "${
                                                gson.fromJson(
                                                    it.text,
                                                    Resulta::class.java
                                                ).summary
                                            }",
                                            maxLines = 3,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = formattedDateTime,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    })
                }

            }
        }
        AnimatedVisibility(visible = showFab.value) {
            ExtendedFloatingActionButton(
                text = { /*TODO*/ },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.play),
                        tint = MaterialTheme.colorScheme.background,
                        contentDescription = ""
                    )
                },
                onClick = { openDialog.value = true },
                expanded = false,
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    }

    when {
        openDialog.value -> {
            Dialog(onDismissRequest = { openDialog.value = false }) {
                Card {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        LazyRow(modifier = Modifier.weight(1f, false)) {
                            items(listOfImage) {
                                val source =
                                    ImageDecoder.createSource(context.contentResolver, it!!)
                                val bitmap = ImageDecoder.decodeBitmap(source)
                                Card(
                                    modifier = Modifier
                                        .padding(4.dp)
                                ) {
                                    Box(contentAlignment = Alignment.TopEnd) {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "",
                                            modifier = Modifier.size(300.dp)
                                        )
                                        IconButton(onClick = {
                                            listOfImage.remove(it)
                                            if (listOfImage.isEmpty()) {
                                                showFab.value = false
                                                openDialog.value = false
                                            } else {
                                                openDialog.value = false
                                                openDialog.value = true
                                            }
                                        }, enabled = enabled.value) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.cross),
                                                contentDescription = "",
                                                modifier = Modifier.padding(5.dp),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                enabled.value = false
                                buttonText.value = "Wait a few seconds..."
                                for (uri in listOfImage) {
                                    val bitmap = MediaStore.Images.Media.getBitmap(
                                        context.contentResolver,
                                        uri
                                    )
                                    val recognizer = TextRecognizer.Builder(context).build()
                                    if (!recognizer.isOperational) {
                                        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                                    } else {
                                        val frame = Frame.Builder().setBitmap(bitmap).build()
                                        val textBlockSparseArray = recognizer.detect(frame)
                                        val stringBuilder = StringBuilder()
                                        for (block in 0 until textBlockSparseArray.size()) {
                                            val textBlock = textBlockSparseArray.valueAt(block)
                                            stringBuilder.append(textBlock.value)
                                            stringBuilder.append(" ")
                                        }
                                        text.value = text.value + stringBuilder.toString()
                                            .replace("\n", " ") + "\n"
                                        images.value = images.value + uri.toString() + "*_*"
                                        text.value = text.value.replace("\n", "")
                                    }
                                }
                                for (i in text.value) {
                                    if (i.isLetter()) {
                                        textOptimize.value = textOptimize.value + i
                                    } else {
                                        textOptimize.value = textOptimize.value + " "
                                    }
                                }
                                getResponse(textOptimize.value) {
                                    scan.value = extractJsonFromString(it)
                                }
                                Handler().postDelayed({
                                    if (scan.value.isNotEmpty()) {
                                        val prefs =
                                            context.getSharedPreferences(
                                                "my_prefs",
                                                Context.MODE_PRIVATE
                                            )
                                        val editor = prefs.edit()
                                        editor.putString("text_key", scan.value)
                                        editor.apply()
                                        openDialog.value = false
                                        navController.navigate(screen.VerifieScreen.name)
                                        viewModel.addData(
                                            Data(
                                                text = scan.value,
                                                time = System.currentTimeMillis(),
                                                images = images.value
                                            )
                                        )
                                    }
                                }, 15000)

                            },
                            modifier = Modifier
                                .width(350.dp)
                                .padding(4.dp, 0.dp),
                            shape = RoundedCornerShape(10.dp),
                            enabled = enabled.value
                        ) {
                            Text(text = buttonText.value)
                        }
                    }
                }
            }
        }
    }
    when {
        openSheet.value -> {
            ModalBottomSheet(onDismissRequest = { openSheet.value = false }) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp)
                            .height(40.dp)
                            .clickable {
                                viewModel.deleteData(openSheetData)
                                openSheet.value = false
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Delete")
                        Icon(
                            painter = painterResource(id = R.drawable.trash),
                            contentDescription = ""
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(10.dp, 0.dp)
                            .clickable {
                                val prefs =
                                    context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                                val editor = prefs.edit()
                                editor.putString("text_key", openSheetData.text)
                                editor.apply()
                                openSheet.value = false
                                navController.navigate(screen.VerifieScreen.name)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("View")
                        Icon(
                            painter = painterResource(id = R.drawable.overview),
                            contentDescription = ""
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(10.dp, 0.dp)
                            .clickable { openSheet.value = false },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Share")
                        Icon(
                            painter = painterResource(id = R.drawable.share),
                            contentDescription = ""
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

fun convertPdfToImageURIs(context: Context, inputPdfUri: Uri): List<Uri> {
    val imageURIs = mutableListOf<Uri>()
    val outputDir = context.cacheDir
    val pdfRenderer = PdfRenderer(context.contentResolver.openFileDescriptor(inputPdfUri, "r")!!)
    for (pageIndex in 0 until pdfRenderer.pageCount) {
        val page = pdfRenderer.openPage(pageIndex)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        val outputFile = File(outputDir, "page${System.currentTimeMillis()}$pageIndex.jpg")
        saveBitmapToFile(bitmap, outputFile)
        imageURIs.add(outputFile.toUri())
        page.close()
    }
    pdfRenderer.close()
    return imageURIs
}

fun saveBitmapToFile(bitmap: Bitmap, outputFile: File) {
    FileOutputStream(outputFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
}


fun getResponse(question: String, callback: (String) -> Unit) {

    val apiKey = "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"
    val url = "https://api.openai.com/v1/chat/completions"
    val prompt =
        "Please analyze and provide a sample summary for the following terms and conditions: $question Then, identify the sections with severity ratings and present the findings in the following format : json ( summary , sections( id(start from 0) ,title ,content,risk(low or middle or high)))"
    val requestBody = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [
                  {
                    "role": "user",
                    "content": "$prompt"
                  }
                ],
                "temperature": 1,
                "max_tokens": 500,
                "top_p": 1,
                "frequency_penalty": 0,
                "presence_penalty": 0
            }
        """.trimIndent()

    val request = Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("error", "API failed", e)
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            if (body != null) {
                Log.v("data", body)
            } else {
                Log.v("data", "empty")
            }
            val jsonObject = body?.let { JSONObject(it) }
            val choicesArray = jsonObject!!.getJSONArray("choices")
            if (choicesArray.length() > 0) {
                val choiceObject = choicesArray.getJSONObject(0)
                val messageObject = choiceObject.getJSONObject("message")
                val content = messageObject.getString("content")
                callback(content.toString())
            } else {
                println("No choices found")
            }
        }
    }
    )
}


fun extractJsonFromString(content: String): String {
    var firstIndex = 0
    var lastIndex = 0
    for (i in 0..content.length) {
        if (content[i] == '{') {
            firstIndex = i
            break
        }
    }
    for (j in content.length - 1 downTo 0) {
        if (content[j] == '}') {
            lastIndex = j + 1
            break
        }
    }
    return content.substring(firstIndex, lastIndex)
}

@RequiresApi(Build.VERSION_CODES.P)
@Preview(showSystemUi = true)
@Composable
fun GreetingPreview() {
    TermScanGuardianTheme {
        //HomeScreen(rememberNavController())
    }
}


