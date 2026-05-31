package `is`.xyz.mpv

data class FileBrowserEntryDto(
    val name: String,
    val isDir: Boolean,
    val fileType: String,
    val extension: String,
    val createdAt: String,
    val modifiedAt: String,
    val filePath: String
)

data class FileBrowserResultsDto(
    val name: String,
    val fullPath: String,
    val parent: String?,
    val entries: List<FileBrowserEntryDto>
)
