package com.symphony.themeengine

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import io.paperdb.Paper

class ThemeEngine(val context: Context) {
    init {
        Paper.init(context)
    }

    fun getAllAccentColors(): MutableList<Int> {
        val accentColors = mutableListOf<Int>()
        //Red
        accentColors.add(Color.parseColor("#ff8a80"))
        accentColors.add(Color.parseColor("#ff5252"))
        accentColors.add(Color.parseColor("#ff1744"))
        accentColors.add(Color.parseColor("#d50000"))
        //Pink
        accentColors.add(Color.parseColor("#ff80ab"))
        accentColors.add(Color.parseColor("#ff4081"))
        accentColors.add(Color.parseColor("#f50057"))
        accentColors.add(Color.parseColor("#c51162"))
        //Purple
        accentColors.add(Color.parseColor("#ea80fc"))
        accentColors.add(Color.parseColor("#e040fb"))
        accentColors.add(Color.parseColor("#d500f9"))
        accentColors.add(Color.parseColor("#aa00ff"))
        //Deep Purple
        accentColors.add(Color.parseColor("#b388ff"))
        accentColors.add(Color.parseColor("#7c4dff"))
        accentColors.add(Color.parseColor("#651fff"))
        accentColors.add(Color.parseColor("#6200ea"))
        //Indigo
        accentColors.add(Color.parseColor("#8c9eff"))
        accentColors.add(Color.parseColor("#536dfe"))
        accentColors.add(Color.parseColor("#3d5afe"))
        accentColors.add(Color.parseColor("#304ffe"))
        //Blue
        accentColors.add(Color.parseColor("#82b1ff"))
        accentColors.add(Color.parseColor("#448aff"))
        accentColors.add(Color.parseColor("#2979ff"))
        accentColors.add(Color.parseColor("#2962ff"))
        //Light Blue
        accentColors.add(Color.parseColor("#80d8ff"))
        accentColors.add(Color.parseColor("#40c4ff"))
        accentColors.add(Color.parseColor("#00b0ff"))
        accentColors.add(Color.parseColor("#0091ea"))
        //Cyan
        accentColors.add(Color.parseColor("#84ffff"))
        accentColors.add(Color.parseColor("#18ffff"))
        accentColors.add(Color.parseColor("#00e5ff"))
        accentColors.add(Color.parseColor("#00b8d4"))
        //Teal
        accentColors.add(Color.parseColor("#a7ffeb"))
        accentColors.add(Color.parseColor("#64ffda"))
        accentColors.add(Color.parseColor("#1de9b6"))
        accentColors.add(Color.parseColor("#00bfa5"))
        //Green
        accentColors.add(Color.parseColor("#b9f6ca"))
        accentColors.add(Color.parseColor("#69f0ae"))
        accentColors.add(Color.parseColor("#00e676"))
        accentColors.add(Color.parseColor("#00c853"))
        //Light Green
        accentColors.add(Color.parseColor("#ccff90"))
        accentColors.add(Color.parseColor("#b2ff59"))
        accentColors.add(Color.parseColor("#76ff03"))
        accentColors.add(Color.parseColor("#64dd17"))
        //Lime
        accentColors.add(Color.parseColor("#f4ff81"))
        accentColors.add(Color.parseColor("#eeff41"))
        accentColors.add(Color.parseColor("#c6ff00"))
        accentColors.add(Color.parseColor("#aeea00"))
        //Yellow
        accentColors.add(Color.parseColor("#ffff8d"))
        accentColors.add(Color.parseColor("#ffff00"))
        accentColors.add(Color.parseColor("#ffea00"))
        accentColors.add(Color.parseColor("#ffd600"))
        //Amber
        accentColors.add(Color.parseColor("#ffe57f"))
        accentColors.add(Color.parseColor("#ffd740"))
        accentColors.add(Color.parseColor("#ffc400"))
        accentColors.add(Color.parseColor("#ffab00"))
        //Orange
        accentColors.add(Color.parseColor("#ffd180"))
        accentColors.add(Color.parseColor("#ffab40"))
        accentColors.add(Color.parseColor("#ff9100"))
        accentColors.add(Color.parseColor("#ff6d00"))
        //Deep Orange
        accentColors.add(Color.parseColor("#ff9e80"))
        accentColors.add(Color.parseColor("#ff6e40"))
        accentColors.add(Color.parseColor("#ff3d00"))
        accentColors.add(Color.parseColor("#dd2c00"))

        return accentColors
    }

    val selectableItemBackground: Int
        get() = with(context) {
            val outValue = TypedValue()
            context.theme
                .resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            return outValue.resourceId
        }

    val selectableItemBackgroundBorderless: Int
        get() = with(context) {
            val outValue = TypedValue()
            context.theme
                .resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
            return outValue.resourceId
        }

    val colorControlHighLight: Int
        get() = with(context) {
            val typedValue = TypedValue()
            val a =
                context.obtainStyledAttributes(
                    typedValue.data,
                    intArrayOf(R.attr.colorControlHighlight)
                )
            val color = a.getColor(0, 0)
            a.recycle()
            return color
        }

    var accentColor: Int
        get() = with(context) {
            val typedValue = TypedValue()
            val a =
                context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
            val color = a.getColor(0, 0)
            a.recycle()
            return color
        }
        set(accentColor) {
            Paper.book().write("accentColor", accentColor)
        }

    val textColorPrimary: Int
        get() = with(context) {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
            val arr = context.obtainStyledAttributes(
                typedValue.data, intArrayOf(
                    android.R.attr.textColorPrimary
                )
            )
            val color = arr.getColor(0, -1)
            arr.recycle()
            return color
        }

    val textColorSecondary: Int
        get() = with(context) {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true)
            val arr = context.obtainStyledAttributes(
                typedValue.data, intArrayOf(
                    android.R.attr.textColorSecondary
                )
            )
            val color = arr.getColor(0, -1)
            arr.recycle()
            return color
        }

    val primaryColor: Int
        get() = with(context) {
            val typedValue = TypedValue()
            val a =
                context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimary))
            val color = a.getColor(0, 0)
            a.recycle()
            return color
        }

    val backgroundColor: Int
        get() = with(context) {
            val typedValue = TypedValue()
            val a = context.obtainStyledAttributes(
                typedValue.data,
                intArrayOf(android.R.attr.windowBackground)
            )
            val color = a.getColor(0, 0)
            a.recycle()
            return color
        }

    var theme: Int
        get() {
            val theme: Int = Paper.book().read("theme", 2)
            val accentColor: Int = Paper.book().read("accentColor", 3)
            return try {
                val fieldName =
                    "Symphony_" + (if (theme == 1) "Light" else if (theme == 2) "Dark" else "Black") + "_Theme" + accentColor
                val obj: Any? = R.style::class.java.getField(fieldName).get(R.style::class.java)
                if (obj is Int) {
                    obj
                } else {
                    R.style.Symphony_Dark_Theme3
                }
            } catch (ignored: Exception) {
                R.style.Symphony_Dark_Theme3
            }
        }
        set(theme) {
            Paper.book().write("theme", theme)
        }
}