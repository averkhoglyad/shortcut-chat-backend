package io.averkhoglyad.shortcut.message.api.data

data class PageResponse<E>(

    val data: List<E>,
    val elements: Long,
    val pages: Int,
    val size: Int,
    val current: Int,

)

data class SliceResponse<E>(

    val data: List<E>,
    val next: String?,

)
