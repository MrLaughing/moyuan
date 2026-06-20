package com.mrlaughing.moyuan.engine.unlock

import com.mrlaughing.moyuan.data.local.db.entity.GardenMetaEntity
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import com.mrlaughing.moyuan.data.model.GrowthLevel
import com.mrlaughing.moyuan.data.model.PlantPath
import com.mrlaughing.moyuan.data.model.WitherStage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnlockEngine @Inject constructor() {

    private data class UnlockablePlant(
        val plantId: String,
        val path: String,
        val thresholdMinutes: Int
    )

    // 可解锁植物列表，按累计阅读总时长解锁
    private val unlockablePlants = listOf(
        UnlockablePlant("plant_jimo_bloom", PlantPath.JIMO.name, 600),
        UnlockablePlant("plant_bingzhu_lantern", PlantPath.BINGZHU.name, 1200),
        UnlockablePlant("plant_suihan_pine", PlantPath.SUIHAN.name, 2400),
        UnlockablePlant("plant_xunfang_plum", PlantPath.XUNFANG.name, 4800),
        UnlockablePlant("plant_jimo_ancient", PlantPath.JIMO.name, 8000),
        UnlockablePlant("plant_bingzhu_sage", PlantPath.BINGZHU.name, 12000)
    )

    fun checkAndUnlock(meta: GardenMetaEntity, plants: List<PlantStateEntity>): List<PlantStateEntity> {
        if (plants.isEmpty()) {
            return getStarterPlants(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
        }

        val totalMinutes = plants.sumOf { it.accumulatedMinutes }
        val existingIds = plants.map { it.plantId }.toSet()
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        val newlyUnlocked = unlockablePlants
            .filter { totalMinutes >= it.thresholdMinutes && it.plantId !in existingIds }
            .map { unlockable ->
                PlantStateEntity(
                    plantId = unlockable.plantId,
                    path = unlockable.path,
                    level = GrowthLevel.LV1.name,
                    witherStage = WitherStage.NONE.name,
                    unlockedDate = today
                )
            }

        return if (newlyUnlocked.isNotEmpty()) plants + newlyUnlocked else plants
    }

    fun getStarterPlants(date: String): List<PlantStateEntity> {
        return listOf(
            PlantStateEntity(plantId = "plant_jimo_seed", path = PlantPath.JIMO.name, level = GrowthLevel.LV1.name, witherStage = WitherStage.NONE.name, unlockedDate = date),
            PlantStateEntity(plantId = "plant_bingzhu_seed", path = PlantPath.BINGZHU.name, level = GrowthLevel.LV1.name, witherStage = WitherStage.NONE.name, unlockedDate = date),
            PlantStateEntity(plantId = "plant_suihan_seed", path = PlantPath.SUIHAN.name, level = GrowthLevel.LV1.name, witherStage = WitherStage.NONE.name, unlockedDate = date),
            PlantStateEntity(plantId = "plant_xunfang_seed", path = PlantPath.XUNFANG.name, level = GrowthLevel.LV1.name, witherStage = WitherStage.NONE.name, unlockedDate = date)
        )
    }
}
