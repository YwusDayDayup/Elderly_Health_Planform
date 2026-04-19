package com.example.service.external;

import java.math.BigDecimal;

public interface MapGateway {

  String resolveAddress(BigDecimal latitude, BigDecimal longitude, String fallbackAddress);

  boolean withinZone(BigDecimal latitude, BigDecimal longitude, BigDecimal centerLatitude,
      BigDecimal centerLongitude, Integer radiusMeters);
}
