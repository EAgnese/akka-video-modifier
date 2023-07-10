package com.ddm.app.businesslogic.serialization;


import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public interface AkkaSerializable extends Serializable {
}
