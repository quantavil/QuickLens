package com.quantavil.quicklens.data

enum class SearchEngine(
    val displayName: String,
    val urlTemplate: String
) {
    BING(
        "Bing",
        "https://www.bing.com/images/search?view=detailv2&iss=sbi&q=imgurl:{imageUrl}"
    ),
    GOOGLE_LENS(
        "Lens",
        "https://lens.google.com/uploadbyurl?url={imageUrl}"
    ),
    YANDEX(
        "Yandex",
        "https://yandex.com/images/search?rpt=imageview&url={imageUrl}"
    ),
    TINEYE(
        "TinEye",
        "https://tineye.com/search?url={imageUrl}"
    );
}
