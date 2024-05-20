package com.github.kotlintelegrambot.updater

import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.RetrieveUpdatesError
import com.github.kotlintelegrambot.network.ApiClient
import com.github.kotlintelegrambot.types.DispatchableObject
import com.github.kotlintelegrambot.types.TelegramBotResult
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

internal class Updater(
    private val looper: Looper,
    private val updatesChannel: Channel<DispatchableObject>,
    private val apiClient: ApiClient,
    private val botTimeout: Int,
    private val gson: Gson
) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Volatile
    private var lastUpdateId: Long? = null

    internal fun startPolling() {
        looper.loop {
            val getUpdatesResult = apiClient.getUpdatesJson(
                offset = lastUpdateId,
                limit = null,
                timeout = botTimeout,
                allowedUpdates = null,
            )
            yield()
            getUpdatesResult.fold(
                ifSuccess = { onUpdatesReceived(parseUpdates(it)) },
                ifError = { onErrorGettingUpdates(it) },
            )
        }
    }

    private fun parseUpdates(result: List<JsonObject>): List<Update> {
        val updates = result.mapNotNull {
            try {
                gson.fromJson(it, Update::class.java)
            } catch (ex: Exception) {
                logger.error("Filed to parse update: $it", ex)
                null
            }
        }
        return updates
    }

    internal fun stopPolling() {
        looper.quit()
    }

    private suspend fun onUpdatesReceived(updates: List<Update>) {
        if (updates.isEmpty()) {
            return
        }

        updates.forEach {
            updatesChannel.send(it)
        }

        lastUpdateId = updates.last().updateId + 1
    }

    private suspend fun onErrorGettingUpdates(error: TelegramBotResult.Error) {
        val errorDescription: String? = when (error) {
            is TelegramBotResult.Error.HttpError -> "${error.httpCode} ${error.description}"
            is TelegramBotResult.Error.TelegramApi -> "${error.errorCode} ${error.description}"
            is TelegramBotResult.Error.InvalidResponse -> "${error.httpCode} ${error.httpStatusMessage}"
            is TelegramBotResult.Error.Unknown -> error.exception.message
        }

        val dispatchableError = RetrieveUpdatesError(
            errorDescription ?: "Error retrieving updates",
        )
        logger.error("Get updates failed: $dispatchableError ")
        updatesChannel.send(dispatchableError)
        delay(1.seconds)
    }
}
