package com.symphony.projjal.exoplayer

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.source.ShuffleOrder
import java.util.*

class SymphonyShuffleOrder private constructor(
    val shuffled: IntArray,
    private val random: Random
) :
    ShuffleOrder {
    val indexInShuffled: IntArray = IntArray(shuffled.size)

    constructor(length: Int) : this(length, Random())

    private constructor(length: Int, random: Random) : this(
        createShuffledList(length, random),
        random
    )

    override fun getLength(): Int {
        return shuffled.size
    }

    override fun getNextIndex(index: Int): Int {
        var shuffledIndex = indexInShuffled[index]
        return if (++shuffledIndex < shuffled.size) shuffled[shuffledIndex] else C.INDEX_UNSET
    }

    override fun getPreviousIndex(index: Int): Int {
        var shuffledIndex = indexInShuffled[index]
        return if (--shuffledIndex >= 0) shuffled[shuffledIndex] else C.INDEX_UNSET
    }

    override fun getLastIndex(): Int {
        return if (shuffled.isNotEmpty()) shuffled[shuffled.size - 1] else C.INDEX_UNSET
    }

    override fun getFirstIndex(): Int {
        return if (shuffled.isNotEmpty()) shuffled[0] else C.INDEX_UNSET
    }

    override fun cloneAndInsert(insertionIndex: Int, insertionCount: Int): ShuffleOrder {
        val insertionPoints = IntArray(insertionCount)
        val insertionValues = IntArray(insertionCount)
        for (i in 0 until insertionCount) {
            insertionPoints[i] = random.nextInt(shuffled.size + 1)
            val swapIndex = random.nextInt(i + 1)
            insertionValues[i] = insertionValues[swapIndex]
            insertionValues[swapIndex] = i + insertionIndex
        }
        Arrays.sort(insertionPoints)
        val newShuffled = IntArray(shuffled.size + insertionCount)
        var indexInOldShuffled = 0
        var indexInInsertionList = 0
        for (i in 0 until shuffled.size + insertionCount) {
            if (indexInInsertionList < insertionCount
                && indexInOldShuffled == insertionPoints[indexInInsertionList]
            ) {
                newShuffled[i] = insertionValues[indexInInsertionList++]
            } else {
                newShuffled[i] = shuffled[indexInOldShuffled++]
                if (newShuffled[i] >= insertionIndex) {
                    newShuffled[i] += insertionCount
                }
            }
        }
        return SymphonyShuffleOrder(newShuffled, Random(random.nextLong()))
    }

    override fun cloneAndRemove(indexFrom: Int, indexToExclusive: Int): ShuffleOrder {
        val numberOfElementsToRemove = indexToExclusive - indexFrom
        val newShuffled = IntArray(shuffled.size - numberOfElementsToRemove)
        var foundElementsCount = 0
        for (i in shuffled.indices) {
            if (shuffled[i] in indexFrom until indexToExclusive) {
                foundElementsCount++
            } else {
                newShuffled[i - foundElementsCount] =
                    if (shuffled[i] >= indexFrom) shuffled[i] - numberOfElementsToRemove else shuffled[i]
            }
        }
        return SymphonyShuffleOrder(newShuffled, Random(random.nextLong()))
    }

    override fun cloneAndClear(): ShuffleOrder {
        return SymphonyShuffleOrder(0)
    }

    companion object {
        private fun createShuffledList(length: Int, random: Random): IntArray {
            val shuffled = IntArray(length)
            for (i in 0 until length) {
                val swapIndex = random.nextInt(i + 1)
                shuffled[i] = shuffled[swapIndex]
                shuffled[swapIndex] = i
            }
            return shuffled
        }
    }

    init {
        for (i in shuffled.indices) {
            indexInShuffled[shuffled[i]] = i
        }
    }
}