package com.twapps.serverstatuschecker

import io.kvision.core.AlignItems
import io.kvision.core.Background
import io.kvision.core.Color
import io.kvision.core.Container
import io.kvision.core.JustifyItems
import io.kvision.form.formPanel
import io.kvision.form.text.Text
import io.kvision.html.button
import io.kvision.html.div
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
                        updateStatus()
                    }
                }
            }

            div().bind(state.sub { it.statuses }) { statuses ->
                when (statuses) {
                    null -> {
                        span("Loading...")
                        updateStatus()
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
            }
        }
        tbody {
            statuses.forEach {
                tr {
                    td {
                        +it.first
                    }
                    td {
                        +it.second.toOutput()
                    }
                }
            }
        }
    }


    private fun addUrl(formInput: FormInput) {
        AppScope.launch {
            val serverResponse = Model.addUrl(formInput.url)

            if (serverResponse is Failable.Success) {
                Toast.success(serverResponse.value)
                updateStatus()
            } else if (serverResponse is Failable.Failure){
                Toast.danger(serverResponse.error ?: "Unknown error")
            }
        }
    }
    private fun updateStatus() {
        AppScope.launch {
            val serverResponse = Model.getStatusList()
            state.value = state.value.copy(statuses = serverResponse)
        }
    }

    private fun ServerStatus.toOutput(): String {
        return when {
            error != null -> "ERROR: $error"
            response != null -> "OK: $response"
            else -> "???"
        }
    }
}