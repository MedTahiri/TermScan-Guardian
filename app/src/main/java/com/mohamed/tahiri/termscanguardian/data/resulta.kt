package com.mohamed.tahiri.termscanguardian.data

data class Resulta(
    var summary: String,
    var sections: List<Section>
)

data class Section(
    var id: Int,
    var title: String,
    var content: String,
    var risk: String,
)
