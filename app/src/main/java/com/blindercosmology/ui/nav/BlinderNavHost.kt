package com.blindercosmology.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blindercosmology.data.ProfileRepository
import com.blindercosmology.data.toBirthInfo
import com.blindercosmology.ui.screens.ChartScreen
import com.blindercosmology.ui.screens.DailyScreen
import com.blindercosmology.ui.screens.HomeScreen
import com.blindercosmology.ui.screens.InputScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val HOME = "home"
    const val INPUT = "input?mainUser={mainUser}"
    const val CHART = "chart/{y}/{m}/{d}/{hr}/{min}/{tz}/{lat}/{lng}/{name}"
    const val DAILY = "daily"

    fun input(mainUser: Boolean) = "input?mainUser=$mainUser"
    fun chart(
        y: Int, m: Int, d: Int, hr: Int, min: Int, tz: Double,
        lat: Double, lng: Double, name: String
    ): String {
        val safeName = URLEncoder.encode(name.ifBlank { "Guest" }, StandardCharsets.UTF_8.name())
        return "chart/$y/$m/$d/$hr/$min/$tz/$lat/$lng/$safeName"
    }
}

@Composable
fun BlinderNavHost(repo: ProfileRepository) {
    val nav = rememberNavController()
    val mainUser by repo.mainUser.collectAsState(initial = null)

    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                mainUser = mainUser,
                onOpenMainUserChart = {
                    mainUser?.let {
                        nav.navigate(
                            Routes.chart(
                                it.year, it.month, it.day, it.hour, it.minute,
                                it.utcOffsetHours, it.latitude, it.longitude, it.fullName,
                            )
                        )
                    }
                },
                onSetupMainUser = { nav.navigate(Routes.input(true)) },
                onGuestReading  = { nav.navigate(Routes.input(false)) },
                onOpenDaily     = { nav.navigate(Routes.DAILY) },
            )
        }
        composable(
            Routes.INPUT,
            arguments = listOf(navArgument("mainUser") {
                type = NavType.BoolType
                defaultValue = false
            }),
        ) { entry ->
            val isMain = entry.arguments?.getBoolean("mainUser") ?: false
            InputScreen(
                repo = repo,
                isMainUser = isMain,
                onChartReady = { name, info ->
                    nav.navigate(
                        Routes.chart(
                            info.year, info.month, info.day, info.hour, info.minute,
                            info.utcOffsetHours, info.latitude, info.longitude, name,
                        )
                    )
                },
                onBack = { nav.popBackStack() },
            )
        }
        composable(Routes.CHART) { entry ->
            val args = entry.arguments!!
            val info = com.blindercosmology.astro.BirthInfo(
                year   = args.getString("y")!!.toInt(),
                month  = args.getString("m")!!.toInt(),
                day    = args.getString("d")!!.toInt(),
                hour   = args.getString("hr")!!.toInt(),
                minute = args.getString("min")!!.toInt(),
                utcOffsetHours = args.getString("tz")!!.toDouble(),
                latitude  = args.getString("lat")!!.toDouble(),
                longitude = args.getString("lng")!!.toDouble(),
                placeLabel = "",
            )
            val name = URLDecoder.decode(args.getString("name") ?: "Guest", StandardCharsets.UTF_8.name())
            ChartScreen(
                fullName = name,
                info = info,
                onBack = { nav.popBackStack() },
            )
        }
        composable(Routes.DAILY) {
            val user = mainUser
            DailyScreen(
                profile = user,
                onBack = { nav.popBackStack() },
                onSetupMainUser = { nav.navigate(Routes.input(true)) },
            )
        }
    }
}
