package com.milaboratory.primitivio;

import java.lang.reflect.Array;

public class DefaultSerializersProviderImpl implements DefaultSerializersProvider {
    @Override
    public Serializer createSerializer(Class<?> type, SerializersManager manager) {
        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            if (componentType.isPrimitive()) {
                return null;
            } else {
                return new ArraySerializer(manager.getSerializer(componentType) == null ? null : componentType);
            }
        }

        if (type == Integer.class)
            return new IntegerSerializer();

        return null;
    }

    private static class IntegerSerializer implements Serializer<Integer> {
        @Override
        public void write(PrimitivO output, Integer object) {
            output.writeInt(object);
        }

        @Override
        public Integer read(PrimitivI input) {
            return input.readInt();
        }

        @Override
        public boolean isReference() {
            return false;
        }

        @Override
        public boolean handlesReference() {
            return false;
        }
    }

    private static class ArraySerializer implements Serializer {
        final Class<?> componentType;

        private ArraySerializer(Class<?> componentType) {
            this.componentType = componentType;
        }

        @Override
        public void write(PrimitivO output, Object object) {
            int length = Array.getLength(object);
            output.writeVarInt(length);
            for (int i = 0; i < length; i++) {
                if (componentType == null)
                    output.writeObject(Array.get(object, i));
                else
                    output.writeObject(Array.get(object, i), componentType);
            }
        }

        @Override
        public Object read(PrimitivI input) {
            int length = input.readVarInt();

            if (length == 0)
                return new Object[0];

            if (componentType == null)
                throw new RuntimeException("Unknown array type.");

            Object array = Array.newInstance(componentType, length);

            for (int i = 0; i < length; i++)
                Array.set(array, i, input.readObject(componentType));

            return array;
        }

        @Override
        public boolean isReference() {
            return true;
        }

        @Override
        public boolean handlesReference() {
            return false;
        }
    }
}
