/*
 * (C) Copyright IBM Corp. 2019
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.fhir.model.type.code;

import com.ibm.fhir.model.type.Code;
import com.ibm.fhir.model.type.Extension;
import com.ibm.fhir.model.type.String;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

@Generated("com.ibm.fhir.tools.CodeGenerator")
public class StructureMapGroupTypeMode extends Code {
    /**
     * Not a Default
     */
    public static final StructureMapGroupTypeMode NONE = StructureMapGroupTypeMode.builder().value(ValueSet.NONE).build();

    /**
     * Default for Type Combination
     */
    public static final StructureMapGroupTypeMode TYPES = StructureMapGroupTypeMode.builder().value(ValueSet.TYPES).build();

    /**
     * Default for type + combination
     */
    public static final StructureMapGroupTypeMode TYPE_AND_TYPES = StructureMapGroupTypeMode.builder().value(ValueSet.TYPE_AND_TYPES).build();

    private volatile int hashCode;

    private StructureMapGroupTypeMode(Builder builder) {
        super(builder);
    }

    public static StructureMapGroupTypeMode of(ValueSet value) {
        switch (value) {
        case NONE:
            return NONE;
        case TYPES:
            return TYPES;
        case TYPE_AND_TYPES:
            return TYPE_AND_TYPES;
        default:
            throw new IllegalArgumentException(value.name());
        }
    }

    public static StructureMapGroupTypeMode of(java.lang.String value) {
        return of(ValueSet.valueOf(value));
    }

    public static String string(java.lang.String value) {
        return of(ValueSet.valueOf(value));
    }

    public static Code code(java.lang.String value) {
        return of(ValueSet.valueOf(value));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StructureMapGroupTypeMode other = (StructureMapGroupTypeMode) obj;
        return Objects.equals(id, other.id) && Objects.equals(extension, other.extension) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, extension, value);
            hashCode = result;
        }
        return result;
    }

    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.id(id);
        builder.extension(extension);
        builder.value(value);
        return builder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Code.Builder {
        private Builder() {
            super();
        }

        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder value(java.lang.String value) {
            return (value != null) ? (Builder) super.value(ValueSet.from(value).value()) : this;
        }

        public Builder value(ValueSet value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public StructureMapGroupTypeMode build() {
            return new StructureMapGroupTypeMode(this);
        }
    }

    public enum ValueSet {
        /**
         * Not a Default
         */
        NONE("none"),

        /**
         * Default for Type Combination
         */
        TYPES("types"),

        /**
         * Default for type + combination
         */
        TYPE_AND_TYPES("type-and-types");

        private final java.lang.String value;

        ValueSet(java.lang.String value) {
            this.value = value;
        }

        public java.lang.String value() {
            return value;
        }

        public static ValueSet from(java.lang.String value) {
            for (ValueSet c : ValueSet.values()) {
                if (c.value.equals(value)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(value);
        }
    }
}
