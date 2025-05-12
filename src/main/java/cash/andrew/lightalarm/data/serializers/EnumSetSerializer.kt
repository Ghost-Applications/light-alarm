package cash.andrew.lightalarm.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.EnumSet

class EnumSetSerializer<T : Enum<T>>(
    elementSerializer: KSerializer<T>
): KSerializer<EnumSet<T>> {

    private val setSerializer: KSerializer<Set<T>> = SetSerializer(elementSerializer)

    override val descriptor: SerialDescriptor =
        SerialDescriptor("EnumSet", setSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: EnumSet<T>
    ) {
        setSerializer.serialize(encoder, value.toSet())
    }

    override fun deserialize(decoder: Decoder): EnumSet<T> {
        val set = setSerializer.deserialize(decoder)
        return EnumSet.copyOf(set)
    }
}