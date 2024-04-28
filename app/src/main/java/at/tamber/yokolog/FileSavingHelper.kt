package at.tamber.yokolog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import java.io.FileDescriptor


interface FileSelectionEntryPoint {

    val fileSelectionOwner: ComponentActivity

    fun onFileCreated(fileDescriptor: FileDescriptor?, fileUri: Uri?)

    fun onFileSelected(fileDescriptor: FileDescriptor?)
}

class StorageAccessFrameworkInteractor(private val fileSelectionEntryPoint: FileSelectionEntryPoint) {

    private val createFileLauncher: ActivityResultLauncher<CreateFileParams> =
        fileSelectionEntryPoint.fileSelectionOwner
            .registerForActivityResult(CreateFileResultContract()) { uri ->
                onFileCreationFinished(uri)
            }

    private val selectFileLauncher: ActivityResultLauncher<SelectFileParams> =
        fileSelectionEntryPoint.fileSelectionOwner
            .registerForActivityResult(SelectFileResultContract()) { uri ->
                onFileSelectionFinished(uri)
            }

    fun beginCreatingFile(createFileParams: CreateFileParams) =
        createFileLauncher.launch(createFileParams)

    private fun onFileCreationFinished(fileUri: Uri?) {
        val fileDescriptor = fileUri?.let { uri ->
            fileSelectionEntryPoint.fileSelectionOwner
                //.requireContext()
                .contentResolver
                .openFileDescriptor(uri, "w")
                ?.fileDescriptor
        }

        fileSelectionEntryPoint.onFileCreated(fileDescriptor, fileUri)
    }

    private fun beginSelectingFile(selectFileParams: SelectFileParams) =
        selectFileLauncher.launch(selectFileParams)

    private fun onFileSelectionFinished(fileUri: Uri?) {
        val fileDescriptor = fileUri?.let { uri ->
            fileSelectionEntryPoint.fileSelectionOwner
                //.requireContext()
                .contentResolver
                .openFileDescriptor(uri, "r")
                ?.fileDescriptor
        }

        fileSelectionEntryPoint.onFileSelected(fileDescriptor)
    }
}

data class CreateFileParams(
    val fileMimeType: String,
    val fileExtension: String,
    val suggestedName: String
)

data class SelectFileParams(
    val fileMimeType: String
)

class CreateFileResultContract : ActivityResultContract<CreateFileParams, Uri?>() {

    override fun createIntent(context: Context, input: CreateFileParams): Intent =
        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setTypeAndNormalize(input.fileMimeType)
            putExtra(Intent.EXTRA_TITLE, "${input.suggestedName}.${input.fileExtension}")
        }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? = when (resultCode) {
        Activity.RESULT_OK -> intent?.data
        else -> null
    }
}

class SelectFileResultContract : ActivityResultContract<SelectFileParams, Uri?>() {

    override fun createIntent(context: Context, input: SelectFileParams): Intent =
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setTypeAndNormalize(input.fileMimeType)
        }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? = when (resultCode) {
        Activity.RESULT_OK -> intent?.data
        else -> null
    }
}