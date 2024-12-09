package ru.yarsu.swg

private const val RIVER_SAND_DENSITY = 1.5
private const val QUARRY_SAND_DENSITY = 1.5
private const val CRUSHED_GRANITE_DENSITY = 1.4
private const val CRUSHED_STONE_GRAVEL_DENSITY = 1.43
private const val CRUSHED_SLAG_STONE_DENSITY = 1.17
private const val SAND_GRAVEL_MIXTURE_DENSITY = 1.6

enum class SWGType(
    var description: String,
    var density: Double,
) {
    RIVER_SAND("Песок речной", RIVER_SAND_DENSITY),
    QUARRY_SAND("Песок карьерный", QUARRY_SAND_DENSITY),
    CRUSHED_GRANITE("Щебень гранитный", CRUSHED_GRANITE_DENSITY),
    CRUSHED_STONE_GRAVEL("Щебень гравийный", CRUSHED_STONE_GRAVEL_DENSITY),
    CRUSHED_SLAG_STONE("Щебень шлаковый", CRUSHED_SLAG_STONE_DENSITY),
    SAND_GRAVEL_MIXTURE("Песчано-гравийная смесь", SAND_GRAVEL_MIXTURE_DENSITY),
}
