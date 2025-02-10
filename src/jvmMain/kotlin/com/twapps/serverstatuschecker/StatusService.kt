package com.twapps.serverstatuschecker

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class StatusService constructor(
    private val serverState: ServerState
) : IStatusService {

    // TODO exception
    /*
    Check if class with serial name 'String' exists and serializer is registered in a corresponding SerializersModule.
    To be registered automatically, class 'String' has to be '@Serializable', and the base class 'Any' has to be sealed and '@Serializable'.
    kotlinx.serialization.SerializationException: Serializer for subclass 'String' is not found in the polymorphic scope of 'Any'.
    Check if class with serial name 'String' exists and serializer is registered in a corresponding SerializersModule.
    To be registered automatically, class 'String' has to be '@Serializable', and the base class 'Any' has to be sealed and '@Serializable'.
	at kotlinx.serialization.internal.AbstractPolymorphicSerializerKt.throwSubtypeNotRegistered(AbstractPolymorphicSerializer.kt:102)
	at kotlinx.serialization.internal.AbstractPolymorphicSerializerKt.throwSubtypeNotRegistered(AbstractPolymorphicSerializer.kt:114)
	at kotlinx.serialization.PolymorphicSerializerKt.findPolymorphicSerializer(PolymorphicSerializer.kt:109)
	at kotlinx.serialization.json.internal.StreamingJsonEncoder.encodeSerializableValue(StreamingJsonEncoder.kt:242)
	at kotlinx.serialization.encoding.AbstractEncoder.encodeSerializableElement(AbstractEncoder.kt:80)
	at com.twapps.serverstatuschecker.Failable$Success.write$Self$serverstatuschecker(Failable.kt:9)
	at com.twapps.serverstatuschecker.Failable$Success$$serializer.serialize(Failable.kt:9)
	at com.twapps.serverstatuschecker.Failable$Success$$serializer.serialize(Failable.kt:9)
	at kotlinx.serialization.json.internal.StreamingJsonEncoder.encodeSerializableValue(StreamingJsonEncoder.kt:249)
     */

    override suspend fun addUrl(url: String): Failable<String> {
        println("addUrl called with url=[$url]")
        if (serverState.urls.contains(url)) {
            println("$url already exists")
            return Failable.Failure("Already exists")
        } else {
            println("Adding $url")
            serverState.urls.add(url)
            serverState.checker.updateStatuses(serverState.urls, serverState.statuses)
            return Failable.Success("Added $url")
        }
    }

    override suspend fun getStatusList(): List<Pair<String, ServerStatus>> {
        return serverState.statuses.toList()
    }
}
