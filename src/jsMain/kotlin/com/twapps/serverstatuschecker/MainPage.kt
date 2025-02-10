package com.twapps.serverstatuschecker

import com.twapps.serverstatuschecker.Failable.Success.Companion.toSuccess
import com.twapps.serverstatuschecker.MainPage.toOutput
import io.kvision.core.AlignItems
import io.kvision.core.Background
import io.kvision.core.Color
import io.kvision.core.Container
import io.kvision.core.JustifyItems
import io.kvision.core.onClick
import io.kvision.form.formPanel
import io.kvision.form.text.Text
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.icon
import io.kvision.html.span
import io.kvision.html.table
import io.kvision.html.tbody
import io.kvision.html.td
import io.kvision.html.th
import io.kvision.html.thead
import io.kvision.html.tr
import io.kvision.panel.hPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.toast.Toast
import io.kvision.utils.auto
import io.kvision.utils.px
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

object MainPage {

    data class State constructor(
        val statuses: Failable<List<Pair<String, ServerStatus>>>? = null
    )

    @Serializable
    data class FormInput constructor(
        val url: String
    )

    val state = ObservableValue(State())

    fun Container.mainPage() {


        div(className = "card") {
            background = Background(Color.hex(0xfafafa))
            maxWidth = 800.px
            minWidth = 500.px
            minHeight = 300.px

            margin = auto
            marginTop = 16.px

            padding = 32.px

            hPanel {
                alignItems = AlignItems.CENTER
                justifyItems = JustifyItems.CENTER
                gridColumnGap = 16

                val formPanel = formPanel<FormInput> {
                    add(
                        FormInput::url,
                        Text(label = "URL"),
                        required = true
                    )
                }
                button("OK") {
                    onClick {
                        if (formPanel.validate()) {
                            addUrl(formPanel.getData())
                        }
                    }
                }

                button("Refresh") {
                    onClick {
                        updateAllStatuses()
                    }
                }
            }

            div().bind(state.sub { it.statuses }) { statuses ->
                when (statuses) {
                    null -> {
                        span("Loading...")
                        updateAllStatuses()
                    }

                    is Failable.Failure -> span("Error: ${statuses.error}")
                    is Failable.Success -> statusTable(statuses.value)
                }
            }
        }
    }

    private fun Container.statusTable(statuses: List<Pair<String, ServerStatus>>) = table("table table-hover") {
        thead {
            tr {
                th { +"URL" }
                th { +"Status" }
                th {
                    setAttribute("colspan", "2")
                }
            }
        }
        tbody {
            statuses.forEach { (url, status) ->
                tr {
                    td {
                        +url
                    }
                    td {
                        +status.toOutput()
                    }
                    td {
                        icon("fas fa-copy") {
                            role = "button"
                            onClick { copyUrl(url) }
                        }
                    }
                    td {
                        icon("fas fa-trash-can") {
                            role = "button"
                            onClick { removeUrl(url) }
                        }
                    }
                }
            }
        }
    }


    private fun addUrl(formInput: FormInput) {
        AppScope.launch {
            val url = formInput.url
            val serverResponse = Model.addUrl(url)

            if (serverResponse is Failable.Success) {
                val addedUrl = serverResponse.value

                Toast.success("Added $addedUrl")

                (state.value.statuses as? Failable.Success)?.value?.let { statusesBefore ->
                    val newStatuses = (statusesBefore + (addedUrl to ServerStatus(null, null, Response(200, "")))).toSuccess()
                    state.value = state.value.copy(statuses = newStatuses)
                }

                delay(2_000)
                updateAllStatuses()
            } else if (serverResponse is Failable.Failure){
                Toast.danger(serverResponse.error ?: "Unknown error")
            }
        }
    }

    private fun updateAllStatuses() {
        AppScope.launch {
            val serverResponse = Model.getStatusList()
            state.value = state.value.copy(statuses = serverResponse)
        }
    }

    private fun removeUrl(url: String) {
        AppScope.launch {
            val serverResponse = Model.removeUrl(url)

            if (serverResponse is Failable.Success) {
                (state.value.statuses as? Failable.Success)?.value?.let { statusesBefore ->
                    val newStatuses = statusesBefore.filter { it.first != url }.toSuccess()
                    state.value = state.value.copy(statuses = newStatuses)
                }
                Toast.success("${serverResponse.value} removed")
            } else if (serverResponse is Failable.Failure){
                Toast.danger(serverResponse.error ?: "Unknown error")
            }
        }
    }

    private fun copyUrl(url: String) {
        window.navigator.clipboard.writeText(url)
        Toast.success("Copied \"$url\" to clipboard")
    }

    private fun ServerStatus.toOutput(): String {
        return when {
            error != null -> "\uD83D\uDD34 $error"
            isOk() -> "\uD83D\uDFE2 ${response?.toOutput()}"
            response != null -> "\uD83D\uDFE0  ${response.toOutput()}"
            else -> "\uD83D\uDFE0"
        }
    }

    private fun Response.toOutput() = "$message ($code)"
}