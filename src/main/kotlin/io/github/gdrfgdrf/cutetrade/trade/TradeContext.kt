package io.github.gdrfgdrf.cutetrade.trade

import cutetrade.protobuf.CommonProto.TradeResult
import io.github.gdrfgdrf.cutetrade.common.TradeStatus
import io.github.gdrfgdrf.cutetrade.common.TraderState
import io.github.gdrfgdrf.cutetrade.extension.generateTradeId
import io.github.gdrfgdrf.cutetrade.extension.removeAllTags
import io.github.gdrfgdrf.cutetrade.manager.TradeManager
import io.github.gdrfgdrf.cutetrade.screen.TradeScreenContext
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import java.time.Instant

class TradeContext private constructor(
    val redPlayer: ServerPlayerEntity,
    val bluePlayer: ServerPlayerEntity
) {
    private var initialized = false

    lateinit var tradeId: String

    var startTime: Instant? = null
    var endTime: Instant? = null
    private lateinit var result: TradeResult
    private lateinit var status: TradeStatus

    private lateinit var tradeScreenContext: TradeScreenContext

    private lateinit var presenter: TradePresenter

    var redState: TraderState = TraderState.UNCHECKED
    var blueState: TraderState = TraderState.UNCHECKED
    lateinit var redTradeItemStack: TradeItemStack
    lateinit var blueTradeItemStack: TradeItemStack

    private var redAddTradeItem = false
    private var blueAddTradeItem = false

    var muteAddItemMessageTime: Long = -1

    private val tradeItemHistory = ArrayList<TradeItemStack.TradeItem>()

    fun initialize() {
        tradeId = generateTradeId()

        startTime = Instant.now()
        result = TradeResult.TRADE_RESULT_DEFAULT

        tradeScreenContext = TradeScreenContext.create(
            this,
            redPlayer,
            bluePlayer
        )
        tradeScreenContext.initialize()

        presenter = TradePresenter.create(this)
        presenter.initialize()

        redTradeItemStack = TradeItemStack.create(redPlayer)
        blueTradeItemStack = TradeItemStack.create(bluePlayer)

        initialized = true
        status = TradeStatus.INITIALIZED
    }

    fun start() {
        if (!initialized) {
            throw IllegalStateException("Trade is not initialized")
        }

        redPlayer.inventory.removeAllTags("cutetrade-add-by")
        bluePlayer.inventory.removeAllTags("cutetrade-add-by")

        presenter.start()
        TradeManager.tradeStart(this)
        tradeScreenContext.start()

        status = TradeStatus.STARTED

        updateRedState(
            TraderState.UNCHECKED,
            playSound = false,
            sendMessage = false
        )
        updateBlueState(
            TraderState.UNCHECKED,
            playSound = false,
            sendMessage = false
        )
        presenter.playStartSound()
    }

    fun updateRedState(
        redState: TraderState,
        playSound: Boolean = true,
        sendMessage: Boolean = true
    ) {
        if (redState == this.redState) {
            return
        }

        this.redState = redState
        presenter.updateState(redState, blueState)
        if (playSound) {
            if (redState == TraderState.CHECKED) {
                presenter.playStatePositiveSound()
            } else {
                presenter.playStateNegativeSound()
            }
        }
        if (sendMessage) {
            presenter.broadcastRedStateChange(redState)
        }
        checkState()
    }

    fun updateBlueState(
        blueState: TraderState,
        playSound: Boolean = true,
        sendMessage: Boolean = true
    ) {
        if (blueState == this.blueState) {
            return
        }

        this.blueState = blueState
        presenter.updateState(redState, blueState)
        if (playSound) {
            if (blueState == TraderState.CHECKED) {
                presenter.playStatePositiveSound()
            } else {
                presenter.playStateNegativeSound()
            }
        }
        if (sendMessage) {
            presenter.broadcastBlueStateChange(blueState)
        }
        checkState()
    }

    fun checkState() {
        if (redState == TraderState.CHECKED && blueState == TraderState.CHECKED) {
            finish()
        }
    }

    fun redAddTradeItem(
        index: Int,
        itemStack: ItemStack,
        playSound: Boolean = true,
        updateState: Boolean = true,
        broadcastMessage: Boolean = true,
        bypass: Boolean = false
    ) {
//        if (!bypass && redAddTradeItem) {
//            redAddTradeItem = false
//            return
//        }

//        tradeItemHistory.add(TradeItemStack.TradeItem(itemStack.copy()))
        redTradeItemStack.setTradeItem(index, itemStack)
        tradeScreenContext.syncTradeInventory()
        if (playSound) {
            presenter.playAddItemSound()
        }
        if (updateState) {
            updateRedState(TraderState.UNCHECKED)
            updateBlueState(TraderState.UNCHECKED)
        }
        if (broadcastMessage) {
            presenter.broadcastRedAddItemMessage(itemStack)
        }

//        redAddTradeItem = true
    }

    fun muteRedAddTradeItem(
        time: Long
    ) {
        presenter.muteRedAddTradeItemMessage(time)
    }

    fun blueAddTradeItem(
        index: Int,
        itemStack: ItemStack,
        playSound: Boolean = true,
        updateState: Boolean = true,
        broadcastMessage: Boolean = true,
        bypass: Boolean = false
    ) {
//        if (!bypass && blueAddTradeItem) {
//            blueAddTradeItem = false
//            return
//        }

//        tradeItemHistory.add(TradeItemStack.TradeItem(itemStack.copy()))
        blueTradeItemStack.setTradeItem(index, itemStack)
        tradeScreenContext.syncTradeInventory()
        if (playSound) {
            presenter.playAddItemSound()
        }
        if (updateState) {
            updateRedState(TraderState.UNCHECKED)
            updateBlueState(TraderState.UNCHECKED)
        }
        if (broadcastMessage) {
            presenter.broadcastBlueAddItem(itemStack)
        }

//        blueAddTradeItem = true
    }

    fun muteBlueAddTradeItem(
        time: Long
    ) {
        presenter.muteBlueAddTradeItemMessage(time)
    }

    fun redRemoveTradeItem(
        index: Int
    ) {
        val itemStack = redTradeItemStack.get(index)
        itemStack?.let {
            presenter.broadcastRedRemoveItemMessage(it.itemStack)
        }

        redTradeItemStack.removeTradeItem(index)
        tradeScreenContext.syncTradeInventory()
        presenter.playRemoveItemSound()

        updateRedState(TraderState.UNCHECKED)
        updateBlueState(TraderState.UNCHECKED)
    }

    fun blueRemoveTradeItem(
        index: Int
    ) {
        val itemStack = blueTradeItemStack.get(index)
        itemStack?.let {
            presenter.broadcastBlueRemoveItemMessage(it.itemStack)
        }

        blueTradeItemStack.removeTradeItem(index)
        tradeScreenContext.syncTradeInventory()
        presenter.playRemoveItemSound()

        updateRedState(TraderState.UNCHECKED)
        updateBlueState(TraderState.UNCHECKED)
    }

    fun terminate() {
        status = TradeStatus.TERMINATED
        end(false)
        presenter.playTerminateSound()
        presenter.broadcastTerminateMessage()
    }

    fun finish() {
        status = TradeStatus.FINISHED
        end(true)

        presenter.playFinishSound()
        presenter.broadcastFinishMessage()
    }

    fun end(
        normal: Boolean
    ) {
        if (!initialized) {
            throw IllegalStateException("Trade is not initialized")
        }
        endTime = Instant.now()
        tradeScreenContext.end()

        if (!normal) {
            redTradeItemStack.returnAll()
            blueTradeItemStack.returnAll()
        } else {
            redTradeItemStack.moveTo(bluePlayer)
            blueTradeItemStack.moveTo(redPlayer)
        }
        redTradeItemStack.removeAll()
        blueTradeItemStack.removeAll()

        redPlayer.inventory.removeAllTags("cutetrade-add-by")
        bluePlayer.inventory.removeAllTags("cutetrade-add-by")

        presenter.end()

        TradeManager.tradeEnd(this)
    }

    companion object {
        fun create(
            redPlayer: ServerPlayerEntity,
            bluePlayer: ServerPlayerEntity
        ): TradeContext = TradeContext(redPlayer, bluePlayer)
    }

}